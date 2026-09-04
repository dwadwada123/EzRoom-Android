package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.model.ContractStatus
import com.example.ezroom.domain.model.RefundInfo
import kotlinx.coroutines.flow.Flow

// Domain Repository: Contract and Fintech Management
interface ContractRepository {
    fun getContracts(): Flow<List<Contract>>
    suspend fun getContractById(contractId: String): Contract?
    suspend fun updateContractStatus(contractId: String, status: ContractStatus)
    suspend fun signContract(contractId: String)
    suspend fun createContract(contract: Contract): Contract
    
    // Fintech Actions
    suspend fun markDepositPaid(contractId: String) // Simulate webhook
    suspend fun requestRefund(contractId: String, refundInfo: RefundInfo)
    suspend fun reportDispute(contractId: String, reason: String)
}
