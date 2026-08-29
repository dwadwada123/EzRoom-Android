package com.example.ezroom.ui.renter.contract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.BankRepositoryImpl
import com.example.ezroom.data.repository.PaymentAccountRepositoryImpl
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.host.profile.BankPickerDialog
import com.example.ezroom.ui.host.profile.PaymentAccountUiState
import com.example.ezroom.ui.host.profile.PaymentAccountViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

// Biến fallback giúp tránh lỗi Unresolved reference 'peaceSansFont'
private val peaceSansFont = FontFamily.Default

@Composable
fun RefundFormScreen(
    contractId: String,
    onRefundRequested: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PaymentAccountViewModel = viewModel(
        factory = viewModelFactory {
            val bankRepo = BankRepositoryImpl()
            val accRepo = PaymentAccountRepositoryImpl()
            PaymentAccountViewModel(
                GetBanksUseCase(bankRepo),
                GetPaymentAccountsUseCase(accRepo),
                SavePaymentAccountUseCase(accRepo),
                DeletePaymentAccountUseCase(accRepo),
                SetDefaultPaymentAccountUseCase(accRepo)
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    RefundFormContent(
        uiState = uiState,
        onRefundRequested = onRefundRequested,
        onNavigateBack = onNavigateBack,
        onBankSearch = { viewModel.onBankSearch(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundFormContent(
    uiState: PaymentAccountUiState,
    onRefundRequested: () -> Unit,
    onNavigateBack: () -> Unit,
    onBankSearch: (String) -> Unit
) {
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var accountOwner by remember { mutableStateOf("") }
    var showBankPicker by remember { mutableStateOf(false) }

    val isFormValid = selectedBank != null && accountNumber.isNotEmpty() && accountOwner.isNotEmpty()

    Scaffold(
        topBar = {
            RefundTopBar(onNavigateBack = onNavigateBack)
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Khối thông báo cảnh báo
            WarningBanner()

            // Tiêu đề phần
            Text(
                text = "Tài khoản nhận tiền hoàn",
                fontFamily = peaceSansFont,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral900
            )

            // Chọn ngân hàng
            BankSelectField(
                selectedBank = selectedBank,
                onClick = { showBankPicker = true }
            )

            // Nhập số tài khoản
            FormInputField(
                label = "Số tài khoản",
                value = accountNumber,
                onValueChange = { accountNumber = it },
                placeholder = "Nhập số tài khoản",
                leadingIcon = Icons.Default.CreditCard,
                keyboardType = KeyboardType.Number
            )

            // Nhập tên chủ tài khoản
            FormInputField(
                label = "Tên chủ tài khoản",
                value = accountOwner,
                onValueChange = { accountOwner = it },
                placeholder = "Nhập tên chủ tài khoản (Viết hoa không dấu)",
                leadingIcon = Icons.Default.PersonOutline
            )

            Spacer(modifier = Modifier.weight(1f))

            // Nút gửi yêu cầu
            Button(
                onClick = onRefundRequested,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryMain,
                    disabledContainerColor = PrimaryMain.copy(alpha = 0.4f),
                    contentColor = White,
                    disabledContentColor = White.copy(alpha = 0.8f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "GỬI YÊU CẦU HOÀN TIỀN",
                    fontFamily = peaceSansFont,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (showBankPicker) {
            BankPickerDialog(
                banks = uiState.filteredBanks,
                query = uiState.bankQuery,
                onQueryChange = onBankSearch,
                onBankSelected = {
                    selectedBank = it
                    showBankPicker = false
                },
                onDismiss = { showBankPicker = false },
                isLoading = uiState.isLoading
            )
        }
    }
}

// ================= UI COMPONENTS ĐƯỢC TÁCH RIÊNG =================

@Composable
private fun RefundTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceCard)
                .clickable { onNavigateBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Neutral900,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "Yêu cầu hoàn tiền",
            fontFamily = peaceSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Neutral900,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 40.dp)
        )
    }
}

@Composable
private fun WarningBanner() {
    Surface(
        color = AccentAmber.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Chủ thuê đã hủy phòng. Hãy nhập tài khoản để nhận lại 100% tiền cọc.",
                fontFamily = peaceSansFont,
                fontSize = 13.sp,
                color = Color(0xFFB45309), // Màu nâu hổ phách đậm chuẩn thiết kế
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun BankSelectField(
    selectedBank: Bank?,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ngân hàng",
            fontFamily = peaceSansFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Neutral500,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedBank?.name ?: "Chọn ngân hàng",
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = if (selectedBank != null) Neutral900 else Neutral500.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Neutral500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = peaceSansFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = Neutral500.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Neutral500,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                disabledContainerColor = SurfaceCard,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = peaceSansFont,
                fontSize = 14.sp,
                color = Neutral900
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RefundFormScreenPreview() {
    val sampleBank = Bank(1, "Vietcombank", "VCB", "970436", "https://api.vietqr.io/img/VCB.png")
    EzRoomTheme {
        RefundFormContent(
            uiState = PaymentAccountUiState(
                filteredBanks = listOf(sampleBank),
                isLoading = false
            ),
            onRefundRequested = {},
            onNavigateBack = {},
            onBankSearch = {}
        )
    }
}