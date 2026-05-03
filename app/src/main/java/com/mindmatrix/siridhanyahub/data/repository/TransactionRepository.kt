package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.TransactionRecordDao
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.transaction.TransactionType
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionRecordDao: TransactionRecordDao,
    private val authRepository: AuthRepository
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val currentUserTransactions: StateFlow<List<TransactionRecordEntity>> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(emptyList()) else transactionRecordDao.observeByUserId(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

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
}
