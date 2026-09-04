package com.example.ezroom.ui.renter.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import coil.compose.AsyncImage
import com.example.ezroom.data.repository.InvoiceRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.invoice.InvoiceViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoice: Invoice? = null,
    onBackClick: () -> Unit,
    onPaymentConfirm: (invoiceId: String, paymentMethod: String, transactionType: TransactionType) -> Unit,
    viewModel: InvoiceViewModel = viewModel(
        factory = viewModelFactory {
            val repository = InvoiceRepositoryImpl()
            InvoiceViewModel(GetInvoicesUseCase(repository), repository)
        }
    )
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val formatter = remember { DecimalFormat("#,### đ") }
    val clipboardManager = LocalClipboardManager.current
    var showQRModal by remember { mutableStateOf(false) }
    
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
            otherCosts = listOf(OtherCostItem("Phí vệ sinh", 50000L)),
            status = InvoiceStatus.UNPAID,
            type = TransactionType.RENT,
            dateCreated = "10/05/2026"
        )
    }

    val displayInvoice = invoice ?: defaultInvoice
    val elecUsage = displayInvoice.newElectricity - displayInvoice.oldElectricity
    val waterUsage = displayInvoice.newWater - displayInvoice.oldWater
    val elecAmount = elecUsage * displayInvoice.electricityPrice
    val waterAmount = waterUsage * displayInvoice.waterPrice
    val totalAmount = displayInvoice.roomPrice + elecAmount + waterAmount + displayInvoice.totalOtherCosts

    var selectedPaymentMethod by remember { mutableStateOf("VIETQR") }
    val isPaid = displayInvoice.status == InvoiceStatus.PAID

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết hóa đơn", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Header Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tổng thanh toán",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatter.format(totalAmount),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StatusBadge(
                        text = if (isPaid) "ĐÃ THANH TOÁN" else "CHƯA THANH TOÁN",
                        color = if (isPaid) Color.White else AccentAmber
                    )
                }
            }

            // Billing Details
            Column {
                Text(
                    text = "Chi tiết các hạng mục",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DetailRow(label = "Giá thuê phòng", value = formatter.format(displayInvoice.roomPrice))
                        
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        
                        UtilityDetailRow(
                            title = "Tiền Điện",
                            usageText = "$elecUsage kWh ($3.500đ/kWh)",
                            amount = formatter.format(elecAmount)
                        )

                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        UtilityDetailRow(
                            title = "Tiền Nước",
                            usageText = "$waterUsage m³ ($15.000đ/m³)",
                            amount = formatter.format(waterAmount)
                        )

                        displayInvoice.otherCosts.forEach { item ->
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            DetailRow(label = item.reason, value = formatter.format(item.amount))
                        }
                    }
                }
            }

            // Payment Methods
            if (!isPaid) {
                Column {
                    Text(
                        text = "Phương thức thanh toán",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PaymentMethodItem(
                        title = "Chuyển khoản VietQR",
                        selected = true,
                        icon = Icons.Default.AccountBalance,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = if (isPaid) "XUẤT HÓA ĐƠN PDF" else "THANH TOÁN NGAY",
                onClick = { 
                    if (!isPaid) {
                        showQRModal = true
                    } else {
                        com.example.ezroom.util.PdfExporter.exportInvoicePdf(context, displayInvoice)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showQRModal) {
            var qrDetails by remember { mutableStateOf<com.example.ezroom.data.remote.PaymentResponse?>(null) }
            var isQRLoading by remember { mutableStateOf(true) }
            var isSubmittingPayment by remember { mutableStateOf(false) }
            var verifyError by remember { mutableStateOf<String?>(null) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(displayInvoice.id) {
                isQRLoading = true
                qrDetails = InvoiceRepositoryImpl().getPaymentQR(displayInvoice.id)
                isQRLoading = false
            }

            val accNo = qrDetails?.accountNumber?.takeIf { it.isNotBlank() } ?: "0898990543"
            val accName = qrDetails?.accountName?.takeIf { it.isNotBlank() } ?: "EZROOM ESCROW PAYOS"
            val bankTitle = qrDetails?.bankName?.takeIf { it.isNotBlank() } ?: "MBBank (PayOS Escrow)"
            val transferContent = "HD ${displayInvoice.id.takeLast(6).uppercase()}"
            val qrImageUrl = "https://img.vietqr.io/image/MB-$accNo-compact2.png?amount=$totalAmount&addInfo=${android.net.Uri.encode(transferContent)}&accountName=${android.net.Uri.encode(accName)}"

            AlertDialog(
                onDismissRequest = { if (!isSubmittingPayment) showQRModal = false },
                title = {
                    Text(
                        text = "THANH TOÁN PAYOS ESCROW (TẠM GIỮ)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(210.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, Neutral300)
                        ) {
                            if (isQRLoading) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                AsyncImage(
                                    model = qrImageUrl,
                                    contentDescription = "VietQR PayOS Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Text(
                            text = "Tổng tiền: ${formatter.format(totalAmount)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        verifyError?.let { err ->
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ $err",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Surface(
                            color = Neutral100,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Cổng thanh toán:", fontSize = 12.sp, color = Neutral500)
                                    Text(
                                        text = bankTitle, 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Chủ tài khoản:", fontSize = 12.sp, color = Neutral500)
                                    Text(
                                        text = accName, 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Số tài khoản:", fontSize = 12.sp, color = Neutral500)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                                    ) {
                                        Text(accNo, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copy STK",
                                            modifier = Modifier.size(14.dp).clickable {
                                                clipboardManager.setText(AnnotatedString(accNo))
                                            },
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Nội dung CK:", fontSize = 12.sp, color = Neutral500)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                                    ) {
                                        Text(transferContent, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessEmerald, textAlign = TextAlign.End)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copy nội dung",
                                            modifier = Modifier.size(14.dp).clickable {
                                                clipboardManager.setText(AnnotatedString(transferContent))
                                            },
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Tiền thanh toán được PayOS chuyển vào cổng trung gian EzRoom. Hệ thống tự động khấu trừ 5% hoa hồng sàn và chuyển doanh thu thực nhận cho Chủ nhà.",
                            fontSize = 11.sp,
                            color = Neutral500,
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                isSubmittingPayment = true
                                verifyError = null
                                val result = InvoiceRepositoryImpl().payInvoiceWithResult(displayInvoice.id, "VietQR (PayOS Escrow)")
                                isSubmittingPayment = false
                                if (result.isSuccess) {
                                    showQRModal = false
                                    android.widget.Toast.makeText(context, "Thanh toán hóa đơn thành công!", android.widget.Toast.LENGTH_LONG).show()
                                    onPaymentConfirm(displayInvoice.id, selectedPaymentMethod, displayInvoice.type)
                                } else {
                                    val errorMsg = result.exceptionOrNull()?.message ?: "Hệ thống PayOS chưa ghi nhận giao dịch chuyển khoản cho đơn hàng này."
                                    verifyError = errorMsg
                                    android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !isSubmittingPayment,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
                    ) {
                        Text(if (isSubmittingPayment) "ĐANG XÁC THỰC PAYOS..." else "XÁC NHẬN ĐÃ CHUYỂN KHOẢN", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showQRModal = false },
                        enabled = !isSubmittingPayment
                    ) {
                        Text("Đóng")
                    }
                }
            )
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
            style = MaterialTheme.typography.bodyLarge, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UtilityDetailRow(title: String, usageText: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = usageText, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = amount, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
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
            onPaymentConfirm = { _, _, _ -> }
        )
    }
}


