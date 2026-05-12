package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isProfileValid: Boolean = false,
    val destinationHomeByRole: UserRole? = null,
    val isEditMode: Boolean = false
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    // Split into nested combines to avoid the 5-param array-based overload
    val uiState: StateFlow<ProfileSetupUiState> =
        combine(
            userProfileRepository.draft,
            userProfileRepository.activeProfile,
            authRepository.currentUser
        ) { draft, profile, user ->
            Triple(draft, profile, user)
        }.combine(
            combine(_isSaving, _message) { saving, msg -> Pair(saving, msg) }
        ) { (draft, profile, user), (isSaving, message) ->
            val resolvedRole = draft.role ?: UserRole.fromValue(profile?.role)
            ProfileSetupUiState(
                draft = draft,
                isLoggedIn = user != null,
                isSaving = isSaving,
                message = message,
                activeRole = resolvedRole,
                isProfileValid = userProfileRepository.isDraftValid(draft),
                destinationHomeByRole = resolvedRole,
                isEditMode = profile?.profileCompleted == true
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

    fun primeRole(role: UserRole) {
        userProfileRepository.primeDraftFromRole(role)
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

    fun save(onSaved: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _message.value = null
            val result = userProfileRepository.saveDraftForCurrentUser()
            _isSaving.value = false
            _message.value = result.exceptionOrNull()?.message ?: "Profile saved successfully!"
            if (result.isSuccess) {
                onSaved(true)
            }
        }
    }
}