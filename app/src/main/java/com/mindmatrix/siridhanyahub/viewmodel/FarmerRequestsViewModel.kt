package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.repository.FirestoreConsumerRequest
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FarmerRequestsUiState(
    val isFarmer: Boolean = false,
    val items: List<FirestoreConsumerRequest> = emptyList()
)

@HiltViewModel
class FarmerRequestsViewModel @Inject constructor(
    marketplaceRepository: MarketplaceRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    val uiState: StateFlow<FarmerRequestsUiState> = combine(
        marketplaceRepository.observeAllConsumerRequestsFromFirestore(),
        marketplaceRepository.activeFarmerListing,
        userProfileRepository.activeProfile
    ) { allRequests, myListing, profile ->
        val isFarmer = UserRole.fromValue(profile?.role) == UserRole.FARMER
        val filtered = if (myListing == null) {
            allRequests  // show all if no listing yet
        } else {
            allRequests.filter { request ->
                request.milletType.equals(myListing.milletType, ignoreCase = true)
            }
        }
        FarmerRequestsUiState(isFarmer = isFarmer, items = filtered)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FarmerRequestsUiState()
    )
}