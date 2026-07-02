package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Contract
import kotlinx.coroutines.flow.Flow

interface ContractRepository {
    fun getContracts(): Flow<List<Contract>>
    suspend fun signContract(contractId: String)
    suspend fun createContract(contract: Contract)
}
