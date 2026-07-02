package com.example.ezroom.ui.chat

import app.cash.turbine.test
import com.example.ezroom.MainDispatcherRule
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import com.example.ezroom.domain.usecase.GetConversationsUseCase
import com.example.ezroom.domain.usecase.GetMessagesUseCase
import com.example.ezroom.domain.usecase.SendMessageUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getConversationsUseCase: GetConversationsUseCase = mockk()
    private val getMessagesUseCase: GetMessagesUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()

    @Test
    fun `init loads conversations`() = runTest {
        val mockConversations = listOf(mockk<Conversation>())
        every { getConversationsUseCase() } returns flowOf(Try.Success(mockConversations))

        val viewModel = ChatViewModel(getConversationsUseCase, getMessagesUseCase, sendMessageUseCase)

        viewModel.listState.test {
            val state = awaitItem()
            assertEquals(mockConversations, state.conversations)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `loadMessages updates roomState`() = runTest {
        every { getConversationsUseCase() } returns flowOf(Try.Success(emptyList()))
        val mockMessages = listOf(mockk<Message>())
        every { getMessagesUseCase("c1") } returns flowOf(Try.Success(mockMessages))

        val viewModel = ChatViewModel(getConversationsUseCase, getMessagesUseCase, sendMessageUseCase)
        viewModel.loadMessages("c1", "Test User")

        viewModel.roomState.test {
            val state = awaitItem()
            assertEquals(mockMessages, state.messages)
            assertEquals("Test User", state.otherPartyName)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `onSendMessage calls usecase and reloads`() = runTest {
        every { getConversationsUseCase() } returns flowOf(Try.Success(emptyList()))
        every { getMessagesUseCase("c1") } returns flowOf(Try.Success(emptyList()))
        coEvery { sendMessageUseCase("c1", "Hello") } returns Unit

        val viewModel = ChatViewModel(getConversationsUseCase, getMessagesUseCase, sendMessageUseCase)
        viewModel.onSendMessage("c1", "Hello")

        io.mockk.coVerify { sendMessageUseCase("c1", "Hello") }
    }
}
