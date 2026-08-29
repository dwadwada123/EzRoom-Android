package com.example.ezroom.ui.host.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDepositAccountScreen(
    initialBankName: String = "",
    initialAccountNumber: String = "",
    initialAccountOwner: String = "",
    onNavigateBack: () -> Unit = {},
    onSaveAccount: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val scope = rememberCoroutineScope()
    var bankName by rememberSaveable { mutableStateOf(initialBankName) }
    var accountNumber by rememberSaveable { mutableStateOf(initialAccountNumber) }
    var accountOwner by rememberSaveable { mutableStateOf(initialAccountOwner) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val isFormValid = bankName.isNotBlank() && accountNumber.isNotBlank() && accountOwner.isNotBlank()
    val cardBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Tài khoản nhận cọc", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                    },
                    navigationIcon = {
                        Surface(
                            modifier = Modifier.padding(start = 12.dp).size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp
                        ) {
                            IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", modifier = Modifier.size(20.dp))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        Text("Liên kết tài khoản ngân hàng", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                        Text("Dùng để nhận tiền đặt cọc từ khách hàng", fontSize = 13.sp, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Form Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DepositInputField(
                            label = "Tên ngân hàng",
                            value = bankName,
                            onValueChange = { bankName = it },
                            placeholder = "Chọn hoặc nhập tên ngân hàng",
                            icon = Icons.Outlined.AccountBalance,
                            enabled = !isLoading
                        )
                        DepositInputField(
                            label = "Số tài khoản",
                            value = accountNumber,
                            onValueChange = { if (it.all { c -> c.isDigit() }) accountNumber = it },
                            placeholder = "Nhập số tài khoản",
                            icon = Icons.Outlined.CreditCard,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !isLoading
                        )
                        DepositInputField(
                            label = "Tên chủ tài khoản",
                            value = accountOwner,
                            onValueChange = { accountOwner = it.uppercase() },
                            placeholder = "NHẬP TÊN CHỦ TÀI KHOẢN",
                            icon = Icons.Outlined.Person,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            enabled = !isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Button
                Button(
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onSaveAccount(bankName, accountNumber, accountOwner)
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = isFormValid && !isLoading,
                    shape = CircleShape
                ) {
                    Text("LƯU THÔNG TIN", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                }

                // Security Note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Thông tin của bạn được bảo mật tuyệt đối", fontSize = 12.sp, fontFamily = peaceSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (isLoading) LoadingWidget()
    }
}

@Composable
private fun DepositInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = peaceSansFont,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontFamily = peaceSansFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.outline) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(fontFamily = peaceSansFont, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddDepositAccountScreenPreview() {
    EzRoomTheme(darkTheme = false) {
        AddDepositAccountScreen()
    }
}