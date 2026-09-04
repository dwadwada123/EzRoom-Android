package com.example.ezroom.domain.model

import com.google.gson.annotations.SerializedName

data class OtherCostItem(
    val reason: String,
    val amount: Long
)

data class Invoice(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String = "",
    val roomId: String = "",
    val roomName: String = "",
    val period: String = "",
    val roomPrice: Long = 0L,
    val oldElectricity: Int = 0,
    val newElectricity: Int = 0,
    val oldWater: Int = 0,
    val newWater: Int = 0,
    val otherCosts: List<OtherCostItem> = emptyList(),
    val status: InvoiceStatus = InvoiceStatus.UNPAID,
    val type: TransactionType = TransactionType.RENT,
    val dateCreated: String = "",
    val paymentMethod: String? = null,
    val electricityPrice: Long = 3500L,
    val waterPrice: Long = 15000L,
    val renterName: String? = null,
    val renterPhone: String? = null,
    val totalAmount: Long = 0L
) {
    val totalOtherCosts: Long
        get() = (otherCosts ?: emptyList()).sumOf { it.amount }

    val calculatedTotalAmount: Long
        get() {
            if (totalAmount > 0L) return totalAmount
            val elecDiff = (newElectricity - oldElectricity).coerceAtLeast(0)
            val waterDiff = (newWater - oldWater).coerceAtLeast(0)
            return roomPrice + (elecDiff * electricityPrice) + (waterDiff * waterPrice) + totalOtherCosts
        }
}
