package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class ContractRepositoryImpl : ContractRepository {
    override fun getContracts(): Flow<List<Contract>> = flow {
        emit(MockData.contracts)
    }

    override suspend fun signContract(contractId: String) {
        val index = MockData.contracts.indexOfFirst { it.id == contractId }
        if (index != -1) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            MockData.contracts[index] = MockData.contracts[index].copy(
                dateSigned = sdf.format(Date())
            )
        }
    }

    override suspend fun createContract(contract: Contract) {
        MockData.contracts.add(contract)
    }
}
