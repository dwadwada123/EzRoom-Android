package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.remote.BankApi
import com.example.ezroom.data.remote.BankDto
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.model.PaymentAccount
import com.example.ezroom.domain.repository.BankRepository
import com.example.ezroom.domain.repository.PaymentAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Data Layer: Bank Repository Implementation
class BankRepositoryImpl(
    private val api: BankApi = BankApi.create()
) : BankRepository {
    override suspend fun getBanks(): List<Bank> {
        val response = api.getBanks()
        return if (response.code == "00") {
            response.data.map { it.toDomain() }
        } else {
            emptyList()
        }
    }
}

// Data Mapping: Bank DTO to Domain
fun BankDto.toDomain() = Bank(
    id = id,
    name = name,
    code = shortName ?: code,
    bin = bin,
    logo = logo
)

// Data Layer: Payment Account Repository Implementation
class PaymentAccountRepositoryImpl : PaymentAccountRepository {
    override fun getAccounts(): Flow<List<PaymentAccount>> = flow {
        // Business Logic: Emit saved accounts
        emit(MockData.paymentAccounts)
    }

    override suspend fun saveAccount(account: PaymentAccount) {
        val index = MockData.paymentAccounts.indexOfFirst { it.id == account.id }
        if (index != -1) {
            MockData.paymentAccounts[index] = account
        } else {
            // If this is the first account, make it default
            val isFirst = MockData.paymentAccounts.isEmpty()
            MockData.paymentAccounts.add(account.copy(isDefault = isFirst))
        }
    }

    override suspend fun deleteAccount(accountId: String) {
        val index = MockData.paymentAccounts.indexOfFirst { it.id == accountId }
        if (index != -1) {
            val wasDefault = MockData.paymentAccounts[index].isDefault
            MockData.paymentAccounts.removeAt(index)
            
            // If we deleted the default account, set a new one
            if (wasDefault && MockData.paymentAccounts.isNotEmpty()) {
                MockData.paymentAccounts[0] = MockData.paymentAccounts[0].copy(isDefault = true)
            }
        }
    }

    override suspend fun setDefaultAccount(accountId: String) {
        MockData.paymentAccounts.forEachIndexed { index, account ->
            MockData.paymentAccounts[index] = account.copy(isDefault = account.id == accountId)
        }
    }
}
