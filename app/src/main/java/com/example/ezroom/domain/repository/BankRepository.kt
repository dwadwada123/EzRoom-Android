package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.model.PaymentAccount
import kotlinx.coroutines.flow.Flow

// Domain Repository: Bank Data
interface BankRepository {
    suspend fun getBanks(): List<Bank>
}

// Domain Repository: User Payment Accounts
interface PaymentAccountRepository {
    fun getAccounts(): Flow<List<PaymentAccount>>
    suspend fun saveAccount(account: PaymentAccount)
    suspend fun deleteAccount(accountId: String)
    suspend fun setDefaultAccount(accountId: String)
}
