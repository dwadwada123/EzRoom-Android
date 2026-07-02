package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun getInvoices(): Flow<List<Invoice>>
    suspend fun createInvoice(invoice: Invoice)
    suspend fun updateInvoiceStatus(invoiceId: String, status: com.example.ezroom.domain.model.InvoiceStatus)
}
