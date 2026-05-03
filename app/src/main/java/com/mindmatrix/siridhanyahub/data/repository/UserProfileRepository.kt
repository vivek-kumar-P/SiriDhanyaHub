package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.UserProfileDraft
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _draft = MutableStateFlow(UserProfileDraft())
    val draft: StateFlow<UserProfileDraft> = _draft.asStateFlow()

    val activeProfile: StateFlow<UserProfileEntity?> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(null) else userProfileDao.observeByUserId(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, null)

    fun updateDraft(transform: (UserProfileDraft) -> UserProfileDraft) {
        _draft.value = transform(_draft.value)
    }

    fun primeDraftFromRole(role: UserRole) {
        _draft.value = _draft.value.copy(role = role)
    }

    suspend fun saveDraftForCurrentUser(): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val draft = _draft.value
        if (draft.role == null) return Result.failure(IllegalArgumentException("Select a role"))
        if (draft.name.isBlank()) return Result.failure(IllegalArgumentException("Name is required"))
        if (draft.phone.isBlank()) return Result.failure(IllegalArgumentException("Phone is required"))
        if (draft.district.isBlank()) return Result.failure(IllegalArgumentException("District is required"))
        if (draft.primaryMillet.isBlank()) return Result.failure(IllegalArgumentException("Primary millet is required"))

        userProfileDao.upsert(
            UserProfileEntity(
                userId = session.uid,
                name = draft.name,
                phone = draft.phone,
                district = draft.district,
                role = draft.role.value,
                primaryMillet = draft.primaryMillet
            )
        )
        return Result.success(Unit)
    }

    suspend fun hydrateDraftFromCurrentUser() {
        val session = authRepository.currentSessionSnapshot() ?: return
        val profile = userProfileDao.getByUserId(session.uid) ?: return
        _draft.value = UserProfileDraft(
            name = profile.name,
            phone = profile.phone,
            district = profile.district,
            role = UserRole.fromValue(profile.role),
            primaryMillet = profile.primaryMillet
        )
    }
}
