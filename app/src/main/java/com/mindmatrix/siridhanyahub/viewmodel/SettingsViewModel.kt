package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.repository.AuthRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingsUiState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val profile: UserProfileEntity? = null,
    val role: String = "",
    val showAnalytics: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        authRepository.currentUser,
        userProfileRepository.activeProfile
    ) { user, profile ->
        SettingsUiState(
            isLoggedIn = user != null,
            userName = user?.name.orEmpty(),
            userEmail = user?.email.orEmpty(),
            profile = profile,
            role = profile?.role.orEmpty(),
            showAnalytics = user != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun logout() {
        authRepository.logout()
    }
}
