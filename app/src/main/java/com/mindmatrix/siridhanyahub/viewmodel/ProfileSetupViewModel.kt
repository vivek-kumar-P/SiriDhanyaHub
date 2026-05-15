package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.auth.UserSession
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.UserProfileDraft
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.AuthRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileSetupUiState(
    val draft: UserProfileDraft = UserProfileDraft(),
    val isLoggedIn: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val activeRole: UserRole? = null,
    val activeProfile: UserProfileEntity? = null,
    val pendingOnboardingRole: UserRole? = null,
    val isRoleLocked: Boolean = false,
    val isProfileComplete: Boolean = false,
    val farmerFormReady: Boolean = false,
    val consumerFormReady: Boolean = false
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    private data class IntermediateState(
        val draft: UserProfileDraft,
        val profile: UserProfileEntity?,
        val pendingRole: UserRole?,
        val user: UserSession?,
        val isSaving: Boolean
    )

    val uiState: StateFlow<ProfileSetupUiState> = combine(
        combine(
            userProfileRepository.draft,
            userProfileRepository.activeProfile,
            userProfileRepository.pendingOnboardingRole,
            authRepository.currentUser,
            _isSaving
        ) { args ->
            // Use array-based lambda to handle 5 flows safely
            @Suppress("UNCHECKED_CAST")
            IntermediateState(
                draft = args[0] as UserProfileDraft,
                profile = args[1] as UserProfileEntity?,
                pendingRole = args[2] as UserRole?,
                user = args[3] as UserSession?,
                isSaving = args[4] as Boolean
            )
        },
        _message
    ) { intermediate, message ->
        val draft = intermediate.draft
        val profile = intermediate.profile
        val role = draft.role ?: UserRole.fromValue(profile?.role)

        ProfileSetupUiState(
            draft = draft,
            isLoggedIn = intermediate.user != null,
            isSaving = intermediate.isSaving,
            message = message,
            activeRole = role,
            activeProfile = profile,
            pendingOnboardingRole = intermediate.pendingRole,
            isRoleLocked = profile?.roleLocked == true,
            isProfileComplete = profile?.profileCompleted == true,
            farmerFormReady = userProfileRepository.isFarmerDraftValid(
                draft.copy(role = UserRole.FARMER)
            ),
            consumerFormReady = userProfileRepository.isConsumerDraftValid(
                draft.copy(role = UserRole.CONSUMER)
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileSetupUiState()
    )

    init {
        viewModelScope.launch {
            userProfileRepository.hydrateDraftFromCurrentUser()
        }
    }

    fun updateFullName(value: String) =
        userProfileRepository.updateDraft { it.copy(fullName = value) }

    fun updateAddress(value: String) =
        userProfileRepository.updateDraft { it.copy(address = value) }

    fun updateContactNumber(value: String) =
        userProfileRepository.updateDraft {
            it.copy(contactNumber = value.filter(Char::isDigit).take(10))
        }

    fun updateAadhaarLast4(value: String) =
        userProfileRepository.updateDraft {
            it.copy(aadhaarLast4 = value.filter(Char::isDigit).take(4))
        }

    fun updatePmKisanOrFarmerId(value: String) =
        userProfileRepository.updateDraft { it.copy(pmKisanOrFarmerId = value.uppercase()) }

    fun updateDistrict(value: String) =
        userProfileRepository.updateDraft { it.copy(district = value, taluk = "", village = "") }

    fun updateTaluk(value: String) =
        userProfileRepository.updateDraft { it.copy(taluk = value, village = "") }

    fun updateVillage(value: String) =
        userProfileRepository.updateDraft { it.copy(village = value) }

    fun updatePrimaryMilletCategory(value: String) =
        userProfileRepository.updateDraft { it.copy(primaryMilletCategory = value) }

    fun updateLandSizeAcres(value: String) =
        userProfileRepository.updateDraft {
            it.copy(landSizeAcres = value.filter { c -> c.isDigit() || c == '.' }.take(8))
        }

    fun updatePreferredPurchaseLocation(value: String) =
        userProfileRepository.updateDraft { it.copy(preferredPurchaseLocation = value) }

    fun startOnboarding(role: UserRole) {
        _message.value = null
        userProfileRepository.setPendingOnboardingRole(role)
    }

    fun clearPendingOnboardingRole() {
        userProfileRepository.clearPendingOnboardingRole()
    }

    fun confirmRole(role: UserRole, onConfirmed: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _message.value = null
            val result = userProfileRepository.confirmRoleForCurrentUser(role)
            _isSaving.value = false
            _message.value = result.exceptionOrNull()?.message
            onConfirmed(result.isSuccess)
        }
    }

    fun saveFarmer(onSaved: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _message.value = null
            val result = userProfileRepository.saveFarmerDraftForCurrentUser()
            _isSaving.value = false
            _message.value = result.exceptionOrNull()?.message ?: "Farmer account activated"
            onSaved(result.isSuccess)
        }
    }

    fun saveConsumer(onSaved: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _message.value = null
            val result = userProfileRepository.saveConsumerDraftForCurrentUser()
            _isSaving.value = false
            _message.value = result.exceptionOrNull()?.message ?: "Consumer account activated"
            onSaved(result.isSuccess)
        }
    }
}