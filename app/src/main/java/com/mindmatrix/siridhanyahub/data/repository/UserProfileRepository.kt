package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.KarnatakaProfileData
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
    private val mobileRegex = Regex("^\\d{10}$")
    private val aadhaarLast4Regex = Regex("^\\d{4}$")
    
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
        _draft.value = _draft.value.copy(
            role = role,
            pmKisanOrFarmerId = if (role == UserRole.CONSUMER) "" else _draft.value.pmKisanOrFarmerId,
            primaryMilletCategory = if (role == UserRole.CONSUMER) "" else _draft.value.primaryMilletCategory,
            landSizeAcres = if (role == UserRole.CONSUMER) "" else _draft.value.landSizeAcres,
            preferredPurchaseLocation = if (role == UserRole.FARMER) "" else _draft.value.preferredPurchaseLocation
        )
    }

    /**
     * Minimal check to enable the Submit button.
     * We only require the role to be selected. This ensures the button is clickable,
     * and strict validation errors are shown only when the user attempts to submit.
     */
    fun isDraftValid(draft: UserProfileDraft): Boolean {
        return draft.role != null
    }

    suspend fun saveDraftForCurrentUser(): Result<Unit> {
        val session = authRepository.currentSessionSnapshot()
            ?: return Result.failure(IllegalStateException("Login required. Please sign in to save your profile."))
        
        val draft = _draft.value
        val role = draft.role ?: return Result.failure(IllegalArgumentException("Please select your account type: Farmer or Consumer."))

        // --- STRICT VALIDATION RULES ---
        
        if (draft.fullName.trim().length < 3) {
            return Result.failure(IllegalArgumentException("Please enter your full name (at least 3 characters)."))
        }
        
        if (!mobileRegex.matches(draft.contactNumber)) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }
        
        if (draft.address.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your address or place."))
        }
        
        if (draft.district.isBlank()) {
            return Result.failure(IllegalArgumentException("Please select your District."))
        }
        
        if (draft.taluk.isBlank()) {
            return Result.failure(IllegalArgumentException("Please select your Taluk."))
        }
        
        if (draft.village.isBlank()) {
            return Result.failure(IllegalArgumentException("Please select your Village."))
        }
        
        if (!aadhaarLast4Regex.matches(draft.aadhaarLast4)) {
            return Result.failure(IllegalArgumentException("Please enter exactly 4 digits for the Aadhaar reference."))
        }

        if (role == UserRole.FARMER) {
            if (draft.pmKisanOrFarmerId.trim().isEmpty()) {
                return Result.failure(IllegalArgumentException("Farmer ID / PM Kisan ID is required for farmers."))
            }
            if (draft.primaryMilletCategory.isBlank()) {
                return Result.failure(IllegalArgumentException("Please select your primary millet category."))
            }
            val landSize = draft.landSizeAcres.toDoubleOrNull()
            if (landSize == null || landSize <= 0) {
                return Result.failure(IllegalArgumentException("Please enter a valid land size in acres (must be greater than 0)."))
            }
        } else {
            if (draft.preferredPurchaseLocation.trim().isEmpty()) {
                return Result.failure(IllegalArgumentException("Preferred purchase location is required for consumers."))
            }
        }

        // --- UNIQUENESS CHECKS ---
        if (userProfileDao.countByContactNumber(draft.contactNumber, session.uid) > 0) {
            return Result.failure(IllegalArgumentException("This mobile number is already linked to another profile."))
        }
        
        if (role == UserRole.FARMER) {
            val normalizedFarmerId = draft.pmKisanOrFarmerId.trim().uppercase()
            if (userProfileDao.countByFarmerId(normalizedFarmerId, session.uid) > 0) {
                return Result.failure(IllegalArgumentException("This Farmer ID is already registered in our system."))
            }
        }

        // --- DATABASE PERSISTENCE ---
        userProfileDao.upsert(
            UserProfileEntity(
                userId = session.uid,
                role = role.value,
                fullName = draft.fullName.trim(),
                address = draft.address.trim(),
                contactNumber = draft.contactNumber,
                aadhaarLast4 = draft.aadhaarLast4,
                pmKisanOrFarmerId = draft.pmKisanOrFarmerId.trim().uppercase().takeIf { role == UserRole.FARMER },
                district = draft.district,
                taluk = draft.taluk,
                village = draft.village,
                primaryMilletCategory = draft.primaryMilletCategory.takeIf { it.isNotBlank() },
                landSizeAcres = draft.landSizeAcres.toDoubleOrNull()?.takeIf { role == UserRole.FARMER },
                preferredPurchaseLocation = draft.preferredPurchaseLocation.trim().takeIf { it.isNotBlank() },
                profileCompleted = true,
                stockStatus = if (role == UserRole.FARMER) "READY_FOR_STOCK" else "READY_TO_BUY"
            )
        )
        clearDraft()
        return Result.success(Unit)
    }

    suspend fun hydrateDraftFromCurrentUser() {
        val session = authRepository.currentSessionSnapshot() ?: return
        val profile = userProfileDao.getByUserId(session.uid) ?: return
        _draft.value = UserProfileDraft(
            fullName = profile.fullName,
            address = profile.address,
            contactNumber = profile.contactNumber,
            aadhaarLast4 = profile.aadhaarLast4,
            pmKisanOrFarmerId = profile.pmKisanOrFarmerId.orEmpty(),
            district = profile.district,
            role = UserRole.fromValue(profile.role),
            taluk = profile.taluk,
            village = profile.village,
            primaryMilletCategory = profile.primaryMilletCategory.orEmpty(),
            landSizeAcres = profile.landSizeAcres?.toString().orEmpty(),
            preferredPurchaseLocation = profile.preferredPurchaseLocation.orEmpty()
        )
    }

    suspend fun resolvePostAuthRoute(): String {
        val session = authRepository.currentSessionSnapshot() ?: return "home"
        val profile = userProfileDao.getByUserId(session.uid)
        val role = UserRole.fromValue(profile?.role)
        return if (profile?.profileCompleted == true) {
            nextHomeRouteForRole(role) ?: "home"
        } else {
            "profileSetup"
        }
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
}
