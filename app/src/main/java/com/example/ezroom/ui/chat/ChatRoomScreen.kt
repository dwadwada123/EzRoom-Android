package com.example.ezroom.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.*
import com.example.ezroom.ui.theme.*

// Mock data
data class MessageUI(
    val message: Message? = null,
    val dateHeader: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    userName: String = "Trần Vũ Phong",
    isOnline: Boolean = true,
    // Event callbacks
    onNavigateBack: () -> Unit
) {
    // State definitions
    var messageText by remember { mutableStateOf("") }

    // Mock data
    val messages = remember {
        mutableStateListOf(
            MessageUI(dateHeader = "10 Tháng 05, 2026"),
            MessageUI(message = Message("1", "other", "Chào anh, phòng Q7 bên mình còn trống không ạ?", 1715310000000L, false)),
            MessageUI(message = Message("2", "me", "Chào bạn, phòng đó vẫn còn nhé. Bạn muốn xem lúc nào?", 1715310120000L, true)),
            MessageUI(dateHeader = "Hôm nay"),
            MessageUI(message = Message("4", "other", "Dạ chiều nay tầm 5h30 mình qua xem được không anh?", 1715396700000L, false)),
            MessageUI(message = Message("5", "me", "Được nhé, khi nào đến bạn gọi số 090xxxxxxx.", 1715397000000L, true))
        )
    }

    // Main layout container
    Scaffold(
        containerColor = BackgroundLight,
        // Top app bar
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = OrangePrimary)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = OrangePrimary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(userName.take(1), fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = OnBackgroundLight
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isOnline) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TealAccent))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (isOnline) "Đang hoạt động" else "Ngoại tuyến",
                                    fontSize = 12.sp,
                                    color = if (isOnline) TealAccent else Color.Gray
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Call, null, tint = OrangePrimary)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, null, tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    scrolledContainerColor = SurfaceLight
                )
            )
        },
        // Input fields group
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = SurfaceLight
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Image, null, tint = Color.Gray)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Gửi tin nhắn cho chủ trọ...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(28.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f),
                            focusedContainerColor = BackgroundLight,
                            unfocusedContainerColor = BackgroundLight
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = { if (messageText.isNotBlank()) messageText = "" },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = OrangePrimary,
                            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        // Content scroll area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = messages,
                key = { it.message?.id ?: it.dateHeader ?: "" }
            ) { msgUI ->
                if (msgUI.dateHeader != null) {
                    DateSeparator(msgUI.dateHeader)
                } else if (msgUI.message != null) {
                    ChatBubble(msgUI.message)
                }
            }
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = OnBackgroundLight.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isMe = message.isFromMe
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) OrangePrimary else SurfaceLight
    val textColor = if (isMe) Color.White else OnBackgroundLight

    val shape = if (isMe)
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    else
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = if (isMe) 2.dp else 1.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = textColor,
                    fontSize = 15.sp, 
                    lineHeight = 22.sp
                )
            }
            Text(
                text = "10:00", 
                fontSize = 11.sp, 
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomPreview() {
    EzRoomTheme {
        ChatRoomScreen(onNavigateBack = {})
    }
}
