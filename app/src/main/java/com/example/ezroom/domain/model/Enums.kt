package com.example.ezroom.domain.model

enum class RoomStructure(val displayName: String) {
    SINGLE("Phòng đơn"), WHOLE("Nguyên căn"), APARTMENT("Căn hộ")
}

enum class PropertyType(val displayName: String) {
    SINGLE("Đăng tin lẻ"), COMPLEX("Quản lý dãy trọ/tòa nhà")
}

enum class AppointmentStatus {
    PENDING, APPROVED, CANCELED, RESCHEDULED
}

enum class InvoiceStatus {
    UNPAID, PAID
}

enum class DepositStatus {
    UNPAID,           // Pending QR payment
    FROZEN,           // Escrow locked
    DISBURSED,        // Disbursed to host
    REFUNDED,         // Refunded to renter
    COMPENSATED       // Compensated to host
}

enum class ContractStatus {
    DRAFT,            // Draft
    WAITING_SIGN,     // Pending renter signature
    WAITING_DEPOSIT,  // Signed, pending deposit
    ACTIVE,           // Active lease
    CANCELLED,        // Cancelled before start
    TERMINATED,       // Terminated
    DISPUTED          // In dispute
}

enum class RoomStatus(val title: String) {
    ACTIVE("Đang hiển thị"),
    RENTED("Đã cho thuê"),
    PENDING("Chờ duyệt"),
    HIDDEN("Đã ẩn bài"),
    REMOVED("Bị gỡ"),
    DELETED("Đã xóa")
}

// Room removal details
data class RoomRemovalInfo(
    val reason: String,
    val removedDate: String,
    val autoDeleteDate: String,
    val appealText: String? = null,
    val appealImages: List<String> = emptyList(),
    val appealStatus: String? = "PENDING"
)

data class Amenity(
    val name: String,
    val compensationAmount: Long = 0L,
    val iconRes: Int? = null
)

data class RoomImage(
    val url: String? = null,
    val resId: Int? = null,
    val category: String? = null
)

enum class TransactionType {
    DEPOSIT,      // Security deposit
    RENT,         // Monthly rent
    COMPENSATION  // Damage compensation
}
