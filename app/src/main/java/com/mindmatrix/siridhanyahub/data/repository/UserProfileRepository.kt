package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.UserProfileDraft
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository
) {
    private val mobileRegex = Regex("^\\d{10}$")
    private val aadhaarLast4Regex = Regex("^\\d{4}$")
    private val farmerIdRegex = Regex(
        "^(PMKISAN\\d{10}|KA\\d{8,10})$",
        RegexOption.IGNORE_CASE
    )
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _draft = MutableStateFlow(UserProfileDraft())
    private val _pendingOnboardingRole = MutableStateFlow<UserRole?>(null)
    val draft: StateFlow<UserProfileDraft> = _draft.asStateFlow()
    val pendingOnboardingRole: StateFlow<UserRole?> = _pendingOnboardingRole.asStateFlow()

    val activeProfile: StateFlow<UserProfileEntity?> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(null) else userProfileDao.observeByUserId(session.uid)
        }
        .stateIn(repositoryScope, SharingStarted.Eagerly, null)

    fun updateDraft(transform: (UserProfileDraft) -> UserProfileDraft) {
        _draft.value = transform(_draft.value)
    }

    fun setPendingOnboardingRole(role: UserRole) {
        _pendingOnboardingRole.value = role
        _draft.value = _draft.value.copy(role = role)
    }

    fun clearPendingOnboardingRole() {
        _pendingOnboardingRole.value = null
    }

    fun isFarmerDraftValid(draft: UserProfileDraft = _draft.value): Boolean {
        return draft.fullName.trim().length >= 3 &&
            mobileRegex.matches(draft.contactNumber) &&
            draft.address.isNotBlank() &&
            draft.district.isNotBlank() &&
            draft.taluk.isNotBlank() &&
            draft.village.isNotBlank() &&
            aadhaarLast4Regex.matches(draft.aadhaarLast4) &&
            farmerIdRegex.matches(draft.pmKisanOrFarmerId.trim()) &&
            draft.primaryMilletCategory.isNotBlank() &&
            (draft.landSizeAcres.toDoubleOrNull() ?: 0.0) > 0.0
    }

    fun isConsumerDraftValid(draft: UserProfileDraft = _draft.value): Boolean {
        return draft.fullName.trim().length >= 3 &&
            mobileRegex.matches(draft.contactNumber) &&
            draft.address.isNotBlank() &&
            draft.district.isNotBlank() &&
            draft.taluk.isNotBlank() &&
            draft.village.isNotBlank() &&
            aadhaarLast4Regex.matches(draft.aadhaarLast4) &&
            draft.preferredPurchaseLocation.isNotBlank()
    }

    suspend fun confirmRoleForCurrentUser(role: UserRole): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val existing = userProfileDao.getByUserId(session.uid)
        if (existing?.roleLocked == true) {
            return Result.failure(IllegalStateException("Account type is already locked for this account"))
        }
        userProfileDao.upsert(
            UserProfileEntity(
                userId = session.uid,
                role = role.value,
                roleLocked = true,
                fullName = existing?.fullName.orEmpty(),
                address = existing?.address.orEmpty(),
                contactNumber = existing?.contactNumber.orEmpty(),
                aadhaarLast4 = existing?.aadhaarLast4.orEmpty(),
                pmKisanOrFarmerId = existing?.pmKisanOrFarmerId,
                district = existing?.district.orEmpty(),
                taluk = existing?.taluk.orEmpty(),
                village = existing?.village.orEmpty(),
                primaryMilletCategory = existing?.primaryMilletCategory,
                landSizeAcres = existing?.landSizeAcres,
                preferredPurchaseLocation = existing?.preferredPurchaseLocation,
                profileCompleted = false,
                stockStatus = if (role == UserRole.FARMER) "READY_FOR_STOCK" else "READY_TO_BUY",
                updatedAt = System.currentTimeMillis()
            )
        )
        _draft.value = UserProfileDraft(role = role)
        return Result.success(Unit)
    }

    suspend fun lockPendingRoleForCurrentUser(): Result<UserRole> {
        val role = _pendingOnboardingRole.value
            ?: return Result.failure(IllegalStateException("Choose account type first"))
        val result = confirmRoleForCurrentUser(role)
        return if (result.isSuccess) {
            clearPendingOnboardingRole()
            Result.success(role)
        } else {
            Result.failure(result.exceptionOrNull() ?: IllegalStateException("Unable to lock account type"))
        }
    }

    suspend fun saveFarmerDraftForCurrentUser(): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val existing = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Choose account type first"))
        if (UserRole.fromValue(existing.role) != UserRole.FARMER || !existing.roleLocked) {
            return Result.failure(IllegalStateException("This account is not locked as a farmer"))
        }

        val draft = _draft.value.copy(role = UserRole.FARMER)
        if (!isFarmerDraftValid(draft)) {
            return Result.failure(IllegalArgumentException("Fill all required farmer details before saving"))
        }
        val normalizedMobile = draft.contactNumber.filter(Char::isDigit)
        val normalizedAadhaar = draft.aadhaarLast4.filter(Char::isDigit).takeLast(4)
        val normalizedFarmerId = draft.pmKisanOrFarmerId.trim().uppercase()

        val duplicate = duplicatePersonError(
            userId = session.uid,
            mobile = normalizedMobile,
            aadhaar = normalizedAadhaar,
            farmerId = normalizedFarmerId
        )
        if (duplicate != null) return Result.failure(IllegalArgumentException(duplicate))

        userProfileDao.upsert(
            existing.copy(
                fullName = draft.fullName.trim(),
                address = draft.address.trim(),
                contactNumber = normalizedMobile,
                aadhaarLast4 = normalizedAadhaar,
                pmKisanOrFarmerId = normalizedFarmerId,
                district = draft.district,
                taluk = draft.taluk,
                village = draft.village,
                primaryMilletCategory = draft.primaryMilletCategory,
                landSizeAcres = draft.landSizeAcres.toDoubleOrNull(),
                preferredPurchaseLocation = null,
                profileCompleted = true,
                stockStatus = "PENDING_VERIFICATION",
                updatedAt = System.currentTimeMillis()
            )
        )
        clearDraft()
        return Result.success(Unit)
    }

    suspend fun saveConsumerDraftForCurrentUser(): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required"))
        val existing = userProfileDao.getByUserId(session.uid)
            ?: return Result.failure(IllegalStateException("Choose account type first"))
        if (UserRole.fromValue(existing.role) != UserRole.CONSUMER || !existing.roleLocked) {
            return Result.failure(IllegalStateException("This account is not locked as a consumer"))
        }

        val draft = _draft.value.copy(role = UserRole.CONSUMER)
        if (!isConsumerDraftValid(draft)) {
            return Result.failure(IllegalArgumentException("Fill all required consumer details before saving"))
        }
        val normalizedMobile = draft.contactNumber.filter(Char::isDigit)
        val normalizedAadhaar = draft.aadhaarLast4.filter(Char::isDigit).takeLast(4)

        val duplicate = duplicatePersonError(
            userId = session.uid,
            mobile = normalizedMobile,
            aadhaar = normalizedAadhaar,
            farmerId = null
        )
        if (duplicate != null) return Result.failure(IllegalArgumentException(duplicate))

        userProfileDao.upsert(
            existing.copy(
                fullName = draft.fullName.trim(),
                address = draft.address.trim(),
                contactNumber = normalizedMobile,
                aadhaarLast4 = normalizedAadhaar,
                pmKisanOrFarmerId = null,
                district = draft.district,
                taluk = draft.taluk,
                village = draft.village,
                primaryMilletCategory = null,
                landSizeAcres = null,
                preferredPurchaseLocation = draft.preferredPurchaseLocation.trim(),
                profileCompleted = true,
                stockStatus = "READY_TO_BUY",
                updatedAt = System.currentTimeMillis()
            )
        )
        clearDraft()
        return Result.success(Unit)
    }

    suspend fun hydrateDraftFromCurrentUser() {
        val session = authRepository.currentSessionSnapshot() ?: return
        val profile = userProfileDao.getByUserId(session.uid) ?: return
        _draft.value = UserProfileDraft(
            role = UserRole.fromValue(profile.role),
            fullName = profile.fullName,
            address = profile.address,
            contactNumber = profile.contactNumber,
            aadhaarLast4 = profile.aadhaarLast4,
            pmKisanOrFarmerId = profile.pmKisanOrFarmerId.orEmpty(),
            district = profile.district,
            taluk = profile.taluk,
            village = profile.village,
            primaryMilletCategory = profile.primaryMilletCategory.orEmpty(),
            landSizeAcres = profile.landSizeAcres?.toString().orEmpty(),
            preferredPurchaseLocation = profile.preferredPurchaseLocation.orEmpty()
        )
    }

    suspend fun resolvePostAuthRoute(): String {
        val session = authRepository.currentSessionSnapshot() ?: return "home"
        val profile = userProfileDao.getByUserId(session.uid) ?: return "roleConfirmation"
        val role = UserRole.fromValue(profile.role)
        if (!profile.roleLocked || role == null) return "roleConfirmation"
        if (!profile.profileCompleted) {
            return if (role == UserRole.FARMER) "farmerProfile" else "consumerProfile"
        }
        return nextHomeRouteForRole(role) ?: "home"
    }

    suspend fun getCurrentRole(): UserRole? {
        val session = authRepository.currentSessionSnapshot() ?: return null
        return UserRole.fromValue(userProfileDao.getByUserId(session.uid)?.role)
    }

    fun clearDraft() {
        _draft.value = UserProfileDraft()
    }

    fun nextHomeRouteForRole(role: UserRole?): String? {
        return when (role) {
            UserRole.FARMER -> "farmerHome"
            UserRole.CONSUMER -> "consumerHome"
            null -> null
        }
    }

    private suspend fun duplicatePersonError(
        userId: String,
        mobile: String,
        aadhaar: String,
        farmerId: String?
    ): String? {
        if (userProfileDao.countByContactNumber(mobile, userId) > 0) {
            return "Account already exists with this mobile number"
        }
        if (userProfileDao.countByAadhaarLast4(aadhaar, userId) > 0) {
            return "Account already exists with this Aadhaar reference"
        }
        if (!farmerId.isNullOrBlank() && userProfileDao.countByFarmerId(farmerId, userId) > 0) {
            return "Account already exists with this Farmer ID"
        }
        return null
    }
}
