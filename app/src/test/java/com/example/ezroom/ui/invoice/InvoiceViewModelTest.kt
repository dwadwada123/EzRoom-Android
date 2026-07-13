package com.example.ezroom.ui.invoice

import com.example.ezroom.MainDispatcherRule
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.model.InvoiceStatus
import com.example.ezroom.domain.repository.InvoiceRepository
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class InvoiceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getInvoicesUseCase: GetInvoicesUseCase = mockk()
    private val repository: InvoiceRepository = mockk()

    @Test
    fun `init loads invoices`() = runTest {
        val mockInvoices = listOf(mockk<Invoice>())
        every { getInvoicesUseCase(forRenter = true) } returns flowOf(Try.Success(mockInvoices))

        val viewModel = InvoiceViewModel(getInvoicesUseCase, repository)
        
        advanceUntilIdle()

        assertEquals(mockInvoices, viewModel.uiState.value.invoices)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `markAsPaid calls repository and reloads`() = runTest {
        every { getInvoicesUseCase(any()) } returns flowOf(Try.Success(emptyList()))
        coEvery { repository.updateInvoiceStatus("i1", InvoiceStatus.PAID) } returns Unit

        val viewModel = InvoiceViewModel(getInvoicesUseCase, repository)
        
        advanceUntilIdle()
        
        viewModel.markAsPaid("i1")
        
        advanceUntilIdle()

        coVerify { repository.updateInvoiceStatus("i1", InvoiceStatus.PAID) }
    }
}
