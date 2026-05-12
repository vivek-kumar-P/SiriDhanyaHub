package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerStockListingEntity
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.FarmerStockDraft
import com.mindmatrix.siridhanyahub.data.repository.MandiOption
import com.mindmatrix.siridhanyahub.data.repository.MandiRepository
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FarmerMandiUiState(
    val selectedCity: String = "Bengaluru",
    val searchQuery: String = "",
    val prices: List<PriceEntity> = emptyList(),
    val selectedPrice: PriceEntity? = null,
    val mandiOptions: List<MandiOption> = emptyList(),
    val selectedOption: MandiOption? = null,
    val stockDraft: FarmerStockDraft = FarmerStockDraft(),
    val currentListing: FarmerStockListingEntity? = null,
    val farmerProfile: UserProfileEntity? = null,
    val isFarmer: Boolean = false,
    val isPublishing: Boolean = false,
    val message: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MandiViewModel @Inject constructor(
    private val mandiRepository: MandiRepository,
    private val marketplaceRepository: MarketplaceRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val selectedCity = MutableStateFlow("Bengaluru")
    private val selectedPrice = MutableStateFlow<PriceEntity?>(null)
    private val selectedOption = MutableStateFlow<MandiOption?>(null)
    private val searchQuery = MutableStateFlow("")
    private val isPublishing = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val latestPrices = selectedCity
        .flatMapLatest { city -> mandiRepository.observeLatestPricesByCity(city) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val marketState = combine(
        selectedCity,
        searchQuery,
        latestPrices,
        selectedPrice,
        selectedOption,
    ) { city, query, prices, selectedPrice, selectedOption ->
        val filteredPrices = prices.filter { price ->
            query.isBlank() ||
                price.city.contains(query, ignoreCase = true) ||
                price.milletType.contains(query, ignoreCase = true)
        }
        FarmerMandiUiState(
            selectedCity = city,
            searchQuery = query,
            prices = filteredPrices,
            selectedPrice = selectedPrice,
            mandiOptions = selectedPrice?.let(marketplaceRepository::buildMarketOptions).orEmpty(),
            selectedOption = selectedOption
        )
    }

    private val farmerProfileState = combine(
        marketplaceRepository.farmerDraft,
        marketplaceRepository.activeFarmerListing,
        userProfileRepository.activeProfile,
        isPublishing,
        message
    ) { draft, listing, profile, isPublishing, message ->
        FarmerMandiUiState(
            stockDraft = draft,
            currentListing = listing,
            farmerProfile = profile,
            isFarmer = UserRole.fromValue(profile?.role) == UserRole.FARMER,
            isPublishing = isPublishing,
            message = message
        )
    }

    val uiState: StateFlow<FarmerMandiUiState> = combine(
        marketState,
        farmerProfileState
    ) { marketState, farmerProfileState ->
        marketState.copy(
            stockDraft = farmerProfileState.stockDraft,
            currentListing = farmerProfileState.currentListing,
            farmerProfile = farmerProfileState.farmerProfile,
            isFarmer = farmerProfileState.isFarmer,
            isPublishing = farmerProfileState.isPublishing,
            message = farmerProfileState.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FarmerMandiUiState())

    init {
        viewModelScope.launch {
            marketplaceRepository.hydrateFarmerDraft(marketplaceRepository.loadListingForCurrentUser())
        }
    }

    fun selectCity(city: String) {
        selectedCity.value = city
        selectedPrice.value = null
        selectedOption.value = null
    }

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun openPrice(price: PriceEntity) {
        selectedPrice.value = price
        selectedOption.value = null
    }

    fun selectMandiOption(option: MandiOption) {
        selectedOption.value = option
        marketplaceRepository.updateFarmerDraft { current ->
            if (current.milletType.isBlank()) current.copy(milletType = option.milletType) else current
        }
    }

    fun updateMilletType(value: String) =
        marketplaceRepository.updateFarmerDraft { it.copy(milletType = value) }

    fun updateQuantity(value: String) =
        marketplaceRepository.updateFarmerDraft { it.copy(quantityAvailableKg = value) }

    fun updateGrowthDuration(value: String) =
        marketplaceRepository.updateFarmerDraft { it.copy(growthDurationDays = value) }

    fun updateGrownArea(value: String) =
        marketplaceRepository.updateFarmerDraft { it.copy(grownArea = value) }

    fun updateStockNote(value: String) =
        marketplaceRepository.updateFarmerDraft { it.copy(stockNote = value) }

    fun publishStock(onPublished: () -> Unit) {
        val option = selectedOption.value ?: return
        viewModelScope.launch {
            isPublishing.value = true
            val result = marketplaceRepository.publishFarmerStock(option)
            isPublishing.value = false
            message.value = result.exceptionOrNull()?.message ?: "Stock available status confirmed"
            if (result.isSuccess) {
                onPublished()
            }
        }
    }
}
