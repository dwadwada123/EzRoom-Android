package com.example.ezroom.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // IMPORT QUAN TRỌNG
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.ezroom.ui.theme.*

// 1. Khai báo Enum (Sửa lỗi Unresolved reference 'NotificationType')
enum class NotificationType {
    BILL,      // Hóa đơn
    SCHEDULE,  // Lịch hẹn
    SYSTEM     // Hệ thống
}

// 2. Khai báo Data Class (Sửa lỗi Unresolved reference 'NotificationItem')
data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val group: String // "Hôm nay" hoặc "Trước đó"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tất cả", "Chưa đọc")

    val notifications = remember {
        listOf(
            NotificationItem("1", "Hóa đơn mới", "Bạn có hóa đơn tiền phòng tháng 5/2026 cần thanh toán.", "10:30", NotificationType.BILL, isRead = false, group = "Hôm nay"),
            NotificationItem("2", "Lịch hẹn xem phòng", "Lịch hẹn xem phòng Q7 của bạn đã được chủ trọ duyệt.", "09:15", NotificationType.SCHEDULE, isRead = false, group = "Hôm nay"),
            NotificationItem("3", "Cập nhật hệ thống", "EzRoom vừa cập nhật tính năng thanh toán qua VNPay.", "Hôm qua", NotificationType.SYSTEM, isRead = true, group = "Trước đó"),
            NotificationItem("4", "Nhắc nhở lịch hẹn", "Bạn có lịch hẹn xem phòng vào lúc 17:30 chiều nay.", "Hôm qua", NotificationType.SCHEDULE, isRead = true, group = "Trước đó"),
            NotificationItem("5", "Thanh toán thành công", "Hóa đơn tháng 4 của bạn đã được xác nhận thanh toán.", "01/05", NotificationType.BILL, isRead = true, group = "Trước đó")
        )
    }

    val filteredNotifications = if (selectedTab == 1) notifications.filter { !it.isRead } else notifications

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("THÔNG BÁO", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = OrangePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DoneAll, null, tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceLight,
                contentColor = OrangePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]), // Đã hết lỗi nhờ import trên
                        color = OrangePrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(title, fontSize = 14.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium)
                        }
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val grouped = filteredNotifications.groupBy { it.group }

                grouped.forEach { (header, items) ->
                    item {
                        Text(
                            text = header.uppercase(),
                            modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                    }

                    items(items) { notification ->
                        NotificationRow(notification)
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun NotificationRow(item: NotificationItem) {
    // Sửa lỗi 'when' expression must be exhaustive bằng cách thêm else
    val (icon, color) = when (item.type) {
        NotificationType.BILL -> Icons.Default.ReceiptLong to Color(0xFFF44336)
        NotificationType.SCHEDULE -> Icons.Default.EventAvailable to TealAccent
        NotificationType.SYSTEM -> Icons.Default.Info to Color(0xFF2196F3)
        else -> Icons.Default.Notifications to Color.Gray // Nhánh an toàn
    }

    Surface(
        color = if (item.isRead) SurfaceLight else OrangePrimary.copy(alpha = 0.03f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { }
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = OnBackgroundLight
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.content,
                    fontSize = 14.sp,
                    color = if (item.isRead) Color.Gray else OnBackgroundLight.copy(0.8f),
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
                        .background(OrangePrimary)
                )
            }
        }
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        color = OnBackgroundLight.copy(alpha = 0.05f),
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