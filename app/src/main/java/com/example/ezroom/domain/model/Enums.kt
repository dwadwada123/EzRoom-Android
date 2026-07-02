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
    UNPAID, PAID
}

enum class RoomStatus(val title: String) {
    ACTIVE("Đang hiển thị"),
    RENTED("Đã cho thuê"),
    PENDING("Chờ duyệt"),
    HIDDEN("Đã ẩn bài")
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
