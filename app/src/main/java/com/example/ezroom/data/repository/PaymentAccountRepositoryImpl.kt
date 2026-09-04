package com.example.ezroom.data.repository

import com.example.ezroom.domain.model.PaymentAccount
import com.example.ezroom.domain.repository.PaymentAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentAccountRepositoryImpl(
    private val authApi: com.example.ezroom.data.remote.AuthApi = com.example.ezroom.data.remote.NetworkClient.createService(),
    private val profileApi: com.example.ezroom.data.remote.UserProfileApi = com.example.ezroom.data.remote.NetworkClient.createService()
) : PaymentAccountRepository {
    override fun getAccounts(): Flow<List<PaymentAccount>> = flow {
        try {
            val response = authApi.getProfile()
            if (response.success && response.user != null) {
                com.example.ezroom.util.TokenManager.saveUser(response.user)
                emit(response.user.paymentAccounts)
            } else {
                emit(emptyList())
            }
        } catch (e: java.lang.Exception) {
            val cachedUser = com.example.ezroom.util.TokenManager.getUser()
            emit(cachedUser?.paymentAccounts ?: emptyList())
        }
    }

    override suspend fun saveAccount(account: PaymentAccount) {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            val response = profileApi.savePaymentAccount(
                com.example.ezroom.data.remote.SavePaymentAccountRequest(user.id, account)
            )
            if (response.success) {
                val updatedUser = user.copy(paymentAccounts = response.paymentAccounts)
                com.example.ezroom.util.TokenManager.saveUser(updatedUser)
                UserRepositoryImpl.updateCachedUser(updatedUser)
                android.util.Log.d("PaymentAccountRepo", "Saved payment accounts: ${response.paymentAccounts.size}")
            }
        } catch (e: java.lang.Exception) {
            android.util.Log.e("PaymentAccountRepo", "Error saving payment account", e)
        }
    }

    override suspend fun deleteAccount(accountId: String) {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            val response = profileApi.deletePaymentAccount(
                com.example.ezroom.data.remote.DeletePaymentAccountRequest(user.id, accountId)
            )
            if (response.success) {
                val updatedUser = user.copy(paymentAccounts = response.paymentAccounts)
                com.example.ezroom.util.TokenManager.saveUser(updatedUser)
            }
        } catch (e: java.lang.Exception) {
            // Error handling
        }
    }

    override suspend fun setDefaultAccount(accountId: String) {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            val response = profileApi.setDefaultPaymentAccount(
                com.example.ezroom.data.remote.SetDefaultPaymentAccountRequest(user.id, accountId)
            )
            if (response.success) {
                val updatedUser = user.copy(paymentAccounts = response.paymentAccounts)
                com.example.ezroom.util.TokenManager.saveUser(updatedUser)
            }
        } catch (e: java.lang.Exception) {
            // Error handling
        }
    }
}

class BankRepositoryImpl(
    private val api: com.example.ezroom.data.remote.BankApi = com.example.ezroom.data.remote.BankApi.create()
) : com.example.ezroom.domain.repository.BankRepository {
    override suspend fun getBanks(): List<com.example.ezroom.domain.model.Bank> {
        val response = api.getBanks()
        return if (response.code == "00") {
            response.data.map { it.toDomain() }
        } else {
            emptyList()
        }
    }
}

fun com.example.ezroom.data.remote.BankDto.toDomain() = com.example.ezroom.domain.model.Bank(
    id = id,
    name = name,
    code = shortName ?: code,
    bin = bin,
    logo = logo
)
