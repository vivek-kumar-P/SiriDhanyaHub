package com.mindmatrix.siridhanyahub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mindmatrix.siridhanyahub.data.auth.UserSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

enum class PasswordStrength(val label: String) {
    WEAK("Weak"),
    MEDIUM("Medium"),
    STRONG("Strong")
}

@Singleton
class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth = Firebase.auth
    private val gmailRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$", RegexOption.IGNORE_CASE)
    
    // Strict password: At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private val strictPasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")

    val currentUser: Flow<UserSession?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toUserSession())
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.toUserSession())
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        if (name.trim().length < 3) {
            return Result.failure(IllegalArgumentException("Name must be at least 3 characters long"))
        }
        if (!isValidGmail(email)) {
            return Result.failure(IllegalArgumentException("Registration is restricted to @gmail.com addresses for security verification"))
        }
        if (!strictPasswordRegex.matches(password)) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character (@#\$%^&+=!)"))
        }

        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
            )?.await()
            Unit
        }.recoverCatching { throwable ->
            throw mapFirebaseError(throwable)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        if (!isValidGmail(email)) {
            return Result.failure(IllegalArgumentException("Please enter a valid @gmail.com address"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required"))
        }

        return runCatching {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            Unit
        }.recoverCatching { throwable ->
            throw mapFirebaseError(throwable)
        }
    }

    fun isValidGmail(email: String): Boolean = gmailRegex.matches(email.trim())

    fun passwordStrength(password: String): PasswordStrength {
        return when {
            strictPasswordRegex.matches(password) -> PasswordStrength.STRONG
            password.length >= 8 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentSessionSnapshot(): UserSession? = auth.currentUser?.toUserSession()

    private fun com.google.firebase.auth.FirebaseUser.toUserSession(): UserSession {
        return UserSession(
            uid = uid,
            name = displayName?.takeIf { it.isNotBlank() } ?: email?.substringBefore("@").orEmpty(),
            email = email.orEmpty()
        )
    }

    private fun mapFirebaseError(throwable: Throwable): Throwable {
        return when (throwable) {
            is FirebaseAuthUserCollisionException -> IllegalArgumentException("This email is already registered.")
            is FirebaseAuthInvalidCredentialsException -> IllegalArgumentException("Invalid login credentials. Please check your password.")
            is FirebaseAuthInvalidUserException -> IllegalArgumentException("No account found for this email.")
            is FirebaseAuthException -> IllegalStateException(throwable.localizedMessage ?: "Firebase authentication failed.")
            else -> throwable
        }
    }
}
