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
    UNPAID,           // Chờ thanh toán QR
    FROZEN,           // Tiền đang được App giữ (Escrow)
    DISBURSED,        // Đã giải ngân cho Chủ nhà
    REFUNDED,         // Đã hoàn tiền cho Người thuê
    COMPENSATED       // Đã đền bù cho Chủ nhà (Khách hủy)
}

enum class ContractStatus {
    DRAFT,            // Bản nháp
    WAITING_SIGN,     // Chờ Người thuê ký
    WAITING_DEPOSIT,  // Đã ký, chờ quét QR cọc
    ACTIVE,           // Đã cọc/Đã giải ngân, hợp đồng đang chạy
    CANCELLED,        // Đã hủy trước khi bắt đầu
    TERMINATED,       // Đã kết thúc (đúng hạn hoặc giữa chừng)
    DISPUTED          // Đang tranh chấp
}

enum class RoomStatus(val title: String) {
    ACTIVE("Đang hiển thị"),
    RENTED("Đã cho thuê"),
    PENDING("Chờ duyệt"),
    HIDDEN("Đã ẩn bài"),
    REMOVED("Bị gỡ")
}

// Data Model: Information about room removal by admin
data class RoomRemovalInfo(
    val reason: String,
    val removedDate: String,
    val autoDeleteDate: String,
    val appealText: String? = null,
    val appealImages: List<String> = emptyList()
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
    DEPOSIT,      // Tiền cọc giữ chỗ
    RENT,         // Tiền phòng định kỳ
    COMPENSATION  // Tiền đền bù thiệt hại
}
