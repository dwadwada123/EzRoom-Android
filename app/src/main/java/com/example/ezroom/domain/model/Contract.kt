package com.example.ezroom.domain.model

// Domain Model: Detailed Contract with Fintech Logic
data class Contract(
    val id: String,
    val roomId: String,
    val roomName: String,
    val address: String? = null,
    val renterName: String,
    val renterPhone: String,
    val hostName: String? = null,
    val startDate: String,
    val endDate: String,
    val depositAmount: Long,
    val depositStatus: DepositStatus = DepositStatus.UNPAID,
    val status: ContractStatus = ContractStatus.WAITING_SIGN,
    val dateCreated: String,
    val dateSigned: String? = null,
    
    // Fintech: Refund/Cancellation Info
    val cancelReason: String? = null,
    val cancelBy: String? = null, // HOST or RENTER
    val refundInfo: RefundInfo? = null,
    
    // Fintech: Disbursement Info
    val disburseDate: String? = null,
    val isProtected: Boolean = false
)

// Data Model: Information for manual refund to renter
data class RefundInfo(
    val bankName: String,
    val accountNumber: String,
    val accountOwner: String,
    val status: RefundStatus = RefundStatus.PENDING
)

enum class RefundStatus {
    PENDING, COMPLETED
}
