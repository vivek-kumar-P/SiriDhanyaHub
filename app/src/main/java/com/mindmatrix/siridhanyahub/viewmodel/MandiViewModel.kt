package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.MandiRepository
import com.mindmatrix.siridhanyahub.data.repository.TransactionRepository
import com.mindmatrix.siridhanyahub.data.repository.UserProfileRepository
import com.mindmatrix.siridhanyahub.data.transaction.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MandiViewModel @Inject constructor(
    private val mandiRepository: MandiRepository,
    userProfileRepository: UserProfileRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _selectedCity = MutableStateFlow("Bengaluru")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedPrice = MutableStateFlow<PriceEntity?>(null)
    val selectedPrice: StateFlow<PriceEntity?> = _selectedPrice.asStateFlow()
    private val _message = MutableSharedFlow<String>()
    val message = _message

    val prices: StateFlow<List<PriceEntity>> = _selectedCity
        .flatMapLatest { city -> mandiRepository.observeLatestPricesByCity(city) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val trend: StateFlow<List<PriceEntity>> = _selectedPrice
        .flatMapLatest { price ->
            if (price == null) flowOf(emptyList())
            else mandiRepository.observeSevenDayTrend(price.milletType, price.city)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val canLogSellIntent: StateFlow<Boolean> = userProfileRepository.activeProfile
        .map { UserRole.fromValue(it?.role) == UserRole.FARMER }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun selectCity(city: String) {
        _selectedCity.value = city
        _selectedPrice.value = null
    }

    fun showTrend(price: PriceEntity) {
        _selectedPrice.value = price
    }

    fun closeTrend() {
        _selectedPrice.value = null
    }

    fun recordSellIntent() {
        val price = selectedPrice.value ?: return
        viewModelScope.launch {
            val result = transactionRepository.record(
                role = UserRole.FARMER,
                type = TransactionType.SELL,
                milletType = price.milletType,
                quantityKg = 150,
                counterpartyName = price.city,
                referenceContext = "Mandi Watch"
            )
            _message.emit(result.exceptionOrNull()?.message ?: "Sell intent recorded")
        }
    }
}
