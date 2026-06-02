package com.example.ezroom.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    roomName: String = "Phòng 101",
    baseRentPrice: Long = 3000000L,
    onNavigateBack: () -> Unit,
    onInvoiceCreated: () -> Unit
) {
    var oldElectricity by remember { mutableStateOf("") }
    var newElectricity by remember { mutableStateOf("") }
    var oldWater by remember { mutableStateOf("") }
    var newWater by remember { mutableStateOf("") }
    var otherCosts by remember { mutableStateOf("") }

    val formatter = remember { DecimalFormat("#,### đ") }

    val elecUsage = (newElectricity.toIntOrNull() ?: 0) - (oldElectricity.toIntOrNull() ?: 0)
    val waterUsage = (newWater.toIntOrNull() ?: 0) - (oldWater.toIntOrNull() ?: 0)

    val totalAmount = remember(oldElectricity, newElectricity, oldWater, newWater, otherCosts) {
        val eUsage = if (elecUsage > 0) elecUsage else 0
        val wUsage = if (waterUsage > 0) waterUsage else 0
        val other = otherCosts.toLongOrNull() ?: 0L
        baseRentPrice + (eUsage * 3500L) + (wUsage * 15000L) + other
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LẬP HÓA ĐƠN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OrangePrimary)
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
            // Header: Thông tin phòng - Hạ font xuống 16.sp/SemiBold giống BookingForm
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

            // Nhóm Điện
            InvoiceInputGroup(
                title = "Chỉ số Điện (3.500đ/kWh)",
                icon = Icons.Default.Bolt,
                unit = "kWh",
                oldValue = oldElectricity,
                newValue = newElectricity,
                onOldChange = { oldElectricity = it },
                onNewChange = { newElectricity = it },
                usage = elecUsage
            )

            // Nhóm Nước
            InvoiceInputGroup(
                title = "Chỉ số Nước (15.000đ/m³)",
                icon = Icons.Default.WaterDrop,
                unit = "m³",
                oldValue = oldWater,
                newValue = newWater,
                onOldChange = { oldWater = it },
                onNewChange = { newWater = it },
                usage = waterUsage
            )

            // Chi phí khác
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Chi phí phát sinh khác",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = OnBackgroundLight,
                    modifier = Modifier.padding(start = 4.dp)
                )
                OutlinedTextField(
                    value = otherCosts,
                    onValueChange = { otherCosts = it },
                    placeholder = { Text("Nhập số tiền phát sinh...") },
                    prefix = { Text("₫ ", color = OrangePrimary, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnBackgroundLight,
                        unfocusedTextColor = OnBackgroundLight,
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f)
                    )
                )
            }

            // Tổng kết tiền - Thu gọn kích cỡ, tinh tế và đồng bộ hơn
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

            // Button chuẩn 50.dp, bo góc 8.dp y hệt BookingFormScreen
            Button(
                onClick = onInvoiceCreated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("XÁC NHẬN & GỬI HÓA ĐƠN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
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
    usage: Int
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
                OutlinedTextField(
                    value = oldValue,
                    onValueChange = onOldChange,
                    label = { Text("Số cũ") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f)
                    )
                )
                OutlinedTextField(
                    value = newValue,
                    onValueChange = onNewChange,
                    label = { Text("Số mới") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f)
                    )
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
