package com.example.ezroom.ui.host.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDepositAccountScreen(
    // Event callbacks
    onNavigateBack: () -> Unit = {}
) {
    // State definitions
    val scope = rememberCoroutineScope()
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var accountOwner by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = bankName.isNotBlank() && accountNumber.isNotBlank() && accountOwner.isNotBlank()

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Tài khoản nhận cọc",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Input fields group
                CustomTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = "Tên ngân hàng",
                    placeholder = "VD: Vietcombank, Techcombank...",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                CustomTextField(
                    value = accountNumber,
                    onValueChange = { if (it.all { char -> char.isDigit() }) accountNumber = it },
                    label = "Số tài khoản",
                    placeholder = "Nhập số tài khoản của bạn",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                CustomTextField(
                    value = accountOwner,
                    onValueChange = { accountOwner = it },
                    label = "Tên chủ tài khoản",
                    placeholder = "Nhập tên in trên thẻ (không dấu)",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons row
                PrimaryButton(
                    text = "LƯU THÔNG TIN",
                    onClick = { 
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading
                )
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDepositAccountScreenPreview() {
    EzRoomTheme(darkTheme = false) {
        AddDepositAccountScreen()
    }
}
