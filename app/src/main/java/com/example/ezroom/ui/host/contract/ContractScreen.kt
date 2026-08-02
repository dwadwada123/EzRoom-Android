package com.example.ezroom.ui.host.contract

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.ezroom.data.repository.ContractRepositoryImpl
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
import com.example.ezroom.domain.usecase.GetRoomsUseCase
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

// UI Component: Contract Detail for Host with Fintech Logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostContractScreen(
    contract: Contract,
    onNavigateBack: () -> Unit = {},
    onSignContract: (TransactionType) -> Unit = {},
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
    val formatter = remember { DecimalFormat("#,### đ") }

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lý hợp đồng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            val scope = rememberCoroutineScope()
            var isSending by remember { mutableStateOf(false) }
            var showTerminateDialog by remember { mutableStateOf(false) }
            var terminateReason by remember { mutableStateOf("") }
            var isTerminating by remember { mutableStateOf(false) }

            if (showTerminateDialog) {
                AlertDialog(
                    onDismissRequest = { if (!isTerminating) showTerminateDialog = false },
                    title = { Text("Xác nhận chấm dứt hợp đồng sớm", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Lưu ý: Việc Chủ nhà tự ý chấm dứt hợp đồng sớm sẽ tự động hoàn trả tiền cọc cho Người thuê theo quy định bảo hộ EzRoom Escrow.",
                                fontSize = 13.sp, color = ErrorRose
                            )
                            OutlinedTextField(
                                value = terminateReason,
                                onValueChange = { terminateReason = it },
                                label = { Text("Lý do chấm dứt hợp đồng") },
                                placeholder = { Text("Nhập lý do...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    isTerminating = true
                                    val repo = ContractRepositoryImpl()
                                    repo.terminateContract(contract.id, terminateReason.ifBlank { "Chủ nhà chấm dứt hợp đồng sớm" }, "HOST")
                                    isTerminating = false
                                    showTerminateDialog = false
                                    onNavigateBack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                            enabled = !isTerminating
                        ) {
                            Text("XÁC NHẬN CHẤM DỨT", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTerminateDialog = false }) {
                            Text("HỦY")
                        }
                    }
                )
            }

            // UI Component: Contextual Host Actions
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (contract.status) {
                        ContractStatus.DRAFT -> {
                            PrimaryButton(
                                text = if (isSending) "ĐANG GỬI HỢP ĐỒNG..." else "GỬI HỢP ĐỒNG CHO NGƯỜI THUÊ",
                                onClick = {
                                    scope.launch {
                                        isSending = true
                                        try {
                                            val isZeroDeposit = contract.depositAmount == 0L
                                            val currentHostName = contract.hostName ?: com.example.ezroom.util.TokenManager.getUser()?.name ?: "Chủ nhà"
                                            val finalContract = contract.copy(
                                                hostName = currentHostName,
                                                status = ContractStatus.WAITING_SIGN,
                                                depositStatus = if (isZeroDeposit) DepositStatus.FROZEN else contract.depositStatus
                                            )
                                            ContractRepositoryImpl().createContract(finalContract)
                                        } catch (e: Exception) {
                                            // Handle fallback
                                        }
                                        isSending = false
                                        onSignContract(TransactionType.DEPOSIT)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSending
                            )
                        }
                        ContractStatus.WAITING_SIGN -> {
                            Surface(
                                color = AccentAmber.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⏳ Hợp đồng đã gửi - Đang chờ người thuê ký xác nhận",
                                    color = AccentAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        ContractStatus.WAITING_DEPOSIT -> {
                            Surface(
                                color = PrimaryMain.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💳 Người thuê đã ký - Đang chờ thanh toán tiền cọc",
                                    color = PrimaryMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        ContractStatus.ACTIVE -> {
                            OutlinedButton(
                                onClick = { showTerminateDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                                border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.3f))
                            ) {
                                Text("CHẤM DỨT HỢP ĐỒNG SỚM", fontWeight = FontWeight.Bold)
                            }
                        }
                        ContractStatus.TERMINATED, ContractStatus.CANCELLED -> {
                            Surface(
                                color = ErrorRose.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🚫 Hợp đồng này đã chấm dứt",
                                    color = ErrorRose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // UI Component: Fintech Status Badge
            FintechStatusBanner(contract)

            // UI Component: Legal Document
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Neutral100)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "HỢP ĐỒNG THUÊ PHÒNG TRỌ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryMain
                    )

                    HorizontalDivider(color = Neutral100)

                    // Host Info (Side A)
                    ContractPartyItem(label = "BÊN A (CHỦ THUÊ)", name = contract.hostName ?: "Chủ trọ EzRoom")
                    
                    // Renter Info (Side B)
                    ContractPartyItem(label = "BÊN B (NGƯỜI THUÊ)", name = contract.renterName)

                    HorizontalDivider(color = Neutral100)

                    ContractDetailRow(label = "Phòng thuê", value = contract.roomName.takeIf { !it.isNullOrBlank() } ?: "Phòng trọ")
                    ContractDetailRow(label = "Địa chỉ", value = contract.address?.takeIf { it.isNotBlank() } ?: "Địa chỉ phòng trọ")
                    ContractDetailRow(label = "Thời hạn", value = "${contract.startDate} - ${contract.endDate}")
                    ContractDetailRow(label = "Tiền cọc", value = formatter.format(contract.depositAmount))
                }
            }
        }
    }
}

private fun isDateArrivedOrPast(dateStr: String?): Boolean {
    if (dateStr.isNullOrBlank()) return true
    return try {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val date = sdf.parse(dateStr)
        val today = sdf.parse(sdf.format(java.util.Date()))
        date != null && today != null && !date.after(today)
    } catch (e: Exception) {
        true
    }
}

@Composable
private fun FintechStatusBanner(contract: Contract) {
    val isArrived = isDateArrivedOrPast(contract.disburseDate)
    val (color, icon, text) = when {
        contract.depositStatus == DepositStatus.FROZEN && isArrived -> 
            Triple(SuccessEmerald, Icons.Default.CheckCircle, "Tiền cọc đã đủ điều kiện giải ngân và được chuyển vào tài khoản của bạn.")
        contract.depositStatus == DepositStatus.FROZEN && !isArrived -> 
            Triple(PrimaryMain, Icons.Default.Lock, "Tiền cọc đang được App đóng băng. Giải ngân vào: ${contract.disburseDate}")
        contract.depositStatus == DepositStatus.DISBURSED -> 
            Triple(SuccessEmerald, Icons.Default.CheckCircle, "Tiền cọc đã giải ngân vào tài khoản của bạn.")
        contract.depositStatus == DepositStatus.UNPAID -> 
            Triple(AccentAmber, Icons.Default.HourglassEmpty, "Chờ người thuê thanh toán tiền cọc.")
        else -> 
            Triple(Neutral500, Icons.Default.Info, "Trạng thái hợp đồng: ${contract.status}")
    }

    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ContractPartyItem(label: String, name: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Neutral500)
        Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ContractDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Neutral500)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
