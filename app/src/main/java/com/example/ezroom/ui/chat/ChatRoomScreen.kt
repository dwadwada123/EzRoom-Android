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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.ChatRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetConversationsUseCase
import com.example.ezroom.domain.usecase.GetMessagesUseCase
import com.example.ezroom.domain.usecase.SendMessageUseCase
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import com.example.ezroom.ui.components.SecondaryButton

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
                // Determine mime type
                val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
                // Generate a random file name
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
                    // Try last location if current location fails
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
            containerColor = MaterialTheme.colorScheme.surface,
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
        containerColor = Color(0xFFF8F9FE), // Light background like in image
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ChatLogoNavy)
                        }
                    },
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.otherPartyName.ifBlank { "Người dùng" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = peaceSansFont,
                                color = ChatLogoNavy
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Đang hoạt động",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontFamily = peaceSansFont
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val dialPhone = phoneNumber.ifBlank { "0898990543" }
                                val intent = Intent(Intent.ACTION_DIAL, "tel:$dialPhone".toUri())
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Default.Call, null, tint = ChatLogoCyan)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                color = Color.White,
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Camera Icon
                    IconButton(onClick = { /* Camera feature if available */ }) {
                        Icon(Icons.Default.PhotoCamera, null, tint = Color.LightGray)
                    }
                    
                    // Gallery/Attachment Icon
                    IconButton(onClick = { showAttachmentMenu = true }) {
                        Icon(Icons.Default.Image, null, tint = Color.LightGray)
                    }

                    // Input Field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = peaceSansFont,
                                color = Color.Black
                            ),
                            decorationBox = { innerTextField ->
                                if (messageText.isEmpty()) {
                                    Text(
                                        "Nhập tin nhắn...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = peaceSansFont,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Send Button
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
                            .background(if (messageText.isNotBlank()) ChatLogoCyan else Color(0xFFE2E8F0))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, 
                            null, 
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
            item {
                Text(
                    text = "Hôm nay",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontFamily = peaceSansFont
                )
            }
            
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
            fontWeight = FontWeight.ExtraBold,
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AttachmentOption(
                icon = Icons.Default.Image,
                label = "Hình ảnh",
                color = Color(0xFF10B981),
                onClick = { onOptionClick("image") },
                modifier = Modifier.weight(1f),
            )
            AttachmentOption(
                icon = Icons.Default.LocationOn,
                label = "Vị trí",
                color = Color(0xFFEF4444),
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
    
    val bubbleColor = if (isMe) ChatLogoCyan else Color(0xFFFCE7F3) // Light pinkish/purple for others
    val textColor = if (isMe) Color.White else ChatLogoNavy

    val shape = if (isMe)
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    else
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isMe) {
            // Other Party Avatar
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = ChatLogoCyan.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "G", // Simulated initial like in image
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChatLogoCyan
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = if (isMe) 2.dp else 1.dp,
                modifier = Modifier.widthIn(max = 260.dp)
            ) {
                Column {
                    if (message.imageUrl != null) {
                        coil.compose.AsyncImage(
                            model = message.imageUrl,
                            contentDescription = "Hình ảnh",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(shape)
                        )
                    }
                    
                    if (message.latitude != null && message.longitude != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMe) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.LocationOn, 
                                    contentDescription = null, 
                                    tint = if (isMe) Color.White else ErrorRose,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Vị trí đã chia sẻ", 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = peaceSansFont,
                                    color = if (isMe) Color.White else ChatLogoNavy
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                SecondaryButton(
                                    text = "Chỉ đường",
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
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = peaceSansFont,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp))
                Text(
                    text = if (isMe) "$time - Đã gửi" else "$time - Đã xem",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontFamily = peaceSansFont
                )
            }
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
