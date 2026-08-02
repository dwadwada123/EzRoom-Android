package com.example.ezroom.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.ChatRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetConversationsUseCase
import com.example.ezroom.domain.usecase.GetMessagesUseCase
import com.example.ezroom.domain.usecase.SendMessageUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatChatListTimestamp(rawTimestamp: String?): String {
    if (rawTimestamp.isNullOrBlank()) return ""

    val date: Date = try {
        val epoch = rawTimestamp.toLongOrNull()
        if (epoch != null) {
            Date(epoch)
        } else {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            isoFormat.parse(rawTimestamp) ?: Date()
        }
    } catch (e: Exception) {
        return rawTimestamp
    }

    val calMsg = Calendar.getInstance().apply { time = date }
    val calToday = Calendar.getInstance()
    val calYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val isToday = calMsg.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
            calMsg.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)

    val isYesterday = calMsg.get(Calendar.YEAR) == calYesterday.get(Calendar.YEAR) &&
            calMsg.get(Calendar.DAY_OF_YEAR) == calYesterday.get(Calendar.DAY_OF_YEAR)

    return when {
        isToday -> "Hôm nay"
        isYesterday -> "Hôm qua"
        else -> {
            val fmt = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            fmt.format(date)
        }
    }
}

@Composable
fun ChatListScreen(
    onConversationClick: (String, String, String) -> Unit,
    viewModel: ChatViewModel = viewModel(
        factory = viewModelFactory {
            val repo = ChatRepositoryImpl()
            ChatViewModel(
                GetConversationsUseCase(repo),
                GetMessagesUseCase(repo),
                SendMessageUseCase(repo),
                com.example.ezroom.domain.usecase.UploadImageUseCase(repo)
            )
        },
    ),
) {
    val uiState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Spacer(modifier = Modifier.height(16.dp))

        // Floating Search Bar
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(56.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text("Tìm kiếm liên hệ...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) 
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                LoadingWidget()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.conversations, key = { _, it -> it.id }) { index, chat ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 40 * (index + 1) }) + fadeIn(),
                    ) {
                        ConversationListItem(
                            chat = chat, 
                            onClick = { 
                                onConversationClick(chat.id, chat.otherPartyName ?: "Người dùng", chat.otherPartyPhone ?: "0000000000") 
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationListItem(
    chat: Conversation,
    onClick: () -> Unit
) {
    // Styling: pulse online status logic can be added here
    val isOnline = chat.unreadCount > 0 // Mocking online status if there are unread messages for visual variety
    
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = chat.otherPartyName?.take(1) ?: "U",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (isOnline) {
                    Surface(
                        modifier = Modifier.size(18.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.background,
                        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(SuccessEmerald)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.otherPartyName ?: "Người dùng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatChatListTimestamp(chat.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.primary else Neutral500,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.lastMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface else Neutral500,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Chat List Screen")
@Composable
fun PreviewChatListScreen() {
    EzRoomTheme {
        ChatListScreen(
            onConversationClick = { _, _, _ -> }
        )
    }
}


