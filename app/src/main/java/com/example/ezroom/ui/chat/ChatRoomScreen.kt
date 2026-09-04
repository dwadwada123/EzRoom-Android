package com.example.ezroom.ui.chat

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
    phoneNumber: String = "",
    onNavigateBack: () -> Unit,
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
    val uiState by viewModel.roomState.collectAsState()
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState()

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
                val fileName = "img_${System.currentTimeMillis()}.jpg"
                viewModel.uploadImageAndSend(conversationId, bytes, fileName, mimeType)
            }
        }
    }

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId, userName)
    }

    val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) }

    fun fetchAndSendLocation() {
        try {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                object : com.google.android.gms.tasks.CancellationToken() {
                    override fun onCanceledRequested(p0: com.google.android.gms.tasks.OnTokenCanceledListener) = com.google.android.gms.tasks.CancellationTokenSource().token
                    override fun isCancellationRequested() = false
                }
            ).addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onSendMessage(conversationId, "Đã gửi vị trí", lat = location.latitude, lng = location.longitude)
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            viewModel.onSendMessage(conversationId, "Đã gửi vị trí", lat = lastLoc.latitude, lng = lastLoc.longitude)
                        } else {
                            android.widget.Toast.makeText(context, "Không thể lấy vị trí hiện tại. Vui lòng bật GPS và thử lại.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                android.widget.Toast.makeText(context, "Lỗi khi lấy vị trí: ${it.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            android.widget.Toast.makeText(context, "Chưa cấp quyền vị trí", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchAndSendLocation()
        } else {
            android.widget.Toast.makeText(context, "Cần cấp quyền vị trí để gửi", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    if (showAttachmentMenu) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentMenu = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = Color.White,
        ) {
            AttachmentMenuContent(
                onOptionClick = { option ->
                    showAttachmentMenu = false
                    if (option == "image") {
                        photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else if (option == "location") {
                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            fetchAndSendLocation()
                        } else {
                            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            )
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.scrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300.copy(alpha = 0.5f))
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Neutral100)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Trở về",
                                tint = PrimaryMain
                            )
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = PrimaryLight,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = uiState.otherPartyName.take(1).ifEmpty { "C" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryMain
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.otherPartyName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral900
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val dialPhone = if (phoneNumber.isNotBlank()) phoneNumber else "0898990543"
                                val intent = Intent(Intent.ACTION_DIAL, "tel:$dialPhone".toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimarySurface)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Gọi điện", tint = PrimaryMain)
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
                    .shadow(4.dp, RectangleShape),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { showAttachmentMenu = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimarySurface)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm tệp", tint = PrimaryMain)
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        color = Neutral100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300.copy(alpha = 0.5f))
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = {
                                Text(
                                    "Gửi tin nhắn...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Neutral500
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = PrimaryMain,
                                focusedTextColor = Neutral900,
                                unfocusedTextColor = Neutral900
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (messageText.isNotBlank()) PrimaryMain else PrimaryMain.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = uiState.messages,
                key = { it.id }
            ) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun AttachmentMenuContent(onOptionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Chia sẻ nội dung",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AttachmentOption(
                icon = Icons.Default.Image,
                label = "Hình ảnh",
                color = SuccessEmerald,
                onClick = { onOptionClick("image") },
                modifier = Modifier.weight(1f),
            )
            AttachmentOption(
                icon = Icons.Default.LocationOn,
                label = "Vị trí",
                color = ErrorRose,
                onClick = { onOptionClick("location") },
                modifier = Modifier.weight(1f),
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
fun ChatBubble(message: Message) {
    val context = LocalContext.current
    val isMe = message.isFromMe
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) PrimaryMain else Color.White
    val textColor = if (isMe) Color.White else Neutral900

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
                modifier = Modifier.widthIn(max = 280.dp),
                border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, Neutral300.copy(alpha = 0.6f)) else null
            ) {
                Column {
                    if (message.imageUrl != null) {
                        coil.compose.AsyncImage(
                            model = message.imageUrl,
                            contentDescription = "Hình ảnh",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(shape)
                        )
                    }

                    if (message.latitude != null && message.longitude != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Neutral50,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRose.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = ErrorRose,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Đã chia sẻ vị trí",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral900
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val uri = android.net.Uri.parse("google.navigation:q=${message.latitude},${message.longitude}")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.google.android.apps.maps")
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            val webUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${message.latitude},${message.longitude}")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = PrimarySurface,
                                        contentColor = PrimaryMain
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryMain)
                                ) {
                                    Text("Chỉ đường", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            Text(
                text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = Neutral500,
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


