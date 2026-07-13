package com.example.ezroom.ui.notification

import androidx.compose.animation.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.NotificationRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetNotificationsUseCase
import com.example.ezroom.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.ezroom.domain.usecase.MarkNotificationAsReadUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onNavigateToSignContract: (String) -> Unit = {},
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
    val tabs = listOf("Tất cả", "Chưa đọc")

    val filteredNotifications = if (selectedTab == 1) {
        uiState.notifications.filter { !it.isRead }
    } else {
        uiState.notifications
    }

    // Main layout container
    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            // Top app bar
            Surface(
                color = SurfaceLight,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("THÔNG BÁO", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = OrangePrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onMarkAllAsRead() }) {
                            Icon(Icons.Default.DoneAll, null, tint = OrangePrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
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

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingWidget()
                }
            } else if (filteredNotifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có thông báo nào", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Grouping logic (Simplified for mock since we don't have many dates)
                    val grouped = filteredNotifications.groupBy { if (it.time.contains("trước")) "Hôm nay" else "Trước đó" }

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
                            var visible by remember { mutableStateOf(value = false) }
                            LaunchedEffect(Unit) { visible = true }
                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInVertically(initialOffsetY = { 30 }) + fadeIn(),
                            ) {
                                NotificationRow(
                                    item = notification,
                                    onClick = { 
                                        viewModel.onNotificationRead(notification.id)
                                        if (notification.type == "CONTRACT") {
                                            onNavigateToSignContract(notification.targetId ?: notification.id)
                                        }
                                    },
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
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
