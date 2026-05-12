package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.ConsumerRequestDraft
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConsumerRequestUiState(
    val draft: ConsumerRequestDraft = ConsumerRequestDraft(),
    val activeRole: UserRole? = null,
    val activeRequest: ConsumerMilletRequestEntity? = null,
    val isSaving: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class DirectBuyViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val isSaving = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ConsumerRequestUiState> = combine(
        marketplaceRepository.consumerDraft,
        marketplaceRepository.activeConsumerRequest,
        userProfileRepository.activeProfile.map { UserRole.fromValue(it?.role) },
        isSaving,
        message
    ) { draft, request, role, isSaving, message ->
        ConsumerRequestUiState(
            draft = draft,
            activeRole = role,
            activeRequest = request,
            isSaving = isSaving,
            message = message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConsumerRequestUiState())

    init {
        viewModelScope.launch {
            marketplaceRepository.hydrateConsumerDraft(marketplaceRepository.loadActiveRequestForCurrentUser())
        }
    }

    fun updateMilletType(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(milletType = value) }

    fun updateQuantity(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(quantityKg = value) }

    fun updateNeededDate(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(neededDate = value) }

    fun updateNeededTime(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(neededTime = value) }

    fun updateLocation(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(consumerLocation = value) }

    fun updatePreferredSourceLocation(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(preferredSourceLocation = value) }

    fun updatePurpose(value: String) =
        marketplaceRepository.updateConsumerDraft { it.copy(purpose = value) }

    fun saveRequest(onSaved: () -> Unit) {
        viewModelScope.launch {
            isSaving.value = true
            val result = marketplaceRepository.saveConsumerRequest()
            isSaving.value = false
            message.value = result.exceptionOrNull()?.message ?: "Request is now active for nearby farmers"
            if (result.isSuccess) onSaved()
        }
    }

    fun markFulfilled() {
        viewModelScope.launch {
            val result = marketplaceRepository.markActiveRequestFulfilled()
            message.value = result.exceptionOrNull()?.message ?: "Request marked as fulfilled"
        }
    }

    fun deleteRequest() {
        viewModelScope.launch {
            val result = marketplaceRepository.deleteActiveRequest()
            message.value = result.exceptionOrNull()?.message ?: "Request deleted"
        }
    }
}
