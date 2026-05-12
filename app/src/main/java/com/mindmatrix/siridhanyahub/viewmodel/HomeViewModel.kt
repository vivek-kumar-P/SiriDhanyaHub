package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerStockListingEntity
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.AppSeedRepository
import com.mindmatrix.siridhanyahub.data.repository.AuthRepository
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Guest",
    val isLoggedIn: Boolean = false,
    val activeRole: UserRole? = null,
    val activeProfile: UserProfileEntity? = null,
    val needsProfileCompletion: Boolean = false,
    val farmerListing: FarmerStockListingEntity? = null,
    val activeConsumerRequest: ConsumerMilletRequestEntity? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository,
    marketplaceRepository: MarketplaceRepository,
    private val seedRepository: AppSeedRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.currentUser,
        userProfileRepository.activeProfile,
        marketplaceRepository.activeFarmerListing,
        marketplaceRepository.activeConsumerRequest
    ) { user, profile, listing, activeRequest ->
        HomeUiState(
            userName = user?.name ?: "Guest",
            isLoggedIn = user != null,
            activeRole = UserRole.fromValue(profile?.role),
            activeProfile = profile,
            needsProfileCompletion = user != null && profile?.profileCompleted != true,
            farmerListing = listing,
            activeConsumerRequest = activeRequest
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch {
            seedRepository.seedIfNeeded()
        }
    }
}
