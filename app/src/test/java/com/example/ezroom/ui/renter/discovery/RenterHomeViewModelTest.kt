package com.example.ezroom.ui.renter.discovery

import app.cash.turbine.test
import com.example.ezroom.MainDispatcherRule
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.DiscoveryItem
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class RenterHomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDiscoveryItemsUseCase: GetDiscoveryItemsUseCase = mockk()
    
    @Test
    fun `init loads discovery items and updates uiState`() = runTest {
        val mockItems = listOf(mockk<DiscoveryItem>())
        every { getDiscoveryItemsUseCase() } returns flowOf(Try.Success(mockItems))

        val viewModel = RenterHomeViewModel(getDiscoveryItemsUseCase)

        viewModel.uiState.test {
            // Skip initial loading state or handle it
            val state = awaitItem()
            assertEquals(mockItems, state.discoveryItems)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `onQueryChange updates query in uiState`() = runTest {
        every { getDiscoveryItemsUseCase() } returns flowOf(Try.Success(emptyList()))
        val viewModel = RenterHomeViewModel(getDiscoveryItemsUseCase)

        viewModel.onQueryChange("new query")
        
        assertEquals("new query", viewModel.uiState.value.query)
    }
}
