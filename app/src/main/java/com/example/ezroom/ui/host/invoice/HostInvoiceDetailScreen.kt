package com.example.ezroom.ui.host.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.*
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

/**
 * EzRoom 2026 "Pro Max" Host-Specific Invoice Detail
 * Focuses on Revenue transparency, Renter payment verification, and Business audit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostInvoiceDetailScreen(
    invoice: Invoice? = null,
    onBackClick: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,### đ") }
    
    val displayInvoice = invoice ?: remember {
        Invoice(
            id = "INV-H-001",
            roomId = "room_1",
            roomName = "Phòng 101 - Hải Châu",
            period = "05/2026",
            roomPrice = 3500000L,
            oldElectricity = 1200,
            newElectricity = 1350,
            oldWater = 400,
            newWater = 415,
            otherCosts = listOf(OtherCostItem("Dịch vụ khác", 50000L)),
            status = InvoiceStatus.PAID,
            dateCreated = "10/05/2026",
            paymentMethod = "VNPAY"
        )
    }

    val isPaid = displayInvoice.status == InvoiceStatus.PAID
    val elecUsage = displayInvoice.newElectricity - displayInvoice.oldElectricity
    val waterUsage = displayInvoice.newWater - displayInvoice.oldWater
    val elecAmount = elecUsage * 3500L
    val waterAmount = waterUsage * 15000L
    val totalAmount = displayInvoice.roomPrice + elecAmount + waterAmount + displayInvoice.totalOtherCosts

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = if (isPaid) "Quản lý nguồn thu" else "Theo dõi thanh toán", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Revenue Card (The "Pro Max" look)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = if (isPaid) MaterialTheme.colorScheme.primary else AccentAmber,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                if (isPaid) listOf(PrimaryMain, Color(0xFF1D4ED8))
                                else listOf(AccentAmber, Color(0xFFD97706))
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isPaid) "DOANH THU THỰC NHẬN" else "SỐ TIỀN CẦN THU", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(formatter.format(totalAmount), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    StatusBadge(
                        text = if (isPaid) "ĐÃ ĐỐI SOÁT" else "CHỜ THANH TOÁN", 
                        color = Color.White
                    )
                }
            }

            // Renter & Transaction Info Section
            SectionHeader(if (isPaid) "Thông tin thanh toán" else "Thông tin khách thuê")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(icon = Icons.Default.Person, label = "Người thuê", value = "Nguyễn Văn A")
                    InfoRow(icon = Icons.Default.MeetingRoom, label = "Tên phòng", value = displayInvoice.roomName)
                    if (isPaid) {
                        InfoRow(icon = Icons.Default.Payment, label = "Hình thức", value = displayInvoice.paymentMethod ?: "Tiền mặt")
                        InfoRow(icon = Icons.Default.EventAvailable, label = "Ngày thanh toán", value = "12/05/2026")
                    } else {
                        InfoRow(icon = Icons.Default.CalendarMonth, label = "Hạn thanh toán", value = "20/05/2026")
                        InfoRow(icon = Icons.Default.Phone, label = "Liên hệ", value = "0901234567")
                    }
                }
            }

            // Financial Breakdown
            SectionHeader("Bảng kê chi tiết")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailLine("Tiền thuê phòng", formatter.format(displayInvoice.roomPrice))
                    DetailLine("Tiền điện ($elecUsage kWh)", formatter.format(elecAmount))
                    DetailLine("Tiền nước ($waterUsage m³)", formatter.format(waterAmount))
                    
                    displayInvoice.otherCosts.forEach { item ->
                        DetailLine(item.reason, formatter.format(item.amount))
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tổng cộng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = formatter.format(totalAmount), 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = if (isPaid) PrimaryMain else AccentAmber,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Admin Actions
            if (isPaid) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { /* Export to Excel */ },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, PrimaryMain)
                    ) {
                        Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(20.dp), tint = PrimaryMain)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("XUẤT FILE", color = PrimaryMain, fontWeight = FontWeight.Bold)
                    }
                    
                    PrimaryButton(
                        text = "GỬI BIÊN LAI",
                        onClick = { /* Send via Email/Chat */ },
                        modifier = Modifier.weight(1.4f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton(
                        text = "NHẮC THANH TOÁN",
                        onClick = { /* Send notification */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = { /* Confirm manual payment */ },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, SuccessEmerald)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp), tint = SuccessEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("XÁC NHẬN ĐÃ THU TIỀN", color = SuccessEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = PrimarySurface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(16.dp), tint = PrimaryMain)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HostInvoiceDetailScreenPreview() {
    EzRoomTheme {
        HostInvoiceDetailScreen(onBackClick = {})
    }
}


