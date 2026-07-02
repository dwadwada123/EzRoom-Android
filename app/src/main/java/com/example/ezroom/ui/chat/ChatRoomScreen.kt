package com.example.ezroom.ui.chat

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.ChatRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetConversationsUseCase
import com.example.ezroom.domain.usecase.GetMessagesUseCase
import com.example.ezroom.domain.usecase.SendMessageUseCase
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    conversationId: String = "conv_1",
    userName: String = "Trần Vũ Phong",
    isOnline: Boolean = true,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(
        factory = viewModelFactory {
            val repo = ChatRepositoryImpl()
            ChatViewModel(
                GetConversationsUseCase(repo),
                GetMessagesUseCase(repo),
                SendMessageUseCase(repo)
            )
        }
    )
) {
    val uiState by viewModel.roomState.collectAsState()
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId, userName)
    }

    if (showAttachmentMenu) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentMenu = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            AttachmentMenuContent(
                onOptionClick = {
                    showAttachmentMenu = false
                }
            )
        }
    }

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = uiState.otherPartyName.take(1).ifEmpty { "C" }, 
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold, 
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.otherPartyName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isOnline) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessEmerald))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = if (isOnline) "Đang hoạt động" else "Ngoại tuyến",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isOnline) SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, "tel:0898990543".toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Call, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RectangleShape),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { showAttachmentMenu = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Gửi tin nhắn...", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    IconButton(
                        onClick = { 
                            if (messageText.isNotBlank()) {
                                viewModel.onSendMessage(conversationId, messageText)
                                messageText = "" 
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, 
                            null, 
                            tint = if (messageText.isNotBlank()) Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(
                items = uiState.messages,
                key = { it.id }
            ) { message ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(initialOffsetY = { 20 }) + fadeIn()
                ) {
                    ChatBubble(message)
                }
            }
        }
    }
}

@Composable
fun AttachmentMenuContent(onOptionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Chia sẻ nội dung",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AttachmentOption(
                icon = Icons.Default.Image,
                label = "Hình ảnh",
                color = Color(0xFF10B981),
                onClick = onOptionClick,
                modifier = Modifier.weight(1f)
            )
            AttachmentOption(
                icon = Icons.Default.LocationOn,
                label = "Vị trí",
                color = Color(0xFFEF4444),
                onClick = onOptionClick,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = CircleShape
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isMe = message.isFromMe
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface

    val shape = if (isMe)
        RoundedCornerShape(24.dp, 24.dp, 4.dp, 24.dp)
    else
        RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = if (isMe) 8.dp else 2.dp,
                modifier = Modifier.widthIn(max = 280.dp),
                border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)) else null
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
            Text(
                text = "10:00", 
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
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
