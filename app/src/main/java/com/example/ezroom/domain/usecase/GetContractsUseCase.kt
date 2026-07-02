package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetContractsUseCase(
    private val repository: ContractRepository
) {
    operator fun invoke(userName: String, isHost: Boolean): Flow<List<Contract>> {
        return repository.getContracts().map { list ->
            if (isHost) {
                list // In mock, host sees all or filter by host name if added to model
            } else {
                list.filter { it.renterName == userName }
            }
        }
    }
}
