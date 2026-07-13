package com.example.ezroom.ui.renter.contract

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
import com.example.ezroom.ui.host.contract.ContractViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractScreen(
    contract: Contract,
    onNavigateBack: () -> Unit = {},
    onSignSuccess: () -> Unit = {},
    onPayClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    onDisputeClick: () -> Unit = {},
    viewModel: ContractViewModel = viewModel(
        factory = viewModelFactory {
            val repository = ContractRepositoryImpl()
            ContractViewModel(
                GetContractsUseCase(repository),
                SignContractUseCase(repository),
                repository,
                isHost = false
            )
        }
    )
) {
    var isAgreed by remember { mutableStateOf(false) }
    val formatter = remember { DecimalFormat("#,### đ") }

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết hợp đồng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (contract.status) {
                        ContractStatus.WAITING_SIGN -> {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { isAgreed = !isAgreed }.padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = isAgreed, onCheckedChange = { isAgreed = it })
                                Text("Tôi đã đọc và đồng ý với các điều khoản", style = MaterialTheme.typography.bodySmall)
                            }
                            PrimaryButton(
                                text = "KÝ HỢP ĐỒNG",
                                onClick = { 
                                    viewModel.signContract(contract.id)
                                    onSignSuccess()
                                },
                                enabled = isAgreed,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        ContractStatus.WAITING_DEPOSIT -> {
                            PrimaryButton(
                                text = "THANH TOÁN TIỀN CỌC (${formatter.format(contract.depositAmount)})",
                                onClick = onPayClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        ContractStatus.CANCELLED -> {
                            if (contract.depositStatus == DepositStatus.FROZEN) {
                                PrimaryButton(
                                    text = "YÊU CẦU HOÀN TIỀN",
                                    onClick = onRefundClick,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        ContractStatus.ACTIVE -> {
                            OutlinedButton(
                                onClick = onDisputeClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                                border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.3f))
                            ) {
                                Text("TỐ CÁO / TRANH CHẤP")
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
            if (contract.depositStatus == DepositStatus.FROZEN) {
                Surface(
                    color = SuccessEmerald.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tiền cọc của bạn đang được đóng băng an toàn tại EzRoom.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Neutral300)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Độc lập - Tự do - Hạnh phúc",
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(modifier = Modifier.width(100.dp), thickness = 1.dp)
                    }

                    Text(
                        text = "HỢP ĐỒNG THUÊ PHÒNG TRỌ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryMain
                    )

                    HorizontalDivider(color = Neutral100)

                    ContractSectionItem(title = "ĐIỀU 1: THÔNG TIN PHÒNG") {
                        Text(
                            text = "• Phòng: ${contract.roomName}\n• Địa chỉ: ${contract.id}",
                            fontSize = 13.sp, lineHeight = 20.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 2: THỜI HẠN & TIỀN CỌC") {
                        Text(
                            text = "• Từ ngày ${contract.startDate} đến ngày ${contract.endDate}.\n• Tiền cọc giữ chỗ: ${formatter.format(contract.depositAmount)}.\n• Khoản cọc được App giữ hộ và giải ngân cho Chủ nhà vào ngày bắt đầu thuê.",
                            fontSize = 13.sp, lineHeight = 20.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 3: ĐIỀU KHOẢN HỦY BỎ") {
                        Text(
                            text = "• Nếu Người thuê hủy: Mất 100% tiền cọc.\n• Nếu Chủ thuê hủy: Hoàn 100% cọc và phạt thêm 100% giá trị cọc cho Người thuê.",
                            fontSize = 13.sp, lineHeight = 20.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ContractSectionItem(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryMain)
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ContractScreenPreview() {
    val dummyContract = Contract(
        id = "1",
        roomId = "101",
        roomName = "Phòng 101 - Tòa nhà A",
        renterName = "Nguyễn Văn A",
        renterPhone = "0987654321",
        startDate = "01/10/2024",
        endDate = "01/10/2025",
        depositAmount = 2000000L,
        depositStatus = DepositStatus.UNPAID,
        dateCreated = "20/09/2024"
    )
    EzRoomTheme {
        ContractScreen(contract = dummyContract)
    }
}
