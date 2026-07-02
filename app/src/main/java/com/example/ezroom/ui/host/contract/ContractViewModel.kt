package com.example.ezroom.ui.host.contract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ContractUiState(
    val contracts: List<Contract> = emptyList(),
    val isLoading: Boolean = false
)

class ContractViewModel(
    private val getContracts: GetContractsUseCase,
    private val signContractUseCase: SignContractUseCase,
    private val repository: ContractRepository,
    private val isHost: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContractUiState())
    val uiState: StateFlow<ContractUiState> = _uiState.asStateFlow()

    init {
        loadContracts()
    }

    private fun loadContracts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            getContracts(userName = "Nguyễn Văn A", isHost = isHost)
                .onEach { list ->
                    _uiState.update { it.copy(contracts = list, isLoading = false) }
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

    fun signContract(contractId: String) {
        viewModelScope.launch {
            signContractUseCase(contractId)
            loadContracts()
        }
    }
}
