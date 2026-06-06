package com.example.ezroom.ui.host.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    // Event callbacks
    roomName: String = "Phòng 101",
    baseRentPrice: Long = 3000000L,
    onNavigateBack: () -> Unit,
    onInvoiceCreated: () -> Unit
) {
    // State definitions
    val scope = rememberCoroutineScope()
    var oldElectricity by remember { mutableStateOf("") }
    var newElectricity by remember { mutableStateOf("") }
    var oldWater by remember { mutableStateOf("") }
    var newWater by remember { mutableStateOf("") }
    var otherCosts by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val formatter = remember { DecimalFormat("#,### đ") }

    val elecUsage = (newElectricity.toIntOrNull() ?: 0) - (oldElectricity.toIntOrNull() ?: 0)
    val waterUsage = (newWater.toIntOrNull() ?: 0) - (oldWater.toIntOrNull() ?: 0)

    val totalAmount = remember(oldElectricity, newElectricity, oldWater, newWater, otherCosts) {
        val eUsage = if (elecUsage > 0) elecUsage else 0
        val wUsage = if (waterUsage > 0) waterUsage else 0
        val other = otherCosts.toLongOrNull() ?: 0L
        baseRentPrice + (eUsage * 3500L) + (wUsage * 15000L) + other
    }

    val isFormValid = oldElectricity.isNotEmpty() && newElectricity.isNotEmpty() && 
                      oldWater.isNotEmpty() && newWater.isNotEmpty() &&
                      elecUsage >= 0 && waterUsage >= 0

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = BackgroundLight,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "LẬP HÓA ĐƠN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = OrangePrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = OrangePrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = SurfaceLight
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header: Room Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Đang lập hóa đơn cho:", color = OnPrimaryLight.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(roomName, color = OnPrimaryLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = OnPrimaryLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tiền phòng cố định: ${formatter.format(baseRentPrice)}", color = OnPrimaryLight, fontSize = 14.sp)
                        }
                    }
                }

                // Input fields group: Electricity
                InvoiceInputGroup(
                    title = "Chỉ số Điện (3.500đ/kWh)",
                    icon = Icons.Default.Bolt,
                    unit = "kWh",
                    oldValue = oldElectricity,
                    newValue = newElectricity,
                    onOldChange = { oldElectricity = it },
                    onNewChange = { newElectricity = it },
                    usage = elecUsage,
                    enabled = !isLoading
                )

                // Input fields group: Water
                InvoiceInputGroup(
                    title = "Chỉ số Nước (15.000đ/m³)",
                    icon = Icons.Default.WaterDrop,
                    unit = "m³",
                    oldValue = oldWater,
                    newValue = newWater,
                    onOldChange = { oldWater = it },
                    onNewChange = { newWater = it },
                    usage = waterUsage,
                    enabled = !isLoading
                )

                // Input fields group: Other costs
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Chi phí phát sinh khác",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = OnBackgroundLight,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    CustomTextField(
                        value = otherCosts,
                        onValueChange = { if (it.all { char -> char.isDigit() }) otherCosts = it },
                        label = "Số tiền phát sinh",
                        placeholder = "Nhập số tiền phát sinh...",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )
                }

                // Total amount summary section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TỔNG TIỀN TỰ ĐỘNG", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OnBackgroundLight.copy(alpha = 0.5f))
                            Text(formatter.format(totalAmount), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealAccent, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons row
                PrimaryButton(
                    text = "XÁC NHẬN & GỬI HÓA ĐƠN",
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onInvoiceCreated()
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
fun InvoiceInputGroup(
    title: String,
    icon: ImageVector,
    unit: String,
    oldValue: String,
    newValue: String,
    onOldChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    usage: Int,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnBackgroundLight)
                Spacer(modifier = Modifier.weight(1f))
                if (usage > 0) {
                    Surface(color = TealAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "+$usage $unit",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomTextField(
                    value = oldValue,
                    onValueChange = onOldChange,
                    label = "Số cũ",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = enabled
                )
                CustomTextField(
                    value = newValue,
                    onValueChange = onNewChange,
                    label = "Số mới",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = enabled,
                    isError = usage < 0 && newValue.isNotEmpty()
                )
            }
            if (usage < 0 && newValue.isNotEmpty()) {
                Text(
                    text = "Số mới không được nhỏ hơn số cũ",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateInvoiceScreenPreview() {
    EzRoomTheme {
        CreateInvoiceScreen(
            onNavigateBack = {},
            onInvoiceCreated = {}
        )
    }
}
