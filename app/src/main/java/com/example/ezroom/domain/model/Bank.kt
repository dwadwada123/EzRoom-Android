package com.example.ezroom.domain.model

// Domain Model: Bank Information (VietQR Format)
data class Bank(
    val id: Int,
    val name: String,     // Full name: Ngân hàng TMCP Ngoại thương Việt Nam
    val code: String,     // Short name: VCB
    val bin: String,      // BIN: 970436
    val logo: String      // Logo URL
)

// Domain Model: User's Saved Payment Account
data class PaymentAccount(
    val id: String,
    val bank: Bank,
    val accountNumber: String,
    val accountOwner: String,
    val isDefault: Boolean = false
)
