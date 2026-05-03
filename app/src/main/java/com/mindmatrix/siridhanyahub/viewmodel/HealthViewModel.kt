package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import com.mindmatrix.siridhanyahub.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HealthViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {
    private val _selectedMilletType = MutableStateFlow<String?>(null)
    val selectedMilletType: StateFlow<String?> = _selectedMilletType.asStateFlow()

    val allBenefits: StateFlow<List<HealthBenefitEntity>> = healthRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedBenefits: StateFlow<List<HealthBenefitEntity>> = _selectedMilletType
        .flatMapLatest { milletType ->
            healthRepository.observeByMilletType(milletType ?: "")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun openMillet(milletType: String) {
        _selectedMilletType.value = milletType
    }

    fun closeMillet() {
        _selectedMilletType.value = null
    }
}
