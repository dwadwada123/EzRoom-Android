package com.example.ezroom.ui.renter.contract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.ezroom.data.remote.PaymentResponse
import com.example.ezroom.data.repository.ContractRepositoryImpl
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentQRScreen(
    contract: Contract,
    onPaymentConfirmed: suspend () -> Unit,
    onNavigateBack: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var paymentDetails by remember { mutableStateOf<PaymentResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(contract.id) {
        isLoading = true
        val res = ContractRepositoryImpl().getPaymentQR(contract.id)
        paymentDetails = res
        isLoading = false
    }

    PaymentQRContent(
        contract = contract,
        paymentDetails = paymentDetails,
        isLoading = isLoading,
        isSubmitting = isSubmitting,
        onPaymentConfirmed = {
            if (!isSubmitting) {
                isSubmitting = true
                scope.launch {
                    onPaymentConfirmed()
                    isSubmitting = false
                }
            }
        },
        onNavigateBack = onNavigateBack,
        onCopyAccountNumber = { accountNumber ->
            clipboardManager.setText(AnnotatedString(accountNumber))
            scope.launch { snackbarHostState.showSnackbar("Đã chép số tài khoản: $accountNumber") }
        },
        onCopyTransferContent = { transferContent ->
            clipboardManager.setText(AnnotatedString(transferContent))
            scope.launch { snackbarHostState.showSnackbar("Đã chép nội dung: $transferContent") }
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentQRContent(
    contract: Contract,
    paymentDetails: PaymentResponse?,
    isLoading: Boolean,
    isSubmitting: Boolean,
    onPaymentConfirmed: () -> Unit,
    onNavigateBack: () -> Unit,
    onCopyAccountNumber: (String) -> Unit,
    onCopyTransferContent: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val formatter = remember { DecimalFormat("#,### đ") }
    val transferContent = "COC ${contract.id.takeLast(6).uppercase()}"

    val accountNumber = paymentDetails?.accountNumber?.takeIf { it.isNotBlank() } ?: "9999999999"
    val accountName = paymentDetails?.accountName?.takeIf { it.isNotBlank() } ?: "EZROOM ESCROW PAYOS"
    val bankName = paymentDetails?.bankName?.takeIf { it.isNotBlank() } ?: "MBBank (PayOS)"

    val qrImageUrl = "https://img.vietqr.io/image/MB-$accountNumber-compact2.png?amount=${contract.depositAmount}&addInfo=${android.net.Uri.encode(transferContent)}&accountName=${android.net.Uri.encode(accountName)}"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán tiền cọc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Neutral50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Protection Badge Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SuccessEmerald.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tiền cọc của bạn được EzRoom đóng băng bảo vệ an toàn 100%.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. QR Code Card Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Quét mã VietQR / PayOS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    Box(
                        modifier = Modifier.size(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = PrimaryMain)
                        } else {
                            coil.compose.AsyncImage(
                                model = qrImageUrl,
                                contentDescription = "Mã QR Thanh toán Cọc PayOS",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Text(
                        text = "Sử dụng ứng dụng Ngân hàng hoặc Ví điện tử bất kỳ để quét mã",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 3. Payment Transfer Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Thông tin chuyển khoản thủ công",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryMain
                    )

                    HorizontalDivider(color = Neutral100)

                    PaymentInfoRow(
                        label = "Số tiền cọc",
                        value = formatter.format(contract.depositAmount),
                        isHighlight = true
                    )

                    PaymentInfoRow(
                        label = "Số tài khoản",
                        value = accountNumber,
                        hasCopy = true,
                        onCopy = { onCopyAccountNumber(accountNumber) }
                    )

                    PaymentInfoRow(
                        label = "Chủ tài khoản",
                        value = accountName
                    )

                    PaymentInfoRow(
                        label = "Ngân hàng",
                        value = bankName
                    )

                    PaymentInfoRow(
                        label = "Nội dung chuyển khoản",
                        value = transferContent,
                        hasCopy = true,
                        onCopy = { onCopyTransferContent(transferContent) }
                    )
                }
            }

            // 4. Instructions Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryMain, modifier = Modifier.size(18.dp))
                Text(
                    text = "Sau khi thực hiện chuyển khoản thành công, vui lòng bấm nút bên dưới để hệ thống kích hoạt Hợp đồng ngay lập tức.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Prominent Action Button with explicit White Text
            Button(
                onClick = onPaymentConfirmed,
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryMain,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryMain.copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ĐANG XÁC NHẬN THANH TOÁN...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "TÔI ĐÃ CHUYỂN KHOẢN CỌC",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentInfoRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    hasCopy: Boolean = false,
    onCopy: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Neutral500)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (hasCopy) Modifier.clickable { onCopy() } else Modifier
        ) {
            Text(
                text = value,
                style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isHighlight) PrimaryMain else Neutral900
            )
            if (hasCopy) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Sao chép",
                    tint = PrimaryMain,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentQRScreenPreview() {
    val sampleContract = Contract(
        id = "CON123456",
        roomId = "ROOM1",
        roomName = "Phòng trọ cao cấp Quận 1",
        renterName = "Nguyễn Văn A",
        renterPhone = "0123456789",
        startDate = "01/01/2024",
        endDate = "01/01/2025",
        depositAmount = 5000000L,
        dateCreated = "01/12/2023"
    )

    val samplePaymentDetails = PaymentResponse(
        success = true,
        accountNumber = "1234567890",
        accountName = "EZROOM ESCROW PAYOS",
        bankName = "MBBank (PayOS)"
    )

    EzRoomTheme {
        PaymentQRContent(
            contract = sampleContract,
            paymentDetails = samplePaymentDetails,
            isLoading = false,
            isSubmitting = false,
            onPaymentConfirmed = {},
            onNavigateBack = {},
            onCopyAccountNumber = {},
            onCopyTransferContent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
