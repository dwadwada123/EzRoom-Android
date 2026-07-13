package com.example.ezroom.ui.host.profile

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ezroom.data.repository.BankRepositoryImpl
import com.example.ezroom.data.repository.PaymentAccountRepositoryImpl
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

// UI Component: Screen to add a new bank account
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentAccountScreen(
    onNavigateBack: () -> Unit = {},
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
    // State Management: UI State
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var accountOwner by remember { mutableStateOf("") }
    var showBankPicker by remember { mutableStateOf(false) }

    val isFormValid = selectedBank != null && accountNumber.length >= 6 && accountOwner.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // UI Component: Header
                CenterAlignedTopAppBar(
                    title = { Text("Thêm tài khoản", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // UI Component: Bank Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ngân hàng thụ hưởng", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    
                    Surface(
                        onClick = { showBankPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Neutral50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedBank != null) {
                                AsyncImage(
                                    model = selectedBank!!.logo,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = selectedBank!!.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            } else {
                                Icon(Icons.Default.AccountBalance, null, tint = Neutral500)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = "Chọn ngân hàng", style = MaterialTheme.typography.bodyLarge, color = Neutral500, modifier = Modifier.weight(1f))
                            }
                            Icon(Icons.Default.ArrowDropDown, null, tint = Neutral500)
                        }
                    }
                }

                // UI Component: Account Number Input
                CustomTextField(
                    value = accountNumber,
                    onValueChange = { if (it.all { c -> it.isNotBlank() && c.isDigit() }) accountNumber = it },
                    label = "Số tài khoản",
                    placeholder = "Nhập số tài khoản",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // UI Component: Account Owner Input
                CustomTextField(
                    value = accountOwner,
                    onValueChange = { accountOwner = it },
                    label = "Tên chủ tài khoản",
                    placeholder = "Tên in trên thẻ (không dấu)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Business Logic: Save Action
                PrimaryButton(
                    text = "LƯU TÀI KHOẢN",
                    onClick = {
                        if (isFormValid) {
                            viewModel.onAddAccount(selectedBank!!, accountNumber, accountOwner)
                            onNavigateBack()
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // UI Component: Searchable Bank Picker Dialog
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

// UI Component: Custom Searchable Bank Picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPickerDialog(
    banks: List<Bank>,
    query: String,
    onQueryChange: (String) -> Unit,
    onBankSelected: (Bank) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chọn ngân hàng", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Input
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm tên ngân hàng...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Neutral50,
                        focusedContainerColor = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryMain)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(banks) { bank ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onBankSelected(bank) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = Neutral50,
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Neutral300)
                                ) {
                                    AsyncImage(
                                        model = bank.logo,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().padding(2.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = bank.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(text = bank.name, style = MaterialTheme.typography.bodySmall, color = Neutral500, maxLines = 1)
                                }
                            }
                            HorizontalDivider(color = Neutral100)
                        }
                    }
                }
            }
        }
    }
}
