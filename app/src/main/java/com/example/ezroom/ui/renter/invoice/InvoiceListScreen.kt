package com.example.ezroom.ui.renter.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*

@Composable
fun RenterInvoiceListScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onInvoiceClick: (String) -> Unit = {}
) {
    // State definitions
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chưa thanh toán", "Đã thanh toán")
    val mockInvoices = remember { MockData.invoices }

    val filteredList = remember(selectedTabIndex) {
        mockInvoices.filter { item ->
            if (selectedTabIndex == 0) item.status == InvoiceStatus.UNPAID else item.status == InvoiceStatus.PAID
        }
    }

    // Main layout container
    Column(modifier = Modifier.fillMaxSize()) {
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

        // Content scroll area
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList, key = { it.id }) { item ->
                RenterInvoiceItemCard(
                    item = item,
                    onClick = { onInvoiceClick(item.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenterInvoiceItemCard(
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
                val title = if (item.period == "Cọc giữ chỗ") "Tiền cọc giữ chỗ" else "Hóa đơn Tháng ${item.period}"
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnBackgroundLight)
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
fun RenterInvoiceListScreenPreview() {
    EzRoomTheme {
        RenterInvoiceListScreen(onNavigateBack = {})
    }
}
