package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.TransactionRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val role: UserRole? = null,
    val records: List<TransactionRecordEntity> = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    val uiState: StateFlow<AnalyticsUiState> = combine(
        userProfileRepository.activeProfile,
        transactionRepository.currentUserTransactions
    ) { profile, records ->
        AnalyticsUiState(
            role = UserRole.fromValue(profile?.role),
            records = records
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsUiState())
}
