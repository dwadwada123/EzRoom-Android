package com.example.ezroom.ui.renter.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.Invoice
import com.example.ezroom.data.model.InvoiceStatus
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

@Composable
fun InvoiceDetailScreen(
    invoice: Invoice? = null,
    onBackClick: () -> Unit,
    onPaymentConfirm: (invoiceId: String, paymentMethod: String) -> Unit
) {
    val formatter = remember { DecimalFormat("#,### đ") }
    
    // Mock Data if null
    val defaultInvoice = remember {
        Invoice(
            id = "INV-2026-05-001",
            roomId = "room_101",
            roomName = "Phòng 101",
            period = "05/2026",
            roomPrice = 3000000L,
            oldElectricity = 1250,
            newElectricity = 1380,
            oldWater = 450,
            newWater = 462,
            otherCosts = 50000L,
            status = InvoiceStatus.UNPAID,
            dateCreated = "10/05/2026"
        )
    }

    val displayInvoice = invoice ?: defaultInvoice
    val elecUsage = displayInvoice.newElectricity - displayInvoice.oldElectricity
    val waterUsage = displayInvoice.newWater - displayInvoice.oldWater
    val elecAmount = elecUsage * 3500L
    val waterAmount = waterUsage * 15000L
    val totalAmount = displayInvoice.roomPrice + elecAmount + waterAmount + displayInvoice.otherCosts

    var selectedPaymentMethod by remember { mutableStateOf("VNPAY") }
    val isPaid = displayInvoice.status == InvoiceStatus.PAID

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CommonTopAppBar(
                title = "Chi tiết hóa đơn",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Thẻ tổng quan trạng thái (Status Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mã hóa đơn: ${displayInvoice.id}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnBackgroundLight
                        )
                        Text(
                            text = "Kỳ thanh toán:\nTháng ${displayInvoice.period}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnBackgroundLight.copy(alpha = 0.6f)
                        )
                    }
                    val badgeColor = if (isPaid) TealAccent else OrangeSecondary
                    Surface(
                        color = badgeColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isPaid) "Đã thanh toán" else "Chưa thanh toán",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = badgeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Khối chi tiết chi phí dòng tiền (Bill Breakdown Layout)
            Text(
                text = "Chi tiết chi phí",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = OnBackgroundLight,
                modifier = Modifier.padding(start = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(label = "Tiền phòng cố định", value = formatter.format(displayInvoice.roomPrice))
                    
                    HorizontalDivider(thickness = 0.5.dp, color = OnBackgroundLight.copy(alpha = 0.05f))
                    
                    UtilityDetailRow(
                        title = "Tiền Điện (3.500đ/kWh)",
                        usageText = "${displayInvoice.oldElectricity} - ${displayInvoice.newElectricity} ($elecUsage kWh)",
                        amount = formatter.format(elecAmount)
                    )

                    HorizontalDivider(thickness = 0.5.dp, color = OnBackgroundLight.copy(alpha = 0.05f))

                    UtilityDetailRow(
                        title = "Tiền Nước (15.000đ/m³)",
                        usageText = "${displayInvoice.oldWater} - ${displayInvoice.newWater} ($waterUsage m³)",
                        amount = formatter.format(waterAmount)
                    )

                    HorizontalDivider(thickness = 0.5.dp, color = OnBackgroundLight.copy(alpha = 0.05f))

                    DetailRow(label = "Chi phí khác (Rác, Wifi)", value = formatter.format(displayInvoice.otherCosts))
                }
            }

            // 4. Khối tổng tiền (Total Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = OrangePrimary.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (isPaid) "TỔNG TIỀN ĐÃ THANH TOÁN" else "TỔNG TIỀN CẦN THANH TOÁN",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = OnBackgroundLight.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatter.format(totalAmount),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = OrangePrimary,
                        maxLines = 1
                    )
                }
            }

            // 5. Khối chọn cổng thanh toán (Payment Method Selector)
            Text(
                text = "Chọn phương thức thanh toán",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = OnBackgroundLight,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )

            PaymentMethodItem(
                title = "Cổng thanh toán VNPay",
                selected = selectedPaymentMethod == "VNPAY",
                enabled = !isPaid,
                icon = Icons.Default.AccountBalance,
                onClick = { if (!isPaid) selectedPaymentMethod = "VNPAY" }
            )

            PaymentMethodItem(
                title = "Ví điện tử MoMo",
                selected = selectedPaymentMethod == "MOMO",
                enabled = !isPaid,
                icon = Icons.Default.Wallet,
                onClick = { if (!isPaid) selectedPaymentMethod = "MOMO" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Nút hành động (Action Button)
            PrimaryButton(
                text = if (isPaid) "HÓA ĐƠN ĐÃ ĐƯỢC THANH TOÁN" else "XÁC NHẬN THANH TOÁN",
                onClick = { onPaymentConfirm(displayInvoice.id, selectedPaymentMethod) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isPaid
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), 
            color = OnBackgroundLight.copy(alpha = 0.7f),
            maxLines = 1
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), 
            color = OnBackgroundLight,
            maxLines = 1
        )
    }
}

@Composable
fun UtilityDetailRow(title: String, usageText: String, amount: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp), 
                color = OnBackgroundLight.copy(alpha = 0.7f),
                maxLines = 1
            )
            Text(
                text = amount, 
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), 
                color = OnBackgroundLight,
                maxLines = 1
            )
        }
        Text(
            text = usageText, 
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), 
            color = OnBackgroundLight.copy(alpha = 0.4f),
            maxLines = 1
        )
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    selected: Boolean,
    enabled: Boolean = true,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val borderColor = if (selected) OrangePrimary else OnBackgroundLight.copy(alpha = 0.1f)
    val containerColor = if (selected) OrangePrimary.copy(alpha = 0.02f) else SurfaceLight
    val alpha = if (enabled) 1f else 0.6f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor.copy(alpha = alpha)),
        border = BorderStroke(1.dp, borderColor.copy(alpha = alpha))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (selected) OrangePrimary else OnBackgroundLight.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) androidx.compose.ui.graphics.Color.White else OnBackgroundLight.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                color = OnBackgroundLight.copy(alpha = alpha)
            )
            RadioButton(
                selected = selected,
                onClick = if (enabled) onClick else null,
                enabled = enabled,
                colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary, disabledSelectedColor = OrangePrimary.copy(alpha = 0.6f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceDetailScreenPreview() {
    EzRoomTheme {
        InvoiceDetailScreen(
            onBackClick = {},
            onPaymentConfirm = { _, _ -> }
        )
    }
}
