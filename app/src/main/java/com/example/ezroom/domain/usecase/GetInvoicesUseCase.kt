package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    operator fun invoke(forRenter: Boolean, roomName: String? = null): Flow<Try<List<Invoice>>> {
        return repository.getInvoices().map<List<Invoice>, Try<List<Invoice>>> { list ->
            val filtered = if (roomName != null) {
                list.filter { it.roomName == roomName }
            } else {
                list
            }
            Try.Success(filtered)
        }.catch {
            emit(Try.Failure(AppError.Unknown(it)))
        }
    }
}
