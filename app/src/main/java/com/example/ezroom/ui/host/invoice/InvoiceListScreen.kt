package com.example.ezroom.ui.host.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.*
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostInvoiceListScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onInvoiceClick: (String) -> Unit = {}
) {
    // State definitions
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    
    val tabs = listOf("Chưa thanh toán", "Đã thanh toán")
    val mockInvoices = remember { MockData.invoices }

    val filteredList = remember(selectedTabIndex) {
        mockInvoices.filter { item ->
            if (selectedTabIndex == 0) item.status == InvoiceStatus.UNPAID else item.status == InvoiceStatus.PAID
        }
    }

    fun refreshData() {
        scope.launch {
            isLoading = true
            isError = false
            delay(1000)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = BackgroundLight,
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "QUẢN LÝ HÓA ĐƠN", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = OrangePrimary
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = OrangePrimary)
                        }
                    },
                    actions = {
                        TextButton(onClick = onNavigateToCreate, enabled = !isLoading) {
                            Text("Tạo mới", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Tab bar section
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SurfaceLight,
                    contentColor = OrangePrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = OrangePrimary
                        )
                    },
                    divider = { HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.1f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) OrangePrimary else OnBackgroundLight.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }

                if (isError) {
                    EmptyState(
                        title = "Đã có lỗi xảy ra",
                        description = "Vui lòng thử lại sau giây lát.",
                        actionText = "Thử lại",
                        onAction = { refreshData() }
                    )
                } else if (!isLoading && filteredList.isEmpty()) {
                    EmptyState(
                        title = "Chưa có hóa đơn",
                        description = "Không có hóa đơn nào ở trạng thái này.",
                        actionText = "Tạo hóa đơn mới",
                        onAction = onNavigateToCreate
                    )
                } else {
                    // Content scroll area
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            InvoiceItemCard(
                                item = item,
                                onClick = { if (!isLoading) onInvoiceClick(item.id) }
                            )
                        }
                    }
                }
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceItemCard(
    item: Invoice,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, OnBackgroundLight.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val periodText = if (item.period == "Cọc giữ chỗ") "Tiền cọc giữ chỗ" else "Hóa đơn Tháng ${item.period}"
                Text(text = periodText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnBackgroundLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Phòng: ${item.roomName}", fontSize = 14.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
                Text(text = "Ngày lập: ${item.dateCreated}", fontSize = 12.sp, color = OnBackgroundLight.copy(alpha = 0.4f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${item.roomPrice} đ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Spacer(modifier = Modifier.height(6.dp))
                
                val badgeColor = if (item.status == InvoiceStatus.PAID) TealAccent else OrangeSecondary
                val badgeText = if (item.status == InvoiceStatus.PAID) "Đã đóng" else "Chưa đóng"
                
                StatusBadge(text = badgeText, color = badgeColor)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostInvoiceListScreenPreview() {
    EzRoomTheme {
        HostInvoiceListScreen(onNavigateBack = {}, onNavigateToCreate = {})
    }
}
