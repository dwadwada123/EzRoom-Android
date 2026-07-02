package com.example.ezroom.ui.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.model.InvoiceStatus
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import com.example.ezroom.domain.repository.InvoiceRepository
import com.example.ezroom.ui.renter.discovery.toMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InvoiceUiState(
    val invoices: List<Invoice> = emptyList(),
    val isLoading: Boolean = false,
    val selectedStatus: InvoiceStatus? = null,
    val error: String? = null
)

class InvoiceViewModel(
    private val getInvoices: GetInvoicesUseCase,
    private val repository: InvoiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceUiState())
    val uiState: StateFlow<InvoiceUiState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(800)
            getInvoices(forRenter = true) // General load
                .onEach { result ->
                    when (result) {
                        is Try.Success -> {
                            _uiState.update { state ->
                                val filtered = if (state.selectedStatus != null) {
                                    result.value.filter { it.status == state.selectedStatus }
                                } else {
                                    result.value
                                }
                                state.copy(invoices = filtered, isLoading = false)
                            }
                        }
                        is Try.Failure -> {
                            _uiState.update { it.copy(isLoading = false, error = result.error.toMessage()) }
                        }
                    }
                }
                .collect()
        }
    }

    fun filterByStatus(status: InvoiceStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadInvoices()
    }

    fun createInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.createInvoice(invoice)
            loadInvoices()
        }
    }

    fun markAsPaid(invoiceId: String) {
        viewModelScope.launch {
            repository.updateInvoiceStatus(invoiceId, InvoiceStatus.PAID)
            loadInvoices()
        }
    }
}
