package com.example.ezroom.data.model

enum class RoomStructure(val displayName: String) {
    SINGLE("Phòng đơn"), WHOLE("Nguyên căn"), APARTMENT("Căn hộ")
}

enum class AppointmentStatus {
    PENDING, APPROVED, CANCELED
}

enum class InvoiceStatus {
    UNPAID, PAID
}

enum class DepositStatus {
    UNPAID, PAID
}

enum class RoomStatus(val title: String) {
    ACTIVE("Đang hiển thị"),
    RENTED("Đã cho thuê"),
    PENDING("Chờ duyệt")
}

data class Amenity(
    val name: String,
    val compensationAmount: Long = 0L,
    val iconRes: Int? = null
)

data class RoomImage(
    val url: String? = null,
    val resId: Int? = null
)

enum class TransactionType {
    DEPOSIT,      // Tiền cọc giữ chỗ
    RENT,         // Tiền phòng định kỳ
    COMPENSATION  // Tiền đền bù thiệt hại
}
