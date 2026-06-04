package com.example.ezroom.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

data class Conversation(
    val id: String,
    val userName: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean = false,
    val lastMsgFromMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onNavigateBack: () -> Unit,
    onConversationClick: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val mockConversations = remember {
        listOf(
            Conversation("1", "Trần Vũ Phong", "Phòng này còn không ạ?", "10:30", 2, isOnline = true),
            Conversation("2", "Bùi Nhật Nguyệt", "Dạ em đã nhận được tiền cọc rồi ạ", "Hôm qua", 0, lastMsgFromMe = true),
            Conversation("3", "Trần Lê Quốc Dũng", "Anh gửi em vị trí chính xác nhé.", "Thứ 2", 1, isOnline = true),
            Conversation("4", "Phạm Đức Anh Tài", "Hợp đồng đã được ký chưa anh?", "01/05", 0),
            Conversation("5", "Trần Tâm", "Cảm ơn bạn đã hỗ trợ nhiệt tình", "28/04", 0, lastMsgFromMe = true)
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text("TIN NHẮN", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary)
                },
                // Thêm nút Back ở đây
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Thanh Search Bar (Giữ nguyên như bản nâng cấp trước)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Tìm kiếm hội thoại...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f)
                )
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(mockConversations) { chat ->
                    ConversationListItem(chat = chat, onClick = { onConversationClick(chat.id, chat.userName) })
                }
            }
        }
    }
}

@Composable
fun ConversationListItem(
    chat: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Khối ảnh đại diện có tích xanh Online
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Hiển thị chữ cái đầu tên nếu không có ảnh
                Text(
                    text = chat.userName.take(1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )
            }

            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(TealAccent))
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nội dung tin nhắn
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.userName,
                    fontSize = 16.sp,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    color = OnBackgroundLight
                )
                Text(
                    text = chat.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (chat.lastMsgFromMe) "Bạn: ${chat.lastMessage}" else chat.lastMessage,
                    fontSize = 14.sp,
                    color = if (chat.unreadCount > 0) OnBackgroundLight else Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = OrangePrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
    // Đường gạch ngang mảnh đồng bộ với màn hình Hóa đơn
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = OnBackgroundLight.copy(alpha = 0.05f)
    )
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    EzRoomTheme {
        ChatListScreen(
            onNavigateBack = {},
            onConversationClick = { _, _ -> }
        )
    }
}
