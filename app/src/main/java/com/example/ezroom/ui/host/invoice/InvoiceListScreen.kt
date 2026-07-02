package com.example.ezroom.ui.host.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.example.ezroom.domain.model.*
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.InvoiceRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.invoice.InvoiceViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostInvoiceListScreen(
    // Event callbacks
    onNavigateToCreate: () -> Unit,
    onInvoiceClick: (String) -> Unit = {},
    viewModel: InvoiceViewModel = viewModel(
        factory = viewModelFactory {
            val repository = InvoiceRepositoryImpl()
            InvoiceViewModel(GetInvoicesUseCase(repository), repository)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Tất cả", "Chưa đóng", "Đã đóng")

    // Main layout container
    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pill-shaped Tab Selection
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = when (index) {
                            0 -> uiState.selectedStatus == null
                            1 -> uiState.selectedStatus == InvoiceStatus.UNPAID
                            2 -> uiState.selectedStatus == InvoiceStatus.PAID
                            else -> false
                        }
                        
                        val backgroundColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            label = "TabBg"
                        )
                        val contentColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "TabContent"
                        )

                        Surface(
                            onClick = { 
                                val status = when (index) {
                                    1 -> InvoiceStatus.UNPAID
                                    2 -> InvoiceStatus.PAID
                                    else -> null
                                }
                                viewModel.filterByStatus(status)
                            },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            color = backgroundColor,
                            contentColor = contentColor
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (!uiState.isLoading && uiState.invoices.isEmpty() && uiState.error == null) {
                EmptyState(
                    title = "Chưa có hóa đơn",
                    description = "Không có hóa đơn nào ở trạng thái này.",
                    actionText = "Tạo hóa đơn mới",
                    onAction = onNavigateToCreate
                )
            } else if (uiState.error != null) {
                EmptyState(
                    title = "Lỗi",
                    description = uiState.error ?: "",
                    actionText = "Thử lại",
                    onAction = { viewModel.loadInvoices() }
                )
            } else {
                // Content scroll area
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.invoices, 
                        key = { it.id },
                        contentType = { "InvoiceCard" }
                    ) { item ->
                        InvoiceItemCard(
                            item = item,
                            onClick = { onInvoiceClick(item.id) }
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            LoadingWidget()
        }

        FloatingActionButton(
            onClick = onNavigateToCreate,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm hóa đơn")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceItemCard(
    item: Invoice,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val periodText = if (item.period == "Cọc giữ chỗ") "Tiền cọc giữ chỗ" else "Hóa đơn Tháng ${item.period}"
                Text(text = periodText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Phòng: ${item.roomName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Ngày lập: ${item.dateCreated}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${item.roomPrice} đ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                
                val badgeColor = if (item.status == InvoiceStatus.PAID) SuccessEmerald else AccentAmber
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
        HostInvoiceListScreen(onNavigateToCreate = {})
    }
}


