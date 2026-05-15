package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.repository.FirestoreFarmerListing
import com.mindmatrix.siridhanyahub.data.repository.MarketplaceRepository
import com.mindmatrix.siridhanyahub.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FulfillmentStep {
    PICK_FARMER,
    CONFIRM_QUANTITY,
    RATE_FARMER,
    DONE
}

data class FulfillmentState(
    val step: FulfillmentStep = FulfillmentStep.PICK_FARMER,
    val availableFarmers: List<FirestoreFarmerListing> = emptyList(),
    val selectedFarmer: FirestoreFarmerListing? = null,
    val quantityKg: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val isSaving: Boolean = false,
    val message: String? = null,
    val lastTransactionId: Int = 0
)

@HiltViewModel
class FulfillmentViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val marketplaceRepository: MarketplaceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FulfillmentState())
    val state: StateFlow<FulfillmentState> = _state.asStateFlow()

    fun loadFarmers(milletType: String, requestedQuantity: Int) {
        viewModelScope.launch {
            marketplaceRepository.observeAllFarmerListingsFromFirestore()
                .collect { listings ->
                    val filtered = listings.filter {
                        it.milletType.equals(milletType, ignoreCase = true)
                    }
                    _state.value = _state.value.copy(
                        availableFarmers = filtered,
                        quantityKg = requestedQuantity.toString()
                    )
                }
        }
    }

    fun selectFarmer(farmer: FirestoreFarmerListing) {
        _state.value = _state.value.copy(
            selectedFarmer = farmer,
            step = FulfillmentStep.CONFIRM_QUANTITY
        )
    }

    fun updateQuantity(value: String) {
        _state.value = _state.value.copy(
            quantityKg = value.filter { it.isDigit() }.take(6)
        )
    }

    fun confirmQuantity() {
        _state.value = _state.value.copy(step = FulfillmentStep.RATE_FARMER)
    }

    fun setRating(stars: Int) {
        _state.value = _state.value.copy(rating = stars)
    }

    fun updateComment(value: String) {
        _state.value = _state.value.copy(comment = value)
    }

    fun submitFulfillment(onDone: () -> Unit) {
        val s = _state.value
        val farmer = s.selectedFarmer ?: return
        val quantity = s.quantityKg.toIntOrNull() ?: return
        if (s.rating == 0) {
            _state.value = s.copy(message = "Please give a star rating before submitting")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, message = null)

            // Record fulfillment + deduct stock
            val fulfillResult = transactionRepository.recordFulfillment(
                farmerUserId = farmer.farmerUserId,
                farmerName = farmer.farmerName,
                milletType = farmer.milletType,
                quantityKg = quantity,
                pricePerQuintal = farmer.selectedMarketRate,
                marketPlace = farmer.marketPlace
            )

            if (fulfillResult.isFailure) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    message = fulfillResult.exceptionOrNull()?.message ?: "Failed to record transaction"
                )
                return@launch
            }

            val transactionId = fulfillResult.getOrDefault(0)

            // Submit feedback
            transactionRepository.submitFeedback(
                transactionId = transactionId,
                farmerUserId = farmer.farmerUserId,
                farmerName = farmer.farmerName,
                milletType = farmer.milletType,
                quantityKg = quantity,
                rating = s.rating,
                comment = s.comment
            )

            _state.value = _state.value.copy(
                isSaving = false,
                step = FulfillmentStep.DONE
            )
            onDone()
        }
    }

    fun reset() {
        _state.value = FulfillmentState()
    }
}