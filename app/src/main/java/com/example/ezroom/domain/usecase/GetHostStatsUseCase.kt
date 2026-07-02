package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.HostStats
import com.example.ezroom.domain.model.RoomStatus
import com.example.ezroom.domain.repository.RoomRepository
import com.example.ezroom.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetHostStatsUseCase(
    private val roomRepository: RoomRepository,
    private val appointmentRepository: AppointmentRepository
) {
    operator fun invoke(timeRange: String): Flow<HostStats> {
        return combine(
            roomRepository.getRooms(),
            appointmentRepository.getAppointments()
        ) { rooms, appointments ->
            val total = rooms.size
            val rented = rooms.count { it.status == RoomStatus.RENTED }
            val vacant = total - rented
            
            // Mocking revenue change based on time range
            val revenueValue = when {
                timeRange == "Tháng này" -> "45.000.000 đ"
                timeRange == "Tháng trước" -> "42.500.000 đ"
                timeRange == "3 tháng qua" -> "128.000.000 đ"
                timeRange.contains("-") -> "38.200.000 đ" // Custom range
                else -> "45.000.000 đ"
            }

            HostStats(
                totalRooms = total,
                vacantRooms = vacant,
                rentedRooms = rented,
                expectedRevenue = revenueValue,
                totalAppointments = appointments.size,
                occupancyRate = if (total > 0) (rented.toFloat() / total) else 0f
            )
        }
    }
}
