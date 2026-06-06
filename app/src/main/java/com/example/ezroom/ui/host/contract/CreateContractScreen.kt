package com.example.ezroom.ui.host.contract

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.data.model.Contract
import com.example.ezroom.data.model.DepositStatus
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.OrangePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContractScreen(
    // Event callbacks
    onBackClick: () -> Unit = {},
    onProceedToTerms: (Contract) -> Unit = {}
) {
    // State definitions
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val rooms = listOf(
        "room_101" to "Phòng 101 - Lầu 1",
        "room_102" to "Phòng 102 - Lầu 1",
        "room_201" to "Phòng 201 - Lầu 2",
        "room_202" to "Phòng 202 - Lầu 2"
    )
    val depositStatuses = listOf("Chưa đóng", "Đã đóng")

    var expandedRoomDropdown by remember { mutableStateOf(false) }
    var selectedRoomId by remember { mutableStateOf("") }
    var selectedRoomName by remember { mutableStateOf("") }

    var renterName by remember { mutableStateOf("") }
    var renterPhone by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var depositAmount by remember { mutableStateOf("") }
    var expandedStatusDropdown by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(depositStatuses[0]) }
    var isLoading by remember { mutableStateOf(false) }

    // Date picker trigger
    val calendar = Calendar.getInstance()
    val datePickerDialog = { onDateSelected: (String) -> Unit ->
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val isFormValid = selectedRoomId.isNotEmpty() &&
            renterName.isNotBlank() &&
            renterPhone.isNotBlank() &&
            startDate.isNotBlank() &&
            depositAmount.isNotBlank()

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Khởi tạo hợp đồng",
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
                // Section: Room Selection
                SectionTitle(text = "Chọn phòng trọ")
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = selectedRoomName,
                        onValueChange = {},
                        label = "Phòng trọ",
                        placeholder = "Chọn phòng để lập hợp đồng",
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedRoomDropdown = true }, enabled = !isLoading) {
                                Icon(
                                    imageVector = if (expandedRoomDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = !isLoading) { expandedRoomDropdown = true }
                    )
                    DropdownMenu(
                        expanded = expandedRoomDropdown,
                        onDismissRequest = { expandedRoomDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text(room.second) },
                                onClick = {
                                    selectedRoomId = room.first
                                    selectedRoomName = room.second
                                    expandedRoomDropdown = false
                                }
                            )
                        }
                    }
                }

                // Section: Renter Information
                SectionTitle(text = "Thông tin người thuê")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CustomTextField(
                        value = renterName,
                        onValueChange = { renterName = it },
                        label = "Họ và tên khách thuê",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    CustomTextField(
                        value = renterPhone,
                        onValueChange = { if (it.all { char -> char.isDigit() }) renterPhone = it },
                        label = "Số điện thoại",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )
                }

                // Section: Duration Selection
                SectionTitle(text = "Thời hạn thuê")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(
                            value = startDate,
                            onValueChange = {},
                            label = "Ngày bắt đầu",
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = !isLoading,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog { startDate = it }.show() }, enabled = !isLoading) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Chọn ngày bắt đầu")
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(enabled = !isLoading) { datePickerDialog { startDate = it }.show() }
                        )
                    }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(
                            value = endDate,
                            onValueChange = {},
                            label = "Ngày kết thúc (Dự kiến)",
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = !isLoading,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog { endDate = it }.show() }, enabled = !isLoading) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Chọn ngày kết thúc")
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(enabled = !isLoading) { datePickerDialog { endDate = it }.show() }
                        )
                    }
                }

                // Section: Deposit Configuration
                SectionTitle(text = "Cấu hình tiền cọc")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        value = depositAmount,
                        onValueChange = { if (it.all { char -> char.isDigit() }) depositAmount = it },
                        label = "Số tiền cọc",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )
                    
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField(
                            value = selectedStatus,
                            onValueChange = {},
                            label = "Trạng thái",
                            readOnly = true,
                            enabled = !isLoading,
                            trailingIcon = {
                                IconButton(onClick = { expandedStatusDropdown = true }, enabled = !isLoading) {
                                    Icon(
                                        imageVector = if (expandedStatusDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(enabled = !isLoading) { expandedStatusDropdown = true }
                        )
                        DropdownMenu(
                            expanded = expandedStatusDropdown,
                            onDismissRequest = { expandedStatusDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.4f)
                        ) {
                            depositStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        selectedStatus = status
                                        expandedStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons row
                PrimaryButton(
                    text = "TIẾP TỤC / SOẠN ĐIỀU KHOẢN",
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1000)
                                isLoading = false
                                val contract = Contract(
                                    id = UUID.randomUUID().toString(),
                                    roomId = selectedRoomId,
                                    roomName = selectedRoomName,
                                    renterName = renterName,
                                    renterPhone = renterPhone,
                                    startDate = startDate,
                                    endDate = endDate,
                                    depositAmount = depositAmount.toLongOrNull() ?: 0L,
                                    depositStatus = if (selectedStatus == "Đã đóng") DepositStatus.PAID else DepositStatus.UNPAID
                                )
                                onProceedToTerms(contract)
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

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = OrangePrimary
        ),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CreateContractScreenPreview() {
    EzRoomTheme(darkTheme = false) {
        CreateContractScreen()
    }
}
