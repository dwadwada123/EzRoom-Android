package com.example.ezroom.ui.host.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.OrangePrimary

/**
 * Screen for linking a bank account for Host module.
 *
 * @param onBackClick Callback when the back button is clicked.
 * @param onLinkSuccess Callback when the bank account is successfully linked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkBankAccountScreen(
    onBackClick: () -> Unit = {},
    onLinkSuccess: (bankName: String, accountNo: String, holderName: String) -> Unit = { _, _, _ -> },
) {
    val scrollState = rememberScrollState()

    // --- Mock Data ---
    val banks = listOf("Vietcombank", "Techcombank", "BIDV", "Agribank", "MB Bank")

    // --- Form States ---
    var expanded by remember { mutableStateOf(value = false) }
    var selectedBank by remember { mutableStateOf("") }
    var accountNo by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    // --- Validation Logic ---
    val isFormValid = selectedBank.isNotEmpty() &&
            accountNo.isNotBlank() &&
            holderName.isNotBlank()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Tài khoản ngân hàng",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 2. Information Note Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Lưu ý: Tên chủ tài khoản phải trùng khớp với thông tin trên giấy tờ eKYC để đảm bảo tính minh bạch khi rút tiền cọc.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 3. Bank Selection Dropdown
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ngân hàng liên kết",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                // Note: Using a custom Box + DropdownMenu instead of ExposedDropdownMenuBox
                // to avoid NoSuchMethodError in Android Studio Preview environment.
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedBank,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Chọn ngân hàng") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        banks.forEach { bank ->
                            DropdownMenuItem(
                                text = { Text(text = bank) },
                                onClick = {
                                    selectedBank = bank
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. Account Details Input
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Account Number
                OutlinedTextField(
                    value = accountNo,
                    onValueChange = { if (it.all { char -> char.isDigit() }) accountNo = it },
                    label = { Text("Số tài khoản") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                // Account Holder Name
                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it.uppercase() },
                    label = { Text("Tên chủ tài khoản") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ví dụ: NGUYEN VAN A") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                // Branch (Optional)
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Chi nhánh (Tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. Action Button
            Button(
                onClick = {
                    onLinkSuccess(selectedBank, accountNo, holderName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "XÁC NHẬN LIÊN KẾT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LinkBankAccountScreenPreview() {
    EzRoomTheme {
        LinkBankAccountScreen()
    }
}
