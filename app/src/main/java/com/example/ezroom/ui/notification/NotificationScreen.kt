package com.example.ezroom.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.ezroom.data.model.*
import com.example.ezroom.ui.theme.*

// Local UI wrapper to keep the grouping logic
data class NotificationUI(
    val item: NotificationItem,
    val group: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onNavigateToSignContract: (String) -> Unit = {}
) {
    // State definitions
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tất cả", "Chưa đọc")

    val notifications = remember {
        listOf(
            NotificationUI(NotificationItem("notif_contract", "Hợp đồng mới", "Chủ nhà đã lập hợp đồng điện tử cho phòng trọ của bạn. Vui lòng nhấn vào đây để kiểm tra và ký xác nhận.", "11:00", isRead = false, type = "CONTRACT"), group = "Hôm nay"),
            NotificationUI(NotificationItem("1", "Hóa đơn mới", "Bạn có hóa đơn tiền phòng tháng 5/2026 cần thanh toán.", "10:30", isRead = false, type = "BILL"), group = "Hôm nay"),
            NotificationUI(NotificationItem("2", "Lịch hẹn xem phòng", "Lịch hẹn xem phòng Q7 của bạn đã được chủ trọ duyệt.", "09:15", isRead = false, type = "SCHEDULE"), group = "Hôm nay"),
            NotificationUI(NotificationItem("3", "Cập nhật hệ thống", "EzRoom vừa cập nhật tính năng thanh toán qua VNPay.", "Hôm qua", isRead = true, type = "SYSTEM"), group = "Trước đó"),
            NotificationUI(NotificationItem("4", "Nhắc nhở lịch hẹn", "Bạn có lịch hẹn xem phòng vào lúc 17:30 chiều nay.", "Hôm qua", isRead = true, type = "SCHEDULE"), group = "Trước đó"),
            NotificationUI(NotificationItem("5", "Thanh toán thành công", "Hóa đơn tháng 4 của bạn đã được xác nhận thanh toán.", "01/05", isRead = true, type = "BILL"), group = "Trước đó")
        )
    }

    val filteredNotifications = if (selectedTab == 1) notifications.filter { !it.item.isRead } else notifications

    // Main layout container
    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            // Top app bar
            CenterAlignedTopAppBar(
                title = { Text("THÔNG BÁO", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = OrangePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DoneAll, null, tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab row section
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceLight,
                contentColor = OrangePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
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

            // Content scroll area
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
                        NotificationRow(
                            item = notification.item,
                            onClick = { 
                                if (notification.item.type == "CONTRACT") {
                                    onNavigateToSignContract(notification.item.id)
                                }
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun NotificationRow(
    item: NotificationItem,
    onClick: () -> Unit = {}
) {
    val (icon, color) = when (item.type) {
        "BILL" -> Icons.AutoMirrored.Filled.ReceiptLong to Color(0xFFF44336)
        "SCHEDULE" -> Icons.Default.EventAvailable to TealAccent
        "CONTRACT" -> Icons.Default.Description to OrangePrimary
        else -> Icons.Default.Info to Color(0xFF2196F3)
    }

    Surface(
        color = if (item.isRead) SurfaceLight else OrangePrimary.copy(alpha = 0.03f),
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    EzRoomTheme {
        NotificationScreen(onNavigateBack = {})
    }
}
