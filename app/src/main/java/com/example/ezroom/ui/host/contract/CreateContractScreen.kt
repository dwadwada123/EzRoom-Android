package com.example.ezroom.ui.host.contract

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.ContractRepositoryImpl
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.model.ContractStatus
import com.example.ezroom.domain.model.DepositStatus
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.GetRoomsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*

private val peaceSansFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContractScreen(
    initialRoomId: String? = null,
    initialRenterPhone: String? = null,
    initialRenterName: String? = null,
    onBackClick: () -> Unit = {},
    onProceedToTerms: (Contract) -> Unit = {},
    viewModel: ContractViewModel = viewModel(
        factory = viewModelFactory {
            val repository = ContractRepositoryImpl()
            val roomRepo = RoomRepositoryImpl()
            ContractViewModel(
                GetContractsUseCase(repository),
                SignContractUseCase(repository),
                repository,
                GetRoomsUseCase(roomRepo),
                isHost = true
            )
        }
    )
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val rooms = uiState.rooms
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
    var showUnregisteredDialog by remember { mutableStateOf(false) }

    val authApi = remember { com.example.ezroom.data.remote.NetworkClient.createService<com.example.ezroom.data.remote.AuthApi>() }

    LaunchedEffect(rooms, initialRoomId, initialRenterPhone, initialRenterName) {
        if (!initialRoomId.isNullOrBlank()) {
            rooms.find { it.id == initialRoomId || it.title.equals(initialRoomId, ignoreCase = true) }?.let {
                selectedRoomId = it.id
                selectedRoomName = it.title
            }
        }
        if (!initialRenterPhone.isNullOrBlank() && renterPhone.isEmpty()) renterPhone = initialRenterPhone
        if (!initialRenterName.isNullOrBlank() && renterName.isEmpty()) renterName = initialRenterName
    }

    val calendar = Calendar.getInstance()
    val showDatePicker = { onDateSelected: (String) -> Unit ->
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> onDateSelected("$dayOfMonth/${month + 1}/$year") },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val isFormValid = selectedRoomId.isNotEmpty() && renterName.isNotBlank() && renterPhone.isNotBlank() && startDate.isNotBlank() && depositAmount.isNotBlank()

    fun proceedWithContractCreation(foundRenterId: String) {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDeposit = depositAmount.toLongOrNull() ?: 0L
        val initialDepositStatus = if (parsedDeposit == 0L || selectedStatus == "Đã đóng") DepositStatus.FROZEN else DepositStatus.UNPAID
        val currentHostName = com.example.ezroom.util.TokenManager.getUser()?.name ?: "Chủ nhà"

        val contract = Contract(
            id = "",
            roomId = selectedRoomId,
            roomName = selectedRoomName,
            renterName = renterName,
            renterPhone = renterPhone,
            hostName = currentHostName,
            startDate = startDate,
            endDate = endDate,
            depositAmount = parsedDeposit,
            depositStatus = initialDepositStatus,
            status = ContractStatus.WAITING_SIGN,
            dateCreated = sdf.format(Date())
        )
        viewModel.createContract(contract)
        onProceedToTerms(contract)
    }

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Khởi tạo hợp đồng", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont, color = Neutral900) },
                    navigationIcon = {
                        Surface(
                            modifier = Modifier.padding(start = 12.dp).size(40.dp),
                            shape = CircleShape,
                            color = White,
                            shadowElevation = 2.dp
                        ) {
                            IconButton(onClick = onBackClick, enabled = !isLoading) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Neutral900, modifier = Modifier.size(20.dp))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = White)
                )
            },
            containerColor = White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // CHỌN PHÒNG TRỌ
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("CHỌN PHÒNG TRỌ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ContractInputField(
                            value = selectedRoomName,
                            placeholder = "Chọn phòng để lập hợp đồng",
                            leadingIcon = Icons.Outlined.Home,
                            readOnly = true,
                            enabled = !isLoading,
                            onClick = { expandedRoomDropdown = true },
                            trailingIcon = { Icon(if (expandedRoomDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null, tint = Neutral700) }
                        )
                        DropdownMenu(
                            expanded = expandedRoomDropdown,
                            onDismissRequest = { expandedRoomDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.85f).background(White)
                        ) {
                            rooms.forEach { room ->
                                DropdownMenuItem(
                                    text = { Text(room.title, fontFamily = peaceSansFont) },
                                    onClick = {
                                        selectedRoomId = room.id
                                        selectedRoomName = room.title
                                        expandedRoomDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // THÔNG TIN NGƯỜI THUÊ
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("THÔNG TIN NGƯỜI THUÊ")
                    ContractInputField(label = "Họ và tên khách thuê", value = renterName, onValueChange = { renterName = it }, placeholder = "Nguyễn Văn A", leadingIcon = Icons.Outlined.Person, enabled = !isLoading)
                    ContractInputField(label = "Số điện thoại", value = renterPhone, onValueChange = { if (it.all { c -> c.isDigit() }) renterPhone = it }, placeholder = "0987 654 321", leadingIcon = Icons.Outlined.Phone, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), enabled = !isLoading)
                }

                // THỜI HẠN THUÊ
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("THỜI HẠN THUÊ")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ContractInputField(label = "Ngày bắt đầu", value = startDate, placeholder = "DD/MM/YYYY", leadingIcon = Icons.Outlined.CalendarToday, readOnly = true, enabled = !isLoading, onClick = { showDatePicker { startDate = it } }, modifier = Modifier.weight(1f))
                        ContractInputField(label = "Ngày kết thúc (Dự kiến)", value = endDate, placeholder = "DD/MM/YYYY", leadingIcon = Icons.Outlined.CalendarToday, readOnly = true, enabled = !isLoading, onClick = { showDatePicker { endDate = it } }, modifier = Modifier.weight(1f))
                    }
                }

                // CẤU HÌNH TIỀN CỌC
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("CẤU HÌNH TIỀN CỌC")
                    ContractInputField(label = "Số tiền cọc", value = depositAmount, onValueChange = { if (it.all { c -> c.isDigit() }) depositAmount = it }, placeholder = "5.000.000đ", leadingIcon = Icons.Outlined.CreditCard, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), enabled = !isLoading)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        ContractInputField(
                            label = "Trạng thái",
                            value = selectedStatus,
                            leadingIcon = Icons.Outlined.AccessTime,
                            readOnly = true,
                            enabled = !isLoading,
                            onClick = { expandedStatusDropdown = true },
                            trailingIcon = { Icon(if (expandedStatusDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null, tint = Neutral700) }
                        )
                        DropdownMenu(
                            expanded = expandedStatusDropdown,
                            onDismissRequest = { expandedStatusDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.85f).background(White)
                        ) {
                            depositStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status, fontFamily = peaceSansFont) },
                                    onClick = { selectedStatus = status; expandedStatusDropdown = false }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                try {
                                    val res = authApi.checkPhone(renterPhone.trim())
                                    isLoading = false
                                    if (res.success && res.exists && res.user != null) {
                                        proceedWithContractCreation(res.user.id)
                                    } else showUnregisteredDialog = true
                                } catch (e: Exception) {
                                    isLoading = false
                                    showUnregisteredDialog = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = isFormValid && !isLoading,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain, disabledContainerColor = PrimaryMain.copy(alpha = 0.4f))
                ) {
                    Text("TIẾP TỤC / SOẠN ĐIỀU KHOẢN", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont, color = White)
                }
            }
        }

        if (showUnregisteredDialog) {
            AlertDialog(
                onDismissRequest = { showUnregisteredDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = PrimaryMain)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chưa tìm thấy tài khoản", fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                    }
                },
                text = {
                    Text(
                        "Số điện thoại $renterPhone chưa có tài khoản trên ứng dụng EzRoom.\n\n" +
                                "Để gửi hợp đồng online và yêu cầu người thuê ký xác nhận, vui lòng nhắc khách thuê tải ứng dụng EzRoom và đăng ký bằng SĐT này trước.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = peaceSansFont
                    )
                },
                confirmButton = {
                    Button(onClick = { showUnregisteredDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain)) {
                        Text("ĐÃ HIỂU", fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                    }
                },
                containerColor = White
            )
        }

        if (isLoading) LoadingWidget()
    }
}

@Composable
fun SectionTitle(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.width(3.dp).height(14.dp).background(PrimaryMain, RoundedCornerShape(2.dp)))
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = peaceSansFont, color = PrimaryMain)
    }
}

@Composable
private fun ContractInputField(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit = {},
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!label.isNullOrBlank()) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = peaceSansFont, color = Neutral500)
        }
        Surface(
            modifier = Modifier.fillMaxWidth().then(if (onClick != null && enabled) Modifier.clickable { onClick() } else Modifier),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, borderColor)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontFamily = peaceSansFont, fontSize = 14.sp, color = Neutral500.copy(alpha = 0.6f)) },
                leadingIcon = leadingIcon?.let { { Icon(it, null, tint = Neutral700, modifier = Modifier.size(20.dp)) } },
                trailingIcon = trailingIcon,
                readOnly = readOnly,
                enabled = enabled && onClick == null,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                textStyle = TextStyle(fontFamily = peaceSansFont, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Neutral900),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateContractScreenPreview() {
    EzRoomTheme(darkTheme = false) {
        CreateContractScreen()
    }
}