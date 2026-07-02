package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    operator fun invoke(forRenter: Boolean, userName: String): Flow<Try<List<Appointment>>> {
        return repository.getAppointments().map<List<Appointment>, Try<List<Appointment>>> { list ->
            val filtered = if (forRenter) {
                list.filter { it.renterName == userName }
            } else {
                list.filter { it.hostName == userName }
            }
            Try.Success(filtered)
        }.catch {
            emit(Try.Failure(AppError.Unknown(it)))
        }
    }
}
