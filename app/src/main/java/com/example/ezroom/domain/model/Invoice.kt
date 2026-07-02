package com.example.ezroom.domain.model

data class OtherCostItem(
    val reason: String,
    val amount: Long
)

data class Invoice(
    val id: String,
    val roomId: String,
    val roomName: String,
    val period: String,
    val roomPrice: Long,
    val oldElectricity: Int,
    val newElectricity: Int,
    val oldWater: Int,
    val newWater: Int,
    val otherCosts: List<OtherCostItem> = emptyList(),
    val status: InvoiceStatus,
    val type: TransactionType = TransactionType.RENT,
    val dateCreated: String,
    val paymentMethod: String? = null
) {
    val totalOtherCosts: Long
        get() = otherCosts.sumOf { it.amount }
}
