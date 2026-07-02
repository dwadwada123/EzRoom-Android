package com.example.ezroom.ui.renter.invoice

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.InvoiceRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.invoice.InvoiceViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

@Composable
fun RenterInvoiceListScreen(
    onNavigateBack: () -> Unit,
    onInvoiceClick: (String) -> Unit = {},
    viewModel: InvoiceViewModel = viewModel(
        factory = viewModelFactory {
            val repository = InvoiceRepositoryImpl()
            InvoiceViewModel(GetInvoicesUseCase(repository), repository)
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Tất cả", "Chưa đóng", "Đã đóng")

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selection
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                            label = "TabBg",
                        )
                        val contentColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "TabContent",
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
                            contentColor = contentColor,
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            if (!uiState.isLoading && uiState.invoices.isEmpty() && uiState.error == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    EmptyState(
                        title = "Trống",
                        description = "Bạn không có hóa đơn nào ở trạng thái này.",
                        actionText = "Về trang chủ",
                        onAction = onNavigateBack,
                    )
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    EmptyState(
                        title = "Lỗi",
                        description = uiState.error ?: "",
                        actionText = "Thử lại",
                        onAction = { viewModel.loadInvoices() },
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(
                        items = uiState.invoices, 
                        key = { _, it -> it.id },
                        contentType = { _, _ -> "InvoiceCard" },
                    ) { index, item ->
                        AnimatedVisibility(
                            visible = !uiState.isLoading,
                            enter = slideInVertically(initialOffsetY = { 50 * (index + 1) }) + fadeIn(),
                        ) {
                            RenterInvoiceBentoCard(
                                item = item,
                                onClick = { onInvoiceClick(item.id) },
                            )
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            LoadingWidget()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenterInvoiceBentoCard(
    item: Invoice,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    val title = if (item.period == "Cọc giữ chỗ") "Tiền cọc giữ chỗ" else "Hóa đơn Tháng ${item.period}"
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Phòng: ${item.roomName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Ngày tạo: ${item.dateCreated}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.roomPrice} đ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val statusColor = if (item.status == InvoiceStatus.PAID) SuccessEmerald else AccentAmber
                val statusText = if (item.status == InvoiceStatus.PAID) "Đã thanh toán" else "Chờ đóng"
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    contentColor = statusColor,
                    shape = CircleShape,
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
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
