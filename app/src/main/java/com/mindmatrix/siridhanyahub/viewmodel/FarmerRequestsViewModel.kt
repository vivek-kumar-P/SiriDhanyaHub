package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.model.FarmerInboxItem
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FarmerRequestsUiState(
    val isFarmer: Boolean = false,
    val items: List<FarmerInboxItem> = emptyList()
)

@HiltViewModel
class FarmerRequestsViewModel @Inject constructor(
    marketplaceRepository: MarketplaceRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {
    val uiState: StateFlow<FarmerRequestsUiState> = combine(
        marketplaceRepository.farmerInbox,
        userProfileRepository.activeProfile.map { UserRole.fromValue(it?.role) == UserRole.FARMER }
    ) { items, isFarmer ->
        FarmerRequestsUiState(
            isFarmer = isFarmer,
            items = items
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FarmerRequestsUiState())
}
