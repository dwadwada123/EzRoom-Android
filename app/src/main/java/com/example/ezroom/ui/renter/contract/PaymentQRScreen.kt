package com.example.ezroom.ui.renter.contract

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

// UI Component: Payment QR screen for Deposit Escrow
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentQRScreen(
    contract: Contract,
    onPaymentConfirmed: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,### đ") }
    val transferContent = "HOPDONG ${contract.id.takeLast(6).uppercase()}"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán tiền cọc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Neutral50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // UI Component: Protection Badge (Fintech logic)
            Surface(
                color = SuccessEmerald.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, null, tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tiền cọc của bạn được EzRoom bảo vệ an toàn.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // UI Component: QR Code Container
            Surface(
                modifier = Modifier.size(240.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
                    // Placeholder for QR
                    Icon(Icons.Default.AccountBalance, null, modifier = Modifier.fillMaxSize(), tint = Neutral900)
                }
            }

            Text(
                text = "Quét mã để thanh toán",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral500
            )

            // UI Component: Payment Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PaymentInfoRow(label = "Số tiền cọc", value = formatter.format(contract.depositAmount), isHighlight = true)
                    PaymentInfoRow(label = "Nội dung chuyển khoản", value = transferContent, hasCopy = true)
                    PaymentInfoRow(label = "Ngân hàng", value = "EzRoom Escrow - MBBank")
                }
            }

            // UI Component: Instruction Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Info, null, tint = PrimaryMain, modifier = Modifier.size(18.dp))
                Text(
                    text = "Hệ thống sẽ tự động xác nhận sau 1-3 phút khi nhận được tiền.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "TÔI ĐÃ CHUYỂN KHOẢN",
                onClick = onPaymentConfirmed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PaymentInfoRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    hasCopy: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Neutral500)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isHighlight) PrimaryMain else Neutral900
            )
            if (hasCopy) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ContentCopy, null, tint = PrimaryMain, modifier = Modifier.size(16.dp))
            }
        }
    }
}
