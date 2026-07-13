package com.example.ezroom.ui.renter.discovery

import app.cash.turbine.test
import com.example.ezroom.MainDispatcherRule
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.DiscoveryItem
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
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
class RenterHomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDiscoveryItemsUseCase: GetDiscoveryItemsUseCase = mockk()
    
    @Test
    fun `init loads discovery items and updates uiState`() = runTest {
        val mockItems = listOf(mockk<DiscoveryItem>())
        every { getDiscoveryItemsUseCase() } returns flowOf(Try.Success(mockItems))

        val viewModel = RenterHomeViewModel(getDiscoveryItemsUseCase)
        
        // Advance time to skip delays in init
        advanceUntilIdle()

        assertEquals(mockItems, viewModel.uiState.value.discoveryItems)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onQueryChange updates query in uiState`() = runTest {
        every { getDiscoveryItemsUseCase() } returns flowOf(Try.Success(emptyList()))
        val viewModel = RenterHomeViewModel(getDiscoveryItemsUseCase)
        
        advanceUntilIdle()

        viewModel.onQueryChange("new query")
        
        assertEquals("new query", viewModel.uiState.value.query)
    }
}
