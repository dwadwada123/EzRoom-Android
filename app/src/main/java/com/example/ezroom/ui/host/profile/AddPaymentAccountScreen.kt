package com.example.ezroom.ui.host.profile

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ezroom.data.repository.BankRepositoryImpl
import com.example.ezroom.data.repository.PaymentAccountRepositoryImpl
import com.example.ezroom.domain.model.Bank
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

val peaceSansFont: FontFamily = FontFamily.Default

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
    val uiState by viewModel.uiState.collectAsState()

    AddPaymentAccountContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddAccount = { bank, accNum, owner -> viewModel.onAddAccount(bank, accNum, owner) },
        onBankSearch = { viewModel.onBankSearch(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentAccountContent(
    uiState: PaymentAccountUiState,
    onNavigateBack: () -> Unit,
    onAddAccount: (Bank, String, String) -> Unit,
    onBankSearch: (String) -> Unit
) {
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountNumber by rememberSaveable { mutableStateOf("") }
    var accountOwner by rememberSaveable { mutableStateOf("") }
    var showBankPicker by remember { mutableStateOf(false) }

    val isFormValid = selectedBank != null && accountNumber.length >= 6 && accountOwner.isNotBlank()
    val cardBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Thêm tài khoản", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont) },
                    navigationIcon = {
                        Surface(
                            modifier = Modifier.padding(start = 12.dp).size(40.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.AccountBalance, null, tint = MaterialTheme.colorScheme.primary)
                            Text("Ngân hàng thụ hưởng", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Selector ngân hàng
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Chọn ngân hàng", fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(
                                onClick = { showBankPicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedBank != null) {
                                        AsyncImage(model = selectedBank!!.logo, contentDescription = null, modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(selectedBank!!.name, fontSize = 15.sp, fontFamily = peaceSansFont, modifier = Modifier.weight(1f))
                                    } else {
                                        Icon(Icons.Outlined.BarChart, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Chọn ngân hàng", fontSize = 14.sp, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f))
                                    }
                                    Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // Form input Số tài khoản & Tên chủ tài khoản
                        FormField(
                            label = "Số tài khoản",
                            value = accountNumber,
                            onValueChange = { if (it.all { c -> c.isDigit() }) accountNumber = it },
                            placeholder = "Nhập số tài khoản",
                            icon = Icons.Outlined.Tag,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        FormField(
                            label = "Tên chủ tài khoản",
                            value = accountOwner,
                            onValueChange = { accountOwner = it.uppercase() },
                            placeholder = "NHẬP TÊN CHỦ TÀI KHOẢN",
                            icon = Icons.Outlined.Person,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (isFormValid) {
                            onAddAccount(selectedBank!!, accountNumber, accountOwner)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = isFormValid,
                    shape = CircleShape
                ) {
                    Text("LƯU TÀI KHOẢN", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                }

                // Security Tag
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Shield, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bảo mật tuyệt đối", fontSize = 12.sp, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (showBankPicker) {
            BankPickerDialog(
                banks = uiState.filteredBanks,
                query = uiState.bankQuery,
                onQueryChange = onBankSearch,
                onBankSelected = { selectedBank = it; showBankPicker = false },
                onDismiss = { showBankPicker = false },
                isLoading = uiState.isLoading
            )
        }
    }
}

// Component phụ tái sử dụng giúp thu gọn Form
@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontFamily = peaceSansFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.outline) },
            leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(fontFamily = peaceSansFont, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

// Dialog chọn ngân hàng
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Chọn ngân hàng", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm tên ngân hàng...", fontFamily = peaceSansFont) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = CircleShape,
                    textStyle = TextStyle(fontFamily = peaceSansFont),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(banks) { bank ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onBankSelected(bank) }.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    AsyncImage(model = bank.logo, contentDescription = null, modifier = Modifier.fillMaxSize().padding(2.dp), contentScale = ContentScale.Fit)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(bank.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                                    Text(bank.name, style = MaterialTheme.typography.bodySmall, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPaymentAccountPreview() {
    val sampleBank = Bank(1, "Vietcombank", "VCB", "970436", "https://api.vietqr.io/img/VCB.png")
    EzRoomTheme {
        AddPaymentAccountContent(
            uiState = PaymentAccountUiState(filteredBanks = listOf(sampleBank), isLoading = false),
            onNavigateBack = {},
            onAddAccount = { _, _, _ -> },
            onBankSearch = {}
        )
    }
}