package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.FeedbackEntity
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.TransactionRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val role: UserRole? = null,
    val records: List<TransactionRecordEntity> = emptyList(),
    val feedback: List<FeedbackEntity> = emptyList(),
    val totalKg: Int = 0,
    val totalAmount: Double = 0.0,
    val averageRating: Double = 0.0,
    val uniqueCounterparties: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = combine(
        userProfileRepository.activeProfile,
        transactionRepository.currentUserTransactions
    ) { profile, records ->
        Pair(profile, records)
    }.flatMapLatest { (profile, records) ->
        val role = UserRole.fromValue(profile?.role)
        val userId = profile?.userId ?: ""
        val feedbackFlow = when (role) {
            UserRole.FARMER -> transactionRepository.observeFeedbackForFarmer(userId)
            UserRole.CONSUMER -> transactionRepository.observeFeedbackForConsumer(userId)
            null -> flowOf(emptyList())
        }
        feedbackFlow.map { feedback ->
            AnalyticsUiState(
                role = role,
                records = records,
                feedback = feedback,
                totalKg = records.sumOf { it.quantityKg },
                totalAmount = records.sumOf { it.amountEstimate ?: 0.0 },
                averageRating = if (feedback.isEmpty()) 0.0
                else feedback.sumOf { it.rating.toDouble() } / feedback.size,
                uniqueCounterparties = records.map { it.counterpartyName }.distinct().size
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AnalyticsUiState()
    )
}