package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.HostStats
import com.example.ezroom.domain.model.RoomStatus
import com.example.ezroom.domain.model.InvoiceStatus
import com.example.ezroom.domain.repository.RoomRepository
import com.example.ezroom.domain.repository.AppointmentRepository
import com.example.ezroom.domain.repository.ContractRepository
import com.example.ezroom.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import java.util.Locale

class GetHostStatsUseCase(
    private val roomRepository: RoomRepository,
    private val appointmentRepository: AppointmentRepository,
    private val contractRepository: ContractRepository,
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(timeRange: String): Flow<HostStats> {
        return combine(
            roomRepository.getHostRooms(),
            appointmentRepository.getAppointments(),
            contractRepository.getContracts(),
            invoiceRepository.getInvoices()
        ) { rooms, appointments, contracts, invoices ->
            val total = rooms.size
            val rented = rooms.count { it.status == RoomStatus.RENTED }
            val vacant = total - rented

            // Calculate actual NET revenue from PAID invoices matching the time range
            val totalRevenue = invoices
                .filter { it.status == InvoiceStatus.PAID && isInvoiceInTimeRange(it, timeRange) }
                .sumOf { invoice ->
                    val commission = (invoice.roomPrice * 0.05).toLong()
                    invoice.calculatedTotalAmount - commission
                }

            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            val revenueValue = formatter.format(totalRevenue)
                .replace("₫", "đ")
                .replace("VND", "đ")
                .trim()

            HostStats(
                totalRooms = total,
                vacantRooms = vacant,
                rentedRooms = rented,
                expectedRevenue = revenueValue,
                totalAppointments = appointments.size,
                occupancyRate = if (total > 0) (rented.toFloat() / total) else 0f,
                totalContracts = contracts.size
            )
        }
    }

    private fun isInvoiceInTimeRange(invoice: com.example.ezroom.domain.model.Invoice, timeRange: String): Boolean {
        if (timeRange.isBlank() || timeRange == "Tất cả") return true

        val invoiceDate = parseInvoiceDate(invoice) ?: return true

        // 1. Custom Date Range: e.g. "01/07/2026 - 25/07/2026" or "01/07/2026 đến 25/07/2026"
        val customRangeRegex = Regex("""(\d{1,2}/\d{1,2}/\d{4})\s*(?:-|đến)\s*(\d{1,2}/\d{1,2}/\d{4})""")
        val matchResult = customRangeRegex.find(timeRange)
        if (matchResult != null) {
            val (startStr, endStr) = matchResult.destructured
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            try {
                val startDate = sdf.parse(startStr)?.let {
                    java.util.Calendar.getInstance().apply {
                        time = it
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }.time
                }
                val endDate = sdf.parse(endStr)?.let {
                    java.util.Calendar.getInstance().apply {
                        time = it
                        set(java.util.Calendar.HOUR_OF_DAY, 23)
                        set(java.util.Calendar.MINUTE, 59)
                        set(java.util.Calendar.SECOND, 59)
                        set(java.util.Calendar.MILLISECOND, 999)
                    }.time
                }
                if (startDate != null && endDate != null) {
                    return invoiceDate.time >= startDate.time && invoiceDate.time <= endDate.time
                }
            } catch (_: Exception) {}
        }

        // 2. Preset Ranges
        val invCal = java.util.Calendar.getInstance().apply { time = invoiceDate }
        val invMonth = invCal.get(java.util.Calendar.MONTH) + 1
        val invYear = invCal.get(java.util.Calendar.YEAR)

        val now = java.util.Calendar.getInstance()
        val curMonth = now.get(java.util.Calendar.MONTH) + 1
        val curYear = now.get(java.util.Calendar.YEAR)

        return when {
            timeRange.contains("Tháng này", ignoreCase = true) -> {
                invMonth == curMonth && invYear == curYear
            }
            timeRange.contains("Tháng trước", ignoreCase = true) -> {
                val lastCal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.MONTH, -1) }
                val lastM = lastCal.get(java.util.Calendar.MONTH) + 1
                val lastY = lastCal.get(java.util.Calendar.YEAR)
                invMonth == lastM && invYear == lastY
            }
            timeRange.contains("3 tháng qua", ignoreCase = true) -> {
                val threeMonthsAgo = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.MONTH, -3)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                !invCal.before(threeMonthsAgo)
            }
            else -> true
        }
    }

    private fun parseInvoiceDate(invoice: com.example.ezroom.domain.model.Invoice): java.util.Date? {
        val dateFormats = listOf(
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy",
            "d/M/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )

        if (invoice.dateCreated.isNotBlank()) {
            for (fmt in dateFormats) {
                try {
                    val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.getDefault())
                    val d = sdf.parse(invoice.dateCreated.trim())
                    if (d != null) return d
                } catch (_: Exception) {}
            }
        }

        val cleanPeriod = invoice.period.replace(Regex("""(?i)tháng\s*"""), "").trim()
        val periodFormats = listOf("MM/yyyy", "M/yyyy", "MM/yy")
        for (fmt in periodFormats) {
            try {
                val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.getDefault())
                val d = sdf.parse(cleanPeriod)
                if (d != null) return d
            } catch (_: Exception) {}
        }

        return null
    }
}
