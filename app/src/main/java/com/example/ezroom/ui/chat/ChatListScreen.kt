package com.example.ezroom.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

val ChatLogoNavy = Color(0xFF0A3366)
val ChatLogoCyan = Color(0xFF00AEEF)
val peaceSansFont = FontFamily.SansSerif

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
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New Chat */ },
                containerColor = ChatLogoCyan,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp) // Thêm chút khoảng cách cho FAB
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "New Message")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header: Title and Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Tránh tràn lên status bar
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Tin nhắn",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = peaceSansFont,
                    color = ChatLogoNavy
                )

                // Simple Segmented Control (All / Scheduled)
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabItem(
                            text = "Tất cả",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        TabItem(
                            text = "Lịch hẹn",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                    }
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = peaceSansFont,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Tìm kiếm tin nhắn hoặc người dùng",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = peaceSansFont,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    LoadingWidget()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        bottom = paddingValues.calculateBottomPadding() + 80.dp
                    )
                ) {
                    itemsIndexed(uiState.conversations, key = { _, it -> it.id }) { index, chat ->
                        ConversationListItem(
                            chat = chat,
                            onClick = {
                                onConversationClick(chat.id, chat.otherPartyName ?: "Người dùng", chat.otherPartyPhone ?: "0000000000")
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 96.dp, end = 24.dp),
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "LƯU TRỮ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = peaceSansFont,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier.width(80.dp).fillMaxHeight()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = peaceSansFont,
                color = if (isSelected) Color.Black else Color.Gray
            )
        }
    }
}

@Composable
private fun ConversationListItem(
    chat: Conversation,
    onClick: () -> Unit
) {
    val isUnread = chat.unreadCount > 0

    Surface(
        onClick = onClick,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = ChatLogoCyan.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = chat.otherPartyName?.take(1) ?: "U",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = peaceSansFont,
                        color = ChatLogoCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.otherPartyName ?: "Người dùng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = peaceSansFont,
                        color = Color.Black
                    )
                    Text(
                        text = formatChatListTimestamp(chat.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = peaceSansFont
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Double checkmarks for "read" (mocked)
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = if (isUnread) Color.Gray else ChatLogoCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = chat.lastMessage ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = peaceSansFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = if (isUnread) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = null,
                        tint = if (isUnread) Color(0xFFF59E0B) else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
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
