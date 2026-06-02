package com.example.ezroom.ui.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

data class InvoiceItem(
    val id: String,
    val title: String,
    val roomName: String,
    val amount: String,
    val date: String,
    val isPaid: Boolean,
    val isDeposit: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    isHost: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = if (isHost) {
        listOf("Chưa thanh toán", "Đã thanh toán")
    } else {
        listOf("Hóa đơn cần đóng", "Lịch sử tiền cọc")
    }

    val mockInvoices = remember {
        listOf(
            InvoiceItem("1", "Hóa đơn tiền phòng Tháng 5", "Phòng 101", "3.450.000 đ", "10/05/2026", false),
            InvoiceItem("2", "Hóa đơn tiền phòng Tháng 5", "Phòng 102", "3.200.000 đ", "08/05/2026", true),
            InvoiceItem("3", "Tiền cọc giữ chỗ", "Phòng 204", "1.000.000 đ", "01/04/2026", true, isDeposit = true),
            InvoiceItem("4", "Hóa đơn tiền phòng Tháng 4", "Phòng 101", "3.500.000 đ", "10/04/2026", true)
        )
    }

    val filteredList = mockInvoices.filter { item ->
        if (isHost) {
            if (selectedTabIndex == 0) !item.isPaid && !item.isDeposit else item.isPaid && !item.isDeposit
        } else {
            if (selectedTabIndex == 0) !item.isPaid && !item.isDeposit else item.isDeposit
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHost) "QUẢN LÝ HÓA ĐƠN" else "HÓA ĐƠN & ĐẶT CỌC",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OrangePrimary)
                    }
                },
                actions = {
                    if (isHost) {
                        TextButton(onClick = onNavigateToCreate) {
                            Text("Tạo mới", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { item ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = SurfaceLight),
                        border = BorderStroke(1.dp, OnBackgroundLight.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBackgroundLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vị trí: ${item.roomName}",
                                    fontSize = 14.sp,
                                    color = OnBackgroundLight.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Ngày lập: ${item.date}",
                                    fontSize = 12.sp,
                                    color = OnBackgroundLight.copy(alpha = 0.4f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = item.amount,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val badgeColor = if (item.isPaid) TealAccent else OrangeSecondary
                                val badgeText = if (item.isPaid) "Đã đóng" else "Chưa đóng"
                                Surface(
                                    color = badgeColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceListScreenPreview() {
    EzRoomTheme {
        InvoiceListScreen(
            isHost = false,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceListScreenHostPreview() {
    EzRoomTheme {
        InvoiceListScreen(
            isHost = true,
            onNavigateBack = {},
            onNavigateToCreate = {}
        )
    }
}
