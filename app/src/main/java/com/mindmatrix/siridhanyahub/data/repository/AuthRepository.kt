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

@Singleton
class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth = Firebase.auth

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
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Name is required"))
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Enter a valid email"))
        if (password.length < 8) return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))

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
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Enter a valid email"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password is required"))

        return runCatching {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            Unit
        }.recoverCatching { throwable ->
            throw mapFirebaseError(throwable)
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
            is FirebaseAuthInvalidCredentialsException -> IllegalArgumentException("Invalid email or password format.")
            is FirebaseAuthInvalidUserException -> IllegalArgumentException("No account found for this email.")
            is FirebaseAuthException -> IllegalStateException(throwable.localizedMessage ?: "Firebase authentication failed.")
            else -> throwable
        }
    }
}
