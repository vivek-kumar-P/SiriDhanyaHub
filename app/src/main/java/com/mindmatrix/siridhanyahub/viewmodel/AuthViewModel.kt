package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.auth.UserSession
import com.mindmatrix.siridhanyahub.data.repository.AuthRepository
import com.mindmatrix.siridhanyahub.data.repository.PasswordStrength
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isRegisterMode: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val user: UserSession? = null,
    val isPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isGmailValid: Boolean = true,
    val postAuthRoute: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            errorMessage = null
        )
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            isGmailValid = value.isBlank() || authRepository.isValidGmail(value),
            errorMessage = null
        )
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordStrength = authRepository.passwordStrength(value),
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun fillTestCredentials() {
        _uiState.value = _uiState.value.copy(
            email = "test@gmail.com",
            password = "test@123",
            isGmailValid = true,
            passwordStrength = authRepository.passwordStrength("test@123"),
            errorMessage = null
        )
    }

    fun clearPostAuthRoute() {
        _uiState.value = _uiState.value.copy(postAuthRoute = null)
    }

    fun submit() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, postAuthRoute = null)
        viewModelScope.launch {
            val result = if (state.isRegisterMode) {
                authRepository.register(state.name, state.email, state.password)
            } else {
                authRepository.login(state.email, state.password)
            }

            result.onSuccess {
                val route = userProfileRepository.resolvePostAuthRoute()
                _uiState.value = _uiState.value.copy(
                    errorMessage = null,
                    isLoading = false,
                    postAuthRoute = route
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Authentication failed",
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isRegisterMode = false)
    }
}
