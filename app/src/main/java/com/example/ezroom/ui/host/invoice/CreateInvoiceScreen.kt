package com.example.ezroom.ui.host.invoice

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.repository.InvoiceRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetInvoicesUseCase
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.SmallTextField
import com.example.ezroom.ui.invoice.InvoiceViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    // Event callbacks
    roomName: String = "Phòng 101",
    baseRentPrice: Long = 3000000L,
    onNavigateBack: () -> Unit,
    onInvoiceCreated: () -> Unit,
    viewModel: InvoiceViewModel = viewModel(
        factory = viewModelFactory {
            val repository = InvoiceRepositoryImpl()
            InvoiceViewModel(GetInvoicesUseCase(repository), repository)
        }
    )
) {
    // State definitions
    val scope = rememberCoroutineScope()
    var oldElectricity by remember { mutableStateOf("") }
    var newElectricity by remember { mutableStateOf("") }
    var elecPrice by remember { mutableStateOf("3500") }
    
    var oldWater by remember { mutableStateOf("") }
    var newWater by remember { mutableStateOf("") }
    var waterPrice by remember { mutableStateOf("15000") }
    
    val otherCostItems = remember { mutableStateListOf<OtherCostItem>() }
    var isLoading by remember { mutableStateOf(false) }

    // Room selection dropdown state
    val mockRooms = MockData.rooms
    var selectedRoom by remember { mutableStateOf(mockRooms.find { it.title == roomName } ?: mockRooms.first()) }
    var isRoomDropdownExpanded by remember { mutableStateOf(false) }

    val formatter = remember { DecimalFormat("#,### đ") }

    // Update prices when room is selected
    LaunchedEffect(selectedRoom) {
        elecPrice = selectedRoom.electricityPrice.toString()
        waterPrice = selectedRoom.waterPrice.toString()
    }

    val elecUsage = (newElectricity.toIntOrNull() ?: 0) - (oldElectricity.toIntOrNull() ?: 0)
    val waterUsage = (newWater.toIntOrNull() ?: 0) - (oldWater.toIntOrNull() ?: 0)

    val totalAmount = remember(oldElectricity, newElectricity, elecPrice, oldWater, newWater, waterPrice, otherCostItems.size, otherCostItems.map { it.amount }.sum(), selectedRoom) {
        val eUsage = if (elecUsage > 0) elecUsage else 0
        val wUsage = if (waterUsage > 0) waterUsage else 0
        val ePrice = elecPrice.toLongOrNull() ?: 0L
        val wPrice = waterPrice.toLongOrNull() ?: 0L
        val otherTotal = otherCostItems.sumOf { it.amount }
        selectedRoom.price + (eUsage * ePrice) + (wUsage * wPrice) + otherTotal
    }

    val isFormValid = oldElectricity.isNotEmpty() && newElectricity.isNotEmpty() && 
                      oldWater.isNotEmpty() && newWater.isNotEmpty() &&
                      elecUsage >= 0 && waterUsage >= 0

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Neutral50,
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "LẬP HÓA ĐƠN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onNavigateBack, 
                                enabled = !isLoading,
                                modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
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
                // Room selection dropdown menu
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Chọn phòng trọ",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = OnBackgroundLight,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLoading) { isRoomDropdownExpanded = true }
                    ) {
                        OutlinedTextField(
                            value = selectedRoom.title,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        )
                        
                        DropdownMenu(
                            expanded = isRoomDropdownExpanded,
                            onDismissRequest = { isRoomDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            mockRooms.forEach { room ->
                                DropdownMenuItem(
                                    text = { Text(room.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    onClick = {
                                        // Update selected room state
                                        selectedRoom = room
                                        isRoomDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tiền phòng cố định: ${formatter.format(selectedRoom.price)}", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), 
                            fontSize = 13.sp
                        )
                    }
                }

                // Input fields group: Electricity
                InvoiceInputGroup(
                    title = "Chỉ số Điện (đ/kWh)",
                    icon = Icons.Default.Bolt,
                    unit = "kWh",
                    oldValue = oldElectricity,
                    newValue = newElectricity,
                    priceValue = elecPrice,
                    onOldChange = { oldElectricity = it },
                    onNewChange = { newElectricity = it },
                    onPriceChange = { if (it.all { char -> char.isDigit() }) elecPrice = it },
                    usage = elecUsage,
                    enabled = !isLoading
                )

                // Input fields group: Water
                InvoiceInputGroup(
                    title = "Chỉ số Nước (đ/m³)",
                    icon = Icons.Default.WaterDrop,
                    unit = "m³",
                    oldValue = oldWater,
                    newValue = newWater,
                    priceValue = waterPrice,
                    onOldChange = { oldWater = it },
                    onNewChange = { newWater = it },
                    onPriceChange = { if (it.all { char -> char.isDigit() }) waterPrice = it },
                    usage = waterUsage,
                    enabled = !isLoading
                )

                // Input fields group: Other costs
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chi phí phát sinh",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = OnBackgroundLight,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        TextButton(onClick = { otherCostItems.add(OtherCostItem("", 0L)) }) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm mục", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    
                    otherCostItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomTextField(
                                value = item.reason,
                                onValueChange = { otherCostItems[index] = item.copy(reason = it) },
                                label = "Lý do",
                                placeholder = "VD: Đền bù...",
                                modifier = Modifier.weight(1.2f),
                                enabled = !isLoading
                            )
                            CustomTextField(
                                value = if (item.amount == 0L) "" else item.amount.toString(),
                                onValueChange = { if (it.all { char -> char.isDigit() }) otherCostItems[index] = item.copy(amount = it.toLongOrNull() ?: 0L) },
                                label = "Số tiền",
                                placeholder = "0",
                                modifier = Modifier.weight(0.8f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !isLoading
                            )
                            IconButton(onClick = { otherCostItems.removeAt(index) }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // Total amount summary section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TỔNG TIỀN TỰ ĐỘNG", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.7f))
                            Text(formatter.format(totalAmount), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
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
                                
                                // Simulate saving to MockData
                                val newInvoice = Invoice(
                                    id = "INV-${UUID.randomUUID().toString().take(6).uppercase()}",
                                    roomId = selectedRoom.id,
                                    roomName = selectedRoom.title,
                                    period = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date()),
                                    roomPrice = selectedRoom.price,
                                    oldElectricity = oldElectricity.toIntOrNull() ?: 0,
                                    newElectricity = newElectricity.toIntOrNull() ?: 0,
                                    oldWater = oldWater.toIntOrNull() ?: 0,
                                    newWater = newWater.toIntOrNull() ?: 0,
                                    otherCosts = otherCostItems.toList(),
                                    status = InvoiceStatus.UNPAID,
                                    type = TransactionType.RENT,
                                    dateCreated = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                )
                                viewModel.createInvoice(newInvoice)

                                delay(1000)
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
    priceValue: String,
    onOldChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    usage: Int,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.weight(1f))
                if (usage > 0) {
                    Surface(color = SuccessEmerald.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "+$usage $unit",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = SuccessEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallTextField(
                    value = oldValue,
                    onValueChange = onOldChange,
                    label = "Số cũ",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = enabled
                )
                SmallTextField(
                    value = newValue,
                    onValueChange = onNewChange,
                    label = "Số mới",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = enabled,
                    isError = usage < 0 && newValue.isNotEmpty()
                )
                SmallTextField(
                    value = priceValue,
                    onValueChange = onPriceChange,
                    label = "Đơn giá",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = enabled
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


