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
import com.example.ezroom.data.repository.ContractRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
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
            ContractViewModel(
                GetContractsUseCase(repository),
                SignContractUseCase(repository),
                repository,
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
            // UI Component: Contextual Host Actions
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (contract.status) {
                        ContractStatus.ACTIVE -> {
                            OutlinedButton(
                                onClick = { /* Logic for termination warning */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                                border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.3f))
                            ) {
                                Text("CHẤM DỨT HỢP ĐỒNG SỚM", fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {
                            Text(
                                text = "Mã hợp đồng: ${contract.id.uppercase()}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500
                            )
                        }
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
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Neutral300)
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

                    ContractDetailRow(label = "Phòng thuê", value = contract.roomName)
                    ContractDetailRow(label = "Thời hạn", value = "${contract.startDate} - ${contract.endDate}")
                    ContractDetailRow(label = "Tiền cọc", value = formatter.format(contract.depositAmount))
                }
            }
        }
    }
}

@Composable
private fun FintechStatusBanner(contract: Contract) {
    val (color, icon, text) = when (contract.depositStatus) {
        DepositStatus.FROZEN -> Triple(PrimaryMain, Icons.Default.Lock, "Tiền cọc đang được App đóng băng. Giải ngân vào: ${contract.disburseDate}")
        DepositStatus.DISBURSED -> Triple(SuccessEmerald, Icons.Default.CheckCircle, "Tiền cọc đã giải ngân vào tài khoản của bạn.")
        DepositStatus.UNPAID -> Triple(AccentAmber, Icons.Default.HourglassEmpty, "Chờ người thuê thanh toán tiền cọc.")
        else -> Triple(Neutral500, Icons.Default.Info, "Trạng thái hợp đồng: ${contract.status}")
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
