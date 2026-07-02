package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.repository.ContractRepository

class SignContractUseCase(
    private val repository: ContractRepository
) {
    suspend operator fun invoke(contractId: String) {
        repository.signContract(contractId)
    }
}
