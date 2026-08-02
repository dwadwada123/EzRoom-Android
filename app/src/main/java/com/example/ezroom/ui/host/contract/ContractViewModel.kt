package com.example.ezroom.ui.host.contract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
import com.example.ezroom.domain.usecase.GetRoomsUseCase
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ContractUiState(
    val contracts: List<Contract> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false
)

class ContractViewModel(
    private val getContracts: GetContractsUseCase,
    private val signContractUseCase: SignContractUseCase,
    private val repository: ContractRepository,
    private val getRooms: GetRoomsUseCase,
    private val isHost: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContractUiState())
    val uiState: StateFlow<ContractUiState> = _uiState.asStateFlow()

    init {
        loadContracts()
        loadRooms()
    }

    private fun loadContracts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            val user = com.example.ezroom.util.TokenManager.getUser()
            val name = user?.name ?: "Nguyễn Văn A"
            getContracts(userName = name, isHost = isHost)
                .onEach { list ->
                    _uiState.update { it.copy(contracts = list, isLoading = false) }
                }
                .collect()
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            getRooms()
                .onEach { result ->
                    if (result is com.example.ezroom.core.Try.Success) {
                        val activeRooms = result.value.filter { it.status == com.example.ezroom.domain.model.RoomStatus.ACTIVE }
                        _uiState.update { it.copy(rooms = activeRooms) }
                    }
                }
                .collect()
        }
    }

    fun createContract(contract: Contract) {
        viewModelScope.launch {
            repository.createContract(contract)
            loadContracts()
        }
    }

    fun signContract(contractId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            signContractUseCase(contractId)
            loadContracts()
            onComplete()
        }
    }
}
