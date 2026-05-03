package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.DirectBuyRepository
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
class DirectBuyViewModel @Inject constructor(
    private val directBuyRepository: DirectBuyRepository,
    userProfileRepository: UserProfileRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _selectedFpoId = MutableStateFlow<Int?>(null)
    val selectedFpoId: StateFlow<Int?> = _selectedFpoId.asStateFlow()
    private val _message = MutableSharedFlow<String>()
    val message = _message

    val fpos: StateFlow<List<FpoEntity>> = directBuyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedFpo: StateFlow<FpoEntity?> = _selectedFpoId
        .flatMapLatest { fpoId ->
            if (fpoId == null) flowOf(null) else directBuyRepository.observeById(fpoId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val activeRole: StateFlow<UserRole?> = userProfileRepository.activeProfile
        .map { UserRole.fromValue(it?.role) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun openFpo(fpoId: Int) {
        _selectedFpoId.value = fpoId
    }

    fun closeFpo() {
        _selectedFpoId.value = null
    }

    fun requestToBuy(quantityKg: Int = 100) {
        val fpo = selectedFpo.value ?: return
        viewModelScope.launch {
            val result = transactionRepository.record(
                role = activeRole.value ?: UserRole.CONSUMER,
                type = TransactionType.BUY,
                milletType = fpo.availableMillets.substringBefore(","),
                quantityKg = quantityKg,
                counterpartyName = fpo.fpoName,
                referenceContext = fpo.district
            )
            _message.emit(result.exceptionOrNull()?.message ?: "Buy request recorded")
        }
    }
}
