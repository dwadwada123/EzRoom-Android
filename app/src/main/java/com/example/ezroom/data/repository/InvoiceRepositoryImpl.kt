package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.remote.InvoiceApi
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.model.InvoiceStatus
import com.example.ezroom.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InvoiceRepositoryImpl : InvoiceRepository {
    private val invoiceApi = NetworkClient.createService<InvoiceApi>()

    override fun getInvoices(): Flow<List<Invoice>> = flow {
        try {
            val list = invoiceApi.getInvoices()
            MockData.invoices.clear()
            MockData.invoices.addAll(list)
            emit(list)
        } catch (e: Exception) {
            android.util.Log.e("InvoiceRepo", "getInvoices error", e)
            emit(MockData.invoices)
        }
    }

    override suspend fun getInvoiceById(invoiceId: String): com.example.ezroom.core.Try<Invoice> {
        return try {
            val invoice = invoiceApi.getInvoiceById(invoiceId)
            com.example.ezroom.core.Try.Success(invoice)
        } catch (e: Exception) {
            com.example.ezroom.core.Try.Failure(com.example.ezroom.core.AppError.Network(e.message ?: "Lỗi kết nối"))
        }
    }

    override suspend fun createInvoice(invoice: Invoice) {
        try {
            val res = invoiceApi.createInvoice(invoice)
            if (!res.success) {
                val errorMsg = res.error ?: res.message ?: "Không thể tạo hóa đơn"
                android.util.Log.e("InvoiceRepo", "createInvoice failed: $errorMsg")
                throw Exception(errorMsg)
            }
            android.util.Log.d("InvoiceRepo", "createInvoice success")
            try {
                val updatedList = invoiceApi.getInvoices()
                MockData.invoices.clear()
                MockData.invoices.addAll(updatedList)
            } catch (e: Exception) {
                MockData.invoices.add(0, invoice)
            }
        } catch (e: Exception) {
            android.util.Log.e("InvoiceRepo", "createInvoice exception", e)
            throw e
        }
    }

    override suspend fun updateInvoiceStatus(invoiceId: String, status: InvoiceStatus) {
        if (status == InvoiceStatus.PAID) {
            try {
                invoiceApi.payInvoice(invoiceId, mapOf("paymentMethod" to "VietQR"))
            } catch (e: Exception) {
                // Error handling
            }
        }
        val index = MockData.invoices.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            MockData.invoices[index] = MockData.invoices[index].copy(status = status)
        }
    }

    suspend fun payInvoiceWithResult(invoiceId: String, paymentMethod: String): Result<String> {
        return try {
            val res = invoiceApi.payInvoice(invoiceId, mapOf("paymentMethod" to paymentMethod))
            if (res.success) {
                val index = MockData.invoices.indexOfFirst { it.id == invoiceId }
                if (index != -1) {
                    MockData.invoices[index] = MockData.invoices[index].copy(status = InvoiceStatus.PAID)
                }
                Result.success("Thanh toán hóa đơn thành công!")
            } else {
                Result.failure(Exception(res.message ?: "Thanh toán không thành công."))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val msg = try {
                org.json.JSONObject(errorBody ?: "").optString("error", "Hệ thống PayOS chưa ghi nhận giao dịch chuyển khoản cho đơn hàng này. Vui lòng hoàn tất chuyển khoản và thử lại.")
            } catch (ex: Exception) {
                "Hệ thống PayOS chưa ghi nhận giao dịch chuyển khoản cho đơn hàng này. Vui lòng hoàn tất chuyển khoản và thử lại."
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Lỗi kết nối khi xác nhận thanh toán."))
        }
    }

    suspend fun getPaymentQR(invoiceId: String): com.example.ezroom.data.remote.PaymentResponse? {
        return try {
            invoiceApi.getPaymentQR(invoiceId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun remindInvoice(invoiceId: String): Result<String> {
        return try {
            val res = invoiceApi.remindInvoice(invoiceId)
            if (res.success) Result.success(res.message ?: "Đã gửi tin nhắn nhắc thanh toán!")
            else Result.failure(Exception(res.error ?: "Gửi nhắc nhở thất bại"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Lỗi gửi nhắc thanh toán"))
        }
    }

    suspend fun sendInvoiceReceipt(invoiceId: String): Result<String> {
        return try {
            val res = invoiceApi.sendInvoiceReceipt(invoiceId)
            if (res.success) Result.success(res.message ?: "Đã gửi biên lai hóa đơn qua tin nhắn!")
            else Result.failure(Exception(res.error ?: "Gửi biên lai thất bại"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Lỗi gửi biên lai"))
        }
    }
}
