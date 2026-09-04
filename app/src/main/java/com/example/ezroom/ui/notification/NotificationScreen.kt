package com.example.ezroom.ui.notification

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import com.example.ezroom.data.repository.NotificationRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetNotificationsUseCase
import com.example.ezroom.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.ezroom.domain.usecase.MarkNotificationAsReadUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

// Xử lý an toàn tránh lỗi Unresolved reference 'peaceSansFont'
private val peaceSansFont = FontFamily.Default

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (conversationId: String, senderName: String) -> Unit = { _, _ -> },
    onNavigateToContract: (contractId: String) -> Unit = {},
    onNavigateToInvoice: (invoiceId: String) -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToRoom: (roomId: String) -> Unit = {},
    viewModel: NotificationViewModel = viewModel(
        factory = viewModelFactory {
            val repo = NotificationRepositoryImpl()
            NotificationViewModel(
                GetNotificationsUseCase(repo),
                MarkNotificationAsReadUseCase(repo),
                MarkAllNotificationsAsReadUseCase(repo),
            )
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val filteredNotifications = if (selectedTab == 1) {
        uiState.notifications.filter { !it.isRead }
    } else {
        uiState.notifications
    }

    Scaffold(
        containerColor = White,
        topBar = {
            NotificationTopBar(
                onNavigateBack = onNavigateBack,
                onMarkAllAsRead = { viewModel.onMarkAllAsRead() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CustomTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingWidget()
                }
            } else if (filteredNotifications.isEmpty()) {
                EmptyNotificationState()
            } else {
                NotificationList(
                    notifications = filteredNotifications,
                    viewModel = viewModel,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToContract = onNavigateToContract,
                    onNavigateToInvoice = onNavigateToInvoice,
                    onNavigateToAppointments = onNavigateToAppointments,
                    onNavigateToRoom = onNavigateToRoom
                )
            }
        }
    }
}

@Composable
private fun NotificationTopBar(
    onNavigateBack: () -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    Surface(
        color = White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Neutral900,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "THÔNG BÁO",
                fontFamily = peaceSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Neutral900
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .clickable { onMarkAllAsRead() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read All",
                    tint = PrimaryMain,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Tất cả", "Chưa đọc")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) White else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .then(
                            if (isSelected) Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), clip = false)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontFamily = peaceSansFont,
                        color = if (isSelected) PrimaryMain else Neutral500,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryLight.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PrimaryMain),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Không có thông báo nào",
            fontFamily = peaceSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Neutral900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bạn sẽ nhận được các thông báo mới nhất về\ntài khoản và giao dịch tại đây.",
            fontFamily = peaceSansFont,
            fontSize = 14.sp,
            color = Neutral500,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}

@Composable
private fun NotificationList(
    notifications: List<NotificationItem>,
    viewModel: NotificationViewModel,
    onNavigateToChat: (conversationId: String, senderName: String) -> Unit,
    onNavigateToContract: (String) -> Unit,
    onNavigateToInvoice: (String) -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToRoom: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val grouped = notifications.groupBy { if (it.time.contains("trước")) "Hôm nay" else "Trước đó" }

        grouped.forEach { (header, items) ->
            item {
                Text(
                    text = header.uppercase(),
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
                    fontFamily = peaceSansFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
            }

            items(items) { notification ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(initialOffsetY = { 30 }) + fadeIn(),
                ) {
                    NotificationRow(
                        item = notification,
                        onClick = {
                            viewModel.onNotificationRead(notification.id)
                            val targetId = notification.targetId
                            when (notification.type.uppercase()) {
                                "CHAT" -> if (!targetId.isNullOrEmpty()) {
                                    val senderName = if (notification.content.contains(":")) {
                                        notification.content.substringBefore(":").trim()
                                    } else {
                                        ""
                                    }
                                    onNavigateToChat(targetId, senderName)
                                }
                                "CONTRACT" -> if (!targetId.isNullOrEmpty()) onNavigateToContract(targetId)
                                "INVOICE", "BILL" -> if (!targetId.isNullOrEmpty()) onNavigateToInvoice(targetId)
                                "APPOINTMENT", "SCHEDULE" -> onNavigateToAppointments()
                                "MODERATION", "ROOM" -> if (!targetId.isNullOrEmpty()) onNavigateToRoom(targetId)
                            }
                        }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun NotificationRow(
    item: NotificationItem,
    onClick: () -> Unit = {}
) {
    val (icon, color) = when (item.type.uppercase()) {
        "BILL", "INVOICE" -> Icons.AutoMirrored.Filled.ReceiptLong to ErrorRose
        "SCHEDULE", "APPOINTMENT" -> Icons.Default.EventAvailable to AccentTeal
        "CONTRACT" -> Icons.Default.Description to PrimaryMain
        "CHAT" -> Icons.AutoMirrored.Filled.Chat to SuccessEmerald
        "MODERATION" -> Icons.Default.VerifiedUser to LogoNavy
        else -> Icons.Default.Info to PrimaryMain
    }

    Surface(
        color = if (item.isRead) White else PrimarySurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontFamily = peaceSansFont,
                        fontWeight = if (item.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Neutral900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = com.example.ezroom.util.DateTimeUtils.formatSmartTime(item.time),
                        fontFamily = peaceSansFont,
                        fontSize = 11.sp,
                        color = Neutral500,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.content,
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = if (item.isRead) Neutral500 else Neutral700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }

            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(PrimaryMain)
                )
            }
        }
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        color = Neutral300.copy(alpha = 0.5f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    EzRoomTheme {
        NotificationScreen(onNavigateBack = {})
    }
}