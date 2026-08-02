package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.remote.ContractApi
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class ContractRepositoryImpl : ContractRepository {
    private val contractApi = NetworkClient.createService<ContractApi>()
    
    override fun getContracts(): Flow<List<Contract>> = flow {
        try {
            val list = contractApi.getContracts()
            emit(list)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getContractById(contractId: String): Contract? {
        return try {
            val list = contractApi.getContracts()
            list.find { it.id == contractId }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateContractStatus(contractId: String, status: ContractStatus) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            MockData.contracts[index] = MockData.contracts[index].copy(status = status)
        }
    }

    override suspend fun signContract(contractId: String) {
        try {
            contractApi.signContract(contractId)
        } catch (e: Exception) {
            // Ignored, fallback to local state
        }
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val current = MockData.contracts[index]
            val isZeroDeposit = current.depositAmount == 0L
            MockData.contracts[index] = current.copy(
                dateSigned = sdf.format(Date()),
                status = if (isZeroDeposit) ContractStatus.ACTIVE else ContractStatus.WAITING_DEPOSIT,
                depositStatus = if (isZeroDeposit) DepositStatus.FROZEN else DepositStatus.UNPAID
            )
        }
    }

    override suspend fun createContract(contract: Contract) {
        try {
            contractApi.createContract(contract)
        } catch (e: Exception) {
            // Ignored, proceed to local update
        }
        MockData.contracts.add(contract)
    }

    suspend fun getPaymentQR(contractId: String): com.example.ezroom.data.remote.PaymentResponse {
        return try {
            contractApi.getPaymentQR(contractId)
        } catch (e: Exception) {
            com.example.ezroom.data.remote.PaymentResponse(success = false, error = e.message)
        }
    }

    suspend fun terminateContract(contractId: String, reason: String, cancelBy: String): Boolean {
        return try {
            val res = contractApi.terminateContract(contractId, mapOf("reason" to reason, "cancelBy" to cancelBy))
            res.success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun confirmPaymentWithVerification(contractId: String): Pair<Boolean, String?> {
        return try {
            val response = contractApi.confirmPayment(contractId)
            if (response.success) {
                Pair(true, null)
            } else {
                Pair(false, response.error ?: "PayOS chưa ghi nhận giao dịch chuyển khoản.")
            }
        } catch (e: Exception) {
            val errorMsg = try {
                val retrofitErr = (e as? retrofit2.HttpException)?.response()?.errorBody()?.string()
                val jsonErr = com.google.gson.JsonParser.parseString(retrofitErr ?: "").asJsonObject.get("error")?.asString
                jsonErr
            } catch (ex: Exception) { null }
            Pair(false, errorMsg ?: "Hệ thống PayOS chưa nhận được chuyển khoản. Vui lòng hoàn tất chuyển khoản và thử lại.")
        }
    }

    override suspend fun markDepositPaid(contractId: String) {
        try {
            contractApi.confirmPayment(contractId)
        } catch (e: Exception) {
            // Local fallback
        }
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val current = MockData.contracts[index]
            MockData.contracts[index] = current.copy(
                status = ContractStatus.ACTIVE,
                depositStatus = DepositStatus.FROZEN,
                isProtected = true
            )
        }
    }

    override suspend fun requestRefund(contractId: String, refundInfo: RefundInfo) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val current = MockData.contracts[index]
            MockData.contracts[index] = current.copy(
                status = ContractStatus.CANCELLED,
                depositStatus = DepositStatus.REFUNDED,
                refundInfo = refundInfo
            )
        }
    }

    override suspend fun reportDispute(contractId: String, reason: String) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val current = MockData.contracts[index]
            MockData.contracts[index] = current.copy(
                status = ContractStatus.DISPUTED,
                cancelReason = reason
            )
        }
    }
}
