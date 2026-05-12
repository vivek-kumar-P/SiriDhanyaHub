package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.ConsumerMilletRequestDao
import com.mindmatrix.siridhanyahub.data.local.dao.FarmerRequestMatchDao
import com.mindmatrix.siridhanyahub.data.local.dao.FarmerStockListingDao
import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerRequestMatchEntity
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerStockListingEntity
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.local.model.FarmerInboxItem
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

data class FarmerStockDraft(
    val milletType: String = "",
    val quantityAvailableKg: String = "",
    val growthDurationDays: String = "",
    val grownArea: String = "",
    val stockNote: String = ""
)

data class ConsumerRequestDraft(
    val milletType: String = "",
    val quantityKg: String = "",
    val neededDate: String = "",
    val neededTime: String = "",
    val consumerLocation: String = "",
    val preferredSourceLocation: String = "",
    val purpose: String = ""
)

data class MandiOption(
    val marketCity: String,
    val marketPlace: String,
    val milletType: String,
    val selectedRate: Double
)

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class MarketplaceRepository @Inject constructor(
    private val farmerStockListingDao: FarmerStockListingDao,
    private val consumerMilletRequestDao: ConsumerMilletRequestDao,
    private val farmerRequestMatchDao: FarmerRequestMatchDao,
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _farmerDraft = MutableStateFlow(FarmerStockDraft())
    val farmerDraft: StateFlow<FarmerStockDraft> = _farmerDraft.asStateFlow()

    private val _consumerDraft = MutableStateFlow(ConsumerRequestDraft())
    val consumerDraft: StateFlow<ConsumerRequestDraft> = _consumerDraft.asStateFlow()

    val activeFarmerListing: StateFlow<FarmerStockListingEntity?> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(null) else farmerStockListingDao.observeByFarmer(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, null)

    val activeConsumerRequest: StateFlow<ConsumerMilletRequestEntity?> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(null) else consumerMilletRequestDao.observeActiveByConsumer(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, null)

    val farmerInbox: StateFlow<List<FarmerInboxItem>> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(emptyList()) else farmerRequestMatchDao.observeInboxForFarmer(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    fun updateFarmerDraft(transform: (FarmerStockDraft) -> FarmerStockDraft) {
        _farmerDraft.value = transform(_farmerDraft.value)
    }

    fun updateConsumerDraft(transform: (ConsumerRequestDraft) -> ConsumerRequestDraft) {
        _consumerDraft.value = transform(_consumerDraft.value)
    }

    fun hydrateFarmerDraft(listing: FarmerStockListingEntity?) {
        if (listing == null) return
        _farmerDraft.value = FarmerStockDraft(
            milletType = listing.milletType,
            quantityAvailableKg = listing.quantityAvailableKg.toString(),
            growthDurationDays = listing.growthDurationDays.toString(),
            grownArea = listing.grownArea,
            stockNote = listing.stockNote
        )
    }

    fun hydrateConsumerDraft(request: ConsumerMilletRequestEntity?) {
        if (request == null) return
        val dateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(request.neededAtMillis),
            ZoneId.systemDefault()
        )
        _consumerDraft.value = ConsumerRequestDraft(
            milletType = request.milletType,
            quantityKg = request.quantityKg.toString(),
            neededDate = dateTime.toLocalDate().toString(),
            neededTime = dateTime.toLocalTime().withSecond(0).withNano(0).toString(),
            consumerLocation = request.consumerLocation,
            preferredSourceLocation = request.preferredSourceLocation,
            purpose = request.purpose.orEmpty()
        )
    }

    suspend fun publishFarmerStock(selection: MandiOption): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val profile = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Complete account profile first"))
        if (UserRole.fromValue(profile.role) != UserRole.FARMER) {
            return Result.failure(IllegalStateException("Farmer account required"))
        }

        val draft = _farmerDraft.value
        val quantity = draft.quantityAvailableKg.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Enter a valid quantity"))
        val growthDays = draft.growthDurationDays.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Enter valid growth duration"))
        if (draft.milletType.isBlank()) return Result.failure(IllegalArgumentException("Choose millet type"))
        if (draft.grownArea.isBlank()) return Result.failure(IllegalArgumentException("Enter grown area"))

        val existing = farmerStockListingDao.getByFarmer(session.uid)
        farmerStockListingDao.upsert(
            FarmerStockListingEntity(
                id = existing?.id ?: 0,
                farmerUserId = session.uid,
                marketCity = selection.marketCity,
                marketPlace = selection.marketPlace,
                selectedMarketRate = selection.selectedRate,
                milletType = draft.milletType,
                quantityAvailableKg = quantity,
                growthDurationDays = growthDays,
                grownArea = draft.grownArea,
                stockNote = draft.stockNote,
                isActive = true
            )
        )
        userProfileDao.upsert(profile.copy(stockStatus = "STOCK_AVAILABLE", updatedAt = System.currentTimeMillis()))
        return Result.success(Unit)
    }

    suspend fun saveConsumerRequest(): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val profile = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Complete account profile first"))
        if (UserRole.fromValue(profile.role) != UserRole.CONSUMER) {
            return Result.failure(IllegalStateException("Consumer account required"))
        }

        val draft = _consumerDraft.value
        val quantity = draft.quantityKg.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Enter a valid quantity"))
        if (draft.milletType.isBlank()) return Result.failure(IllegalArgumentException("Choose millet type"))
        if (draft.consumerLocation.isBlank()) return Result.failure(IllegalArgumentException("Enter location"))
        if (draft.preferredSourceLocation.isBlank()) {
            return Result.failure(IllegalArgumentException("Choose where you want to buy from"))
        }
        if (quantity > 100 && draft.purpose.isBlank()) {
            return Result.failure(IllegalArgumentException("Add purpose for requests above 100kg"))
        }

        val active = consumerMilletRequestDao.getActiveByConsumer(session.uid)
        val neededAtMillis = parseDateTimeMillis(draft.neededDate, draft.neededTime)
        val requestId = consumerMilletRequestDao.upsert(
            ConsumerMilletRequestEntity(
                id = active?.id ?: 0,
                consumerUserId = session.uid,
                milletType = draft.milletType,
                quantityKg = quantity,
                neededAtMillis = neededAtMillis,
                consumerLocation = draft.consumerLocation,
                preferredSourceLocation = draft.preferredSourceLocation,
                purpose = draft.purpose.takeIf { it.isNotBlank() },
                status = "ACTIVE"
            )
        ).toInt()

        val resolvedRequestId = if (requestId == 0) active?.id ?: 0 else requestId
        if (resolvedRequestId != 0) {
            rebuildMatchesForRequest(resolvedRequestId)
        }
        return Result.success(Unit)
    }

    suspend fun markActiveRequestFulfilled(): Result<Unit> = updateActiveRequestStatus("FULFILLED")

    suspend fun deleteActiveRequest(): Result<Unit> = updateActiveRequestStatus("DELETED")

    private suspend fun updateActiveRequestStatus(status: String): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val active = consumerMilletRequestDao.getActiveByConsumer(session.uid)
            ?: return Result.failure(IllegalStateException("No active request found"))
        consumerMilletRequestDao.updateStatus(active.id, status)
        farmerRequestMatchDao.deleteByRequestId(active.id)
        return Result.success(Unit)
    }

    suspend fun loadActiveRequestForCurrentUser(): ConsumerMilletRequestEntity? {
        val session = authRepository.currentSessionSnapshot() ?: return null
        return consumerMilletRequestDao.getActiveByConsumer(session.uid)
    }

    suspend fun loadListingForCurrentUser(): FarmerStockListingEntity? {
        val session = authRepository.currentSessionSnapshot() ?: return null
        return farmerStockListingDao.getByFarmer(session.uid)
    }

    fun buildMarketOptions(price: PriceEntity): List<MandiOption> {
        val basePlaces = cityMarketPlaces[price.city] ?: defaultPlaces(price.city)
        return basePlaces.take(5).mapIndexed { index, place ->
            val delta = when (index) {
                0 -> -90.0
                1 -> -30.0
                2 -> 0.0
                3 -> 45.0
                else -> 95.0
            }
            MandiOption(
                marketCity = price.city,
                marketPlace = place,
                milletType = price.milletType,
                selectedRate = (price.pricePerQuintal + delta).coerceAtLeast(1000.0)
            )
        }
    }

    private suspend fun rebuildMatchesForRequest(requestId: Int) {
        val request = consumerMilletRequestDao.getById(requestId) ?: return
        farmerRequestMatchDao.deleteByRequestId(request.id)
        val listings = farmerStockListingDao.getAllActive()
        val matches = listings.filter { listing ->
            listing.milletType.equals(request.milletType, ignoreCase = true) &&
                isNearbyLocation(request.consumerLocation, listing.marketCity)
        }.map { listing ->
            FarmerRequestMatchEntity(
                requestId = request.id,
                farmerUserId = listing.farmerUserId,
                listingId = listing.id,
                matchLocationContext = "${listing.marketPlace}, ${listing.marketCity}"
            )
        }
        if (matches.isNotEmpty()) {
            farmerRequestMatchDao.insertAll(matches)
        }
    }

    private fun isNearbyLocation(requestLocation: String, marketCity: String): Boolean {
        val normalizedRequest = requestLocation.trim().lowercase(Locale.getDefault())
        val normalizedCity = marketCity.trim().lowercase(Locale.getDefault())
        if (normalizedRequest.contains(normalizedCity) || normalizedCity.contains(normalizedRequest)) {
            return true
        }
        val nearby = nearbyCities[normalizedCity].orEmpty()
        return nearby.contains(normalizedRequest)
    }

    private fun parseDateTimeMillis(date: String, time: String): Long {
        return try {
            val safeDate = if (date.isBlank()) LocalDateTime.now().toLocalDate().toString() else date
            val safeTime = if (time.isBlank()) "09:00" else time
            LocalDateTime.parse(
                "$safeDate $safeTime",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Throwable) {
            System.currentTimeMillis()
        }
    }

    private fun defaultPlaces(city: String): List<String> = listOf(
        "$city Main Yard",
        "$city APMC Gate",
        "$city Rural Yard",
        "$city Trade Point",
        "$city Farmers Hub"
    )

    companion object {
        private val cityMarketPlaces = mapOf(
            "Bengaluru" to listOf("Yeshwanthpur Yard", "KR Market", "Byatarayanapura Yard", "Electronic City Hub", "Anekal Yard"),
            "Davangere" to listOf("Davangere APMC", "Harihar Yard", "Channagiri Yard", "Jagalur Trade Point", "Honnali Yard"),
            "Mysuru" to listOf("Mysuru APMC", "Nanjangud Yard", "Hunsur Yard", "T Narasipura Market", "Periyapatna Hub"),
            "Hubli" to listOf("Hubli APMC", "Navanagar Yard", "Keshwapur Market", "Dharwad Yard", "Kundgol Yard")
        )

        private val nearbyCities = mapOf(
            "bengaluru" to setOf("mysuru", "mandya", "tumakuru", "ramanagara", "bengaluru"),
            "davangere" to setOf("harihar", "shivamogga", "chitradurga", "haveri", "davangere"),
            "mysuru" to setOf("mandya", "nanjangud", "hunsur", "chamarajanagar", "mysuru"),
            "hubli" to setOf("dharwad", "gadag", "haveri", "belagavi", "hubli")
        )
    }
}
