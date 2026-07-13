package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.model.PaymentAccount
import com.example.ezroom.domain.repository.BankRepository
import com.example.ezroom.domain.repository.PaymentAccountRepository
import kotlinx.coroutines.flow.Flow

// Business Logic: Get List of All Banks
class GetBanksUseCase(private val repository: BankRepository) {
    suspend operator fun invoke(): List<Bank> = repository.getBanks()
}

// Business Logic: Get User's Payment Accounts
class GetPaymentAccountsUseCase(private val repository: PaymentAccountRepository) {
    operator fun invoke(): Flow<List<PaymentAccount>> = repository.getAccounts()
}

// Business Logic: Save/Update Payment Account
class SavePaymentAccountUseCase(private val repository: PaymentAccountRepository) {
    suspend operator fun invoke(account: PaymentAccount) = repository.saveAccount(account)
}

// Business Logic: Delete Payment Account
class DeletePaymentAccountUseCase(private val repository: PaymentAccountRepository) {
    suspend operator fun invoke(accountId: String) = repository.deleteAccount(accountId)
}

// Business Logic: Set Default Payment Account
class SetDefaultPaymentAccountUseCase(private val repository: PaymentAccountRepository) {
    suspend operator fun invoke(accountId: String) = repository.setDefaultAccount(accountId)
}
