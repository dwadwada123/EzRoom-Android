package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.model.InvoiceStatus
import com.example.ezroom.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InvoiceRepositoryImpl : InvoiceRepository {
    override fun getInvoices(): Flow<List<Invoice>> = flow {
        emit(MockData.invoices)
    }

    override suspend fun createInvoice(invoice: Invoice) {
        MockData.invoices.add(invoice)
    }

    override suspend fun updateInvoiceStatus(invoiceId: String, status: InvoiceStatus) {
        val index = MockData.invoices.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            MockData.invoices[index] = MockData.invoices[index].copy(status = status)
        }
    }
}
