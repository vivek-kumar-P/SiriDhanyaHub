package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.FeedbackDao
import com.mindmatrix.siridhanyahub.data.local.dao.FarmerStockListingDao
import com.mindmatrix.siridhanyahub.data.local.dao.TransactionRecordDao
import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.FeedbackEntity
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.transaction.TransactionType
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionRecordDao: TransactionRecordDao,
    private val feedbackDao: FeedbackDao,
    private val farmerStockListingDao: FarmerStockListingDao,
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository,
    private val firestoreRepo: FirestoreMarketplaceRepository
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val currentUserTransactions: StateFlow<List<TransactionRecordEntity>> =
        authRepository.currentUser
            .flatMapLatest { session ->
                if (session == null) flowOf(emptyList())
                else transactionRecordDao.observeByUserId(session.uid)
            }
            .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    fun observeFeedbackForFarmer(farmerUserId: String) =
        feedbackDao.observeByFarmer(farmerUserId)

    fun observeFeedbackForConsumer(consumerUserId: String) =
        feedbackDao.observeByConsumer(consumerUserId)

    // Called when consumer marks request fulfilled
    suspend fun recordFulfillment(
        farmerUserId: String,
        farmerName: String,
        milletType: String,
        quantityKg: Int,
        pricePerQuintal: Double,
        marketPlace: String
    ): Result<Int> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val consumerProfile = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Complete your profile first"))

        val totalAmount = (quantityKg / 100.0) * pricePerQuintal

        // 1. Record transaction for consumer (BUY)
        val consumerRecord = TransactionRecordEntity(
            userId = session.uid,
            role = UserRole.CONSUMER.value,
            transactionType = TransactionType.BUY.value,
            milletType = milletType,
            quantityKg = quantityKg,
            createdAt = System.currentTimeMillis(),
            counterpartyName = farmerName,
            referenceContext = marketPlace,
            amountEstimate = totalAmount
        )
        transactionRecordDao.insert(consumerRecord)

        // 2. Record transaction for farmer (SELL)
        val farmerRecord = TransactionRecordEntity(
            userId = farmerUserId,
            role = UserRole.FARMER.value,
            transactionType = TransactionType.SELL.value,
            milletType = milletType,
            quantityKg = quantityKg,
            createdAt = System.currentTimeMillis(),
            counterpartyName = consumerProfile.fullName,
            referenceContext = marketPlace,
            amountEstimate = totalAmount
        )
        transactionRecordDao.insert(farmerRecord)

        // 3. Deduct farmer stock + adjust rate
        val listing = farmerStockListingDao.getByFarmer(farmerUserId)
        if (listing != null) {
            val newQuantity = (listing.quantityAvailableKg - quantityKg).coerceAtLeast(0)
            val newRate = adjustedRate(
                originalQuantity = listing.quantityAvailableKg,
                remainingQuantity = newQuantity,
                currentRate = listing.selectedMarketRate
            )
            farmerStockListingDao.upsert(
                listing.copy(
                    quantityAvailableKg = newQuantity,
                    selectedMarketRate = newRate,
                    isActive = newQuantity > 0,
                    updatedAt = System.currentTimeMillis()
                )
            )
            // Sync to Firebase
            firestoreRepo.updateFarmerQuantityAndRate(
                farmerUserId = farmerUserId,
                newQuantity = newQuantity,
                newRate = newRate
            )
        }

        return Result.success(consumerRecord.id)
    }

    // Called after fulfillment to save feedback
    suspend fun submitFeedback(
        transactionId: Int,
        farmerUserId: String,
        farmerName: String,
        milletType: String,
        quantityKg: Int,
        rating: Int,
        comment: String
    ): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val consumerProfile = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Profile not found"))

        // Save to Room
        feedbackDao.insert(
            FeedbackEntity(
                transactionId = transactionId,
                farmerUserId = farmerUserId,
                consumerUserId = session.uid,
                farmerName = farmerName,
                consumerName = consumerProfile.fullName,
                milletType = milletType,
                quantityKg = quantityKg,
                rating = rating,
                comment = comment
            )
        )

        // Sync to Firebase so farmer sees it
        firestoreRepo.publishFeedbackToFirebase(
            farmerUserId = farmerUserId,
            consumerName = consumerProfile.fullName,
            rating = rating,
            comment = comment,
            milletType = milletType,
            quantityKg = quantityKg
        )

        return Result.success(Unit)
    }

    suspend fun record(
        role: UserRole,
        type: TransactionType,
        milletType: String,
        quantityKg: Int,
        counterpartyName: String,
        referenceContext: String,
        amountEstimate: Double? = null
    ): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        transactionRecordDao.insert(
            TransactionRecordEntity(
                userId = session.uid,
                role = role.value,
                transactionType = type.value,
                milletType = milletType,
                quantityKg = quantityKg,
                createdAt = System.currentTimeMillis(),
                counterpartyName = counterpartyName,
                referenceContext = referenceContext,
                amountEstimate = amountEstimate
            )
        )
        return Result.success(Unit)
    }

    // Rate adjustment based on remaining stock
    private fun adjustedRate(
        originalQuantity: Int,
        remainingQuantity: Int,
        currentRate: Double
    ): Double {
        if (originalQuantity == 0) return currentRate
        val remainingRatio = remainingQuantity.toDouble() / originalQuantity.toDouble()
        return when {
            remainingRatio < 0.20 -> currentRate * 1.10  // < 20% left → +10%
            remainingRatio < 0.50 -> currentRate * 1.05  // < 50% left → +5%
            remainingRatio > 0.80 -> currentRate * 0.98  // > 80% left → -2%
            else -> currentRate
        }
    }
}