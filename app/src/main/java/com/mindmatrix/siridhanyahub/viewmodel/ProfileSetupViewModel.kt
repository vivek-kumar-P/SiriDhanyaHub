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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileSetupUiState(
    val draft: UserProfileDraft = UserProfileDraft(),
    val isLoggedIn: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val _isSaving = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileSetupUiState> = combine(
        userProfileRepository.draft,
        authRepository.currentUser,
        _isSaving,
        _message
    ) { draft, user, isSaving, message ->
        ProfileSetupUiState(
            draft = draft,
            isLoggedIn = user != null,
            isSaving = isSaving,
            message = message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileSetupUiState())

    init {
        viewModelScope.launch {
            userProfileRepository.hydrateDraftFromCurrentUser()
        }
    }

    fun primeRole(role: UserRole) {
        userProfileRepository.primeDraftFromRole(role)
    }

    fun updateName(value: String) = userProfileRepository.updateDraft { it.copy(name = value) }
    fun updatePhone(value: String) = userProfileRepository.updateDraft { it.copy(phone = value) }
    fun updateDistrict(value: String) = userProfileRepository.updateDraft { it.copy(district = value) }
    fun updatePrimaryMillet(value: String) = userProfileRepository.updateDraft { it.copy(primaryMillet = value) }

    fun save(onSaved: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val result = userProfileRepository.saveDraftForCurrentUser()
            _isSaving.value = false
            _message.value = result.exceptionOrNull()?.message ?: "Profile saved"
            onSaved(result.isSuccess)
        }
    }
}
