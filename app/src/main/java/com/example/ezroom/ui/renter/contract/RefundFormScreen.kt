package com.example.ezroom.ui.renter.contract

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.BankRepositoryImpl
import com.example.ezroom.data.repository.PaymentAccountRepositoryImpl
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.model.RefundInfo
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.host.profile.BankPickerDialog
import com.example.ezroom.ui.host.profile.PaymentAccountViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

// UI Component: Form for renter to receive deposit refund
@OptIn(ExperimentalMaterial3Api::class)
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
    
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var accountOwner by remember { mutableStateOf("") }
    var showBankPicker by remember { mutableStateOf(false) }

    val isFormValid = selectedBank != null && accountNumber.isNotEmpty() && accountOwner.isNotEmpty()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yêu cầu hoàn tiền", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Neutral50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // UI Component: Alert Warning
            Surface(
                color = ErrorRose.copy(alpha = 0.05f),
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = ErrorRose, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Chủ thuê đã hủy phòng. Hãy nhập tài khoản để nhận lại 100% tiền cọc.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRose
                    )
                }
            }

            Text("Tài khoản nhận tiền hoàn", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // UI Component: Bank Selector
            OutlinedCard(
                onClick = { showBankPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountBalance, null, tint = PrimaryMain)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = selectedBank?.name ?: "Chọn ngân hàng",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedBank != null) Neutral900 else Neutral500,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, null, tint = Neutral500)
                }
            }

            CustomTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = "Số tài khoản",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = accountOwner,
                onValueChange = { accountOwner = it },
                label = "Tên chủ tài khoản",
                placeholder = "Tên in trên thẻ (không dấu)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "GỬI YÊU CẦU HOÀN TIỀN",
                onClick = onRefundRequested,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showBankPicker) {
            BankPickerDialog(
                banks = uiState.filteredBanks,
                query = uiState.bankQuery,
                onQueryChange = { viewModel.onBankSearch(it) },
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
