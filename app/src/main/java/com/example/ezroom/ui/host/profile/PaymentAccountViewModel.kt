package com.example.ezroom.ui.host.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.model.PaymentAccount
import com.example.ezroom.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

// State Management: UI State for Payment Account Management
data class PaymentAccountUiState(
    val savedAccounts: List<PaymentAccount> = emptyList(),
    val allBanks: List<Bank> = emptyList(),
    val filteredBanks: List<Bank> = emptyList(),
    val isLoading: Boolean = false,
    val bankQuery: String = "",
    val error: String? = null
)

// State Management: Payment Account Logic
class PaymentAccountViewModel(
    private val getBanks: GetBanksUseCase,
    private val getAccounts: GetPaymentAccountsUseCase,
    private val saveAccount: SavePaymentAccountUseCase,
    private val deleteAccount: DeletePaymentAccountUseCase,
    private val setDefault: SetDefaultPaymentAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentAccountUiState())
    val uiState: StateFlow<PaymentAccountUiState> = _uiState.asStateFlow()

    init {
        // Initialization: Load data
        loadSavedAccounts()
        loadBanks()
    }

    // Business Logic: Load user's saved accounts
    fun loadSavedAccounts() {
        getAccounts().onEach { accounts ->
            _uiState.update { it.copy(savedAccounts = accounts) }
        }.launchIn(viewModelScope)
    }

    // Business Logic: Load bank list from API
    private fun loadBanks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val banks = getBanks()
                _uiState.update { it.copy(allBanks = banks, filteredBanks = banks, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Không thể tải danh sách ngân hàng") }
            }
        }
    }

    // Business Logic: Search and filter banks
    fun onBankSearch(query: String) {
        _uiState.update { state ->
            state.copy(
                bankQuery = query,
                filteredBanks = state.allBanks.filter { 
                    it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true) 
                }
            )
        }
    }

    // Business Logic: Set default account
    fun onSetDefault(accountId: String) {
        viewModelScope.launch {
            setDefault(accountId)
            loadSavedAccounts()
        }
    }

    // Business Logic: Delete account
    fun onDelete(accountId: String) {
        viewModelScope.launch {
            deleteAccount(accountId)
            loadSavedAccounts()
        }
    }

    // Business Logic: Add new account
    fun onAddAccount(bank: Bank, number: String, owner: String) {
        viewModelScope.launch {
            val newAccount = PaymentAccount(
                id = UUID.randomUUID().toString(),
                bank = bank,
                accountNumber = number,
                accountOwner = owner
            )
            saveAccount(newAccount)
            loadSavedAccounts()
        }
    }
}
