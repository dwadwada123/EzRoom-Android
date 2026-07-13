package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

// Data Layer: Contract Repository Implementation
class ContractRepositoryImpl : ContractRepository {
    
    override fun getContracts(): Flow<List<Contract>> = flow {
        // Business Logic: Emit all mock contracts
        emit(MockData.contracts)
    }

    override suspend fun getContractById(contractId: String): Contract? {
        return MockData.contracts.find { it.id == contractId }
    }

    override suspend fun updateContractStatus(contractId: String, status: ContractStatus) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            MockData.contracts[index] = MockData.contracts[index].copy(status = status)
        }
    }

    override suspend fun signContract(contractId: String) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val current = MockData.contracts[index]
            MockData.contracts[index] = current.copy(
                dateSigned = sdf.format(Date()),
                status = ContractStatus.WAITING_DEPOSIT,
                depositStatus = DepositStatus.UNPAID
            )
        }
    }

    override suspend fun createContract(contract: Contract) {
        MockData.contracts.add(contract)
    }

    override suspend fun markDepositPaid(contractId: String) {
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
