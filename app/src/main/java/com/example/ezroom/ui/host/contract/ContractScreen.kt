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
import androidx.compose.material.icons.filled.Description
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
import com.example.ezroom.data.model.Contract
import com.example.ezroom.data.model.DepositStatus
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractScreen(
    // Event callbacks
    contract: Contract,
    onNavigateBack: () -> Unit,
    onSignContract: () -> Unit
) {
    // State definitions
    var isAgreed by remember { mutableStateOf(false) }
    val formatter = remember { DecimalFormat("#,### VND") }

    // Main layout container
    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            // Top app bar
            TopAppBar(
                title = {
                    Text(
                        text = "Hợp đồng điện tử",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Legal document container
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = SurfaceLight),
                border = BorderStroke(1.dp, OnBackgroundLight.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header: Document title
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = OnBackgroundLight,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Độc lập - Tự do - Hạnh phúc",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = OnBackgroundLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(
                            modifier = Modifier.width(120.dp),
                            color = OnBackgroundLight.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "HỢP ĐỒNG THUÊ PHÒNG TRỌ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = OrangePrimary
                    )

                    HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.08f))

                    // Party information section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BackgroundLight.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = OrangePrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                    Text("BÊN A", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = OrangePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Hệ thống EzRoom (Đại diện chủ nhà)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = OnBackgroundLight)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = BackgroundLight.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = TealAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                    Text("BÊN B", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = contract.renterName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = OnBackgroundLight)
                                    Text(text = "SĐT: ${contract.renterPhone}", fontSize = 11.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.08f))

                    // Dynamic terms content
                    ContractSectionItem(title = "ĐIỀU 1: THỜI HẠN THUÊ TRỌ") {
                        Text(
                            text = "• Bên B thuê phòng: ${contract.roomName}\n• Thời hạn: Từ ngày ${contract.startDate} đến ngày ${contract.endDate}.\n• Bên B cam kết ở tối thiểu đủ thời hạn nêu trên. Mọi trường hợp đơn phương chấm dứt hợp đồng sớm sẽ chịu mất toàn bộ số tiền đặt cọc.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 2: CHI PHÍ & CHI TIẾT ĐẶT CỌ") {
                        val statusText = if (contract.depositStatus == DepositStatus.PAID) "Đã thanh toán" else "Chưa thanh toán"
                        Text(
                            text = "• Số tiền đặt cọc giữ chỗ: ${formatter.format(contract.depositAmount)}.\n• Trạng thái cọc: $statusText\n• Số tiền này được hoàn trả 100% khi kết thúc hợp đồng và hoàn thành đầy đủ nghĩa vụ thanh toán.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 3: CHÍNH SÁCH ĐỀN BÙ VÀ HƯ HỎNG") {
                        Text(
                            text = "• Bên B có trách nhiệm bảo quản tài sản bàn giao (Giường, tủ, máy lạnh, kệ bếp).\n• Nếu phát hiện hư hỏng do lỗi chủ quan, Bên B có nghĩa vụ đền bù 100% giá trị thị trường.\n• Chi phí sửa chữa sẽ được khấu trừ trực tiếp vào tiền cọc nếu không thanh toán.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }
                }
            }

            // Input fields group: Agreement checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAgreed = !isAgreed }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Agreement checkbox
                Checkbox(
                    checked = isAgreed,
                    onCheckedChange = { isAgreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = TealAccent)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tôi đã xác nhận thông tin chính xác và đồng ý ký kết",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnBackgroundLight
                )
            }

            // Action buttons row: Digital signature action
            Button(
                onClick = onSignContract,
                enabled = isAgreed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealAccent,
                    contentColor = OnPrimaryLight,
                    disabledContainerColor = OnBackgroundLight.copy(alpha = 0.12f),
                    disabledContentColor = OnBackgroundLight.copy(alpha = 0.38f)
                )
            ) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("XÁC NHẬN KÝ HỢP ĐỒNG ĐIỆN TỬ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ContractSectionItem(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .background(OrangeSecondary, shape = RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = OrangeSecondary
            )
        }
        Box(modifier = Modifier.padding(start = 9.dp)) {
            content()
        }
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
        depositStatus = DepositStatus.PAID
    )
    EzRoomTheme(darkTheme = false) {
        ContractScreen(
            contract = dummyContract,
            onNavigateBack = {},
            onSignContract = {}
        )
    }
}
