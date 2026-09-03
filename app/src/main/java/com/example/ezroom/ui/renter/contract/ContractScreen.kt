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
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetContractsUseCase
import com.example.ezroom.domain.usecase.SignContractUseCase
import com.example.ezroom.domain.usecase.GetRoomsUseCase
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
            val roomRepo = RoomRepositoryImpl()
            ContractViewModel(
                GetContractsUseCase(repository),
                SignContractUseCase(repository),
                repository,
                GetRoomsUseCase(roomRepo),
                isHost = false
            )
        }
    )
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAgreed by remember { mutableStateOf(false) }
    var isSigning by remember { mutableStateOf(false) }
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
                actions = {
                    IconButton(onClick = { com.example.ezroom.util.PdfExporter.exportContractPdf(context, contract) }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Xuất PDF Hợp đồng",
                            tint = PrimaryMain
                        )
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
                        ContractStatus.WAITING_SIGN, ContractStatus.DRAFT -> {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { if (!isSigning) isAgreed = !isAgreed }.padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = isAgreed, onCheckedChange = { if (!isSigning) isAgreed = it })
                                Text("Tôi đã đọc kỹ và đồng ý với toàn bộ điều khoản hợp đồng", style = MaterialTheme.typography.bodySmall)
                            }
                            PrimaryButton(
                                text = if (isSigning) "ĐANG XỬ LÝ..." else "KÝ HỢP ĐỒNG ĐIỆN TỬ",
                                onClick = { 
                                    if (isSigning) return@PrimaryButton
                                    isSigning = true
                                    viewModel.signContract(contract.id) {
                                        isSigning = false
                                        onSignSuccess()
                                    }
                                },
                                enabled = isAgreed && !isSigning,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nút mở/xuất PDF chuẩn A4
            OutlinedButton(
                onClick = { com.example.ezroom.util.PdfExporter.exportContractPdf(context, contract) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                border = BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("XEM BẢN IN PDF (CHUẨN KHỔ A4)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            if (contract.depositStatus == DepositStatus.FROZEN) {
                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val isArrived = try {
                    val date = contract.disburseDate?.let { sdf.parse(it) }
                    val today = sdf.parse(sdf.format(java.util.Date()))
                    date != null && today != null && !date.after(today)
                } catch (e: Exception) { true }

                val bannerText = if (isArrived) 
                    "Tiền cọc đã đủ điều kiện giải ngân và được chuyển cho Chủ nhà theo thời hạn hợp đồng."
                else 
                    "Tiền cọc của bạn đang được đóng băng an toàn tại EzRoom Escrow. Giải ngân vào: ${contract.disburseDate}"

                Surface(
                    color = SuccessEmerald.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = bannerText,
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
                    Text(
                        text = "(Mã hợp đồng: ${contract.id.ifBlank { "HD-EZROOM-2026" }})",
                        fontSize = 11.sp,
                        color = Neutral500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Neutral100)

                    // Căn cứ pháp lý
                    ContractSectionItem(title = "CĂN CỨ PHÁP LÝ GIAO KẾT") {
                        Text(
                            text = "• Căn cứ Bộ luật Dân sự số 91/2015/QH13;\n• Căn cứ Luật Giao dịch điện tử số 20/2023/QH15;\n• Căn cứ Luật Nhà ở số 27/2023/QH15 & Luật Kinh doanh BĐS số 29/2023/QH15;\n• Căn cứ Nghị định 52/2013/NĐ-CP & Nghị định 85/2021/NĐ-CP về TMĐT;\n• Thông qua nền tảng công nghệ quản lý và thuê trọ trực tuyến EzRoom.",
                            fontSize = 12.sp, lineHeight = 18.sp, color = Neutral700
                        )
                    }

                    HorizontalDivider(color = Neutral100)

                    // Thông tin 3 bên
                    ContractSectionItem(title = "CÁC BÊN THAM GIA HỢP ĐỒNG") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = Neutral50,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("BÊN CHO THUÊ (BÊN A):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                                    Text("• Họ và tên: ${contract.hostName ?: "Chủ nhà"}", fontSize = 12.sp)
                                    Text("• Tư cách: Chủ cơ sở lưu trú định danh trên EzRoom", fontSize = 11.sp, color = Neutral500)
                                }
                            }

                            Surface(
                                color = Neutral50,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("BÊN THUÊ (BÊN B):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                                    Text("• Họ và tên: ${contract.renterName}", fontSize = 12.sp)
                                    Text("• Số điện thoại định danh: ${contract.renterPhone}", fontSize = 12.sp)
                                    Text("• Nơi ĐKTT: (Kê khai theo hồ sơ thủ tục tạm trú khi nhận phòng)", fontSize = 11.sp, color = Neutral500)
                                }
                            }

                            Surface(
                                color = Neutral50,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("BÊN TRUNG GIAN NỀN TẢNG (BÊN C):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                                    Text("• Tên đơn vị: Nền tảng Công nghệ EzRoom", fontSize = 12.sp)
                                    Text("• MST: 0123456789 | Đại diện: Trần Vũ Phong", fontSize = 11.sp, color = Neutral500)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Neutral100)

                    // Điều 1
                    ContractSectionItem(title = "ĐIỀU 1: THÔNG TIN TÀI SẢN THUÊ") {
                        val displayRoomName = contract.roomName.takeIf { !it.isNullOrBlank() } ?: "Phòng trọ"
                        val displayAddress = contract.address?.takeIf { it.isNotBlank() } ?: "Theo bài đăng phòng trọ"
                        Text(
                            text = "• Phòng trọ: $displayRoomName\n• Địa chỉ: $displayAddress\n• Mục đích thuê: Dùng để ở, sinh hoạt văn minh\n• Thời hạn thuê: Từ ngày ${contract.startDate} đến ngày ${contract.endDate}",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    // Điều 2
                    ContractSectionItem(title = "ĐIỀU 2: GIÁ THUÊ & TIỀN CỌC KÝ QUỸ") {
                        Text(
                            text = "• Tiền đặt cọc bảo hộ (Escrow): ${formatter.format(contract.depositAmount)}\n• Cơ chế bảo vệ: Tiền cọc được phong tỏa an toàn trên EzRoom Escrow và giải ngân cho Chủ nhà khi hợp đồng có hiệu lực.\n• Thanh toán tiền phòng: Vào ngày 05 hàng tháng qua hệ thống ứng dụng EzRoom.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    // Điều 3
                    ContractSectionItem(title = "ĐIỀU 3: PHÍ DỊCH VỤ NỀN TẢNG") {
                        Text(
                            text = "• Phí kết nối giao dịch thành công: 5% theo quy chế hoạt động nền tảng EzRoom.\n• Khấu trừ tự động qua hệ thống khi thanh toán tiền phòng. Bên C có nghĩa vụ xuất hóa đơn điện tử hợp pháp theo quy định pháp luật.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    // Điều 4
                    ContractSectionItem(title = "ĐIỀU 4: QUYỀN VÀ NGHĨA VỤ CỦA CÁC BÊN") {
                        Text(
                            text = "1. Quyền và nghĩa vụ Bên A:\n• Giao phòng và tài sản đúng tình trạng thỏa thuận.\n• Tôn trọng quyền riêng tư của Bên B: Tuyệt đối KHÔNG tự ý vào phòng thuê khi chưa báo trước ít nhất 24 giờ và chưa được Bên B đồng ý (trừ trường hợp khẩn cấp như hỏa hoạn, sự cố nguy hiểm).\n• Có trách nhiệm hỗ trợ làm thủ tục đăng ký tạm trú cho Bên B.\n\n2. Quyền và nghĩa vụ Bên B:\n• Trả tiền phòng và chi phí sinh hoạt đúng thời hạn.\n• Sử dụng đúng mục đích, giữ gìn trật tự và vệ sinh chung; không tự ý sửa đổi kết cấu khi chưa được Bên A đồng ý.\n\n3. Quyền và nghĩa vụ Bên C (EzRoom):\n• Đảm bảo vận hành nền tảng, bảo mật dữ liệu hợp đồng điện tử.\n• Giữ vai trò trung gian đối soát và bảo đảm tiền cọc minh bạch.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    // Điều 5
                    ContractSectionItem(title = "ĐIỀU 5: CHẤM DỨT HỢP ĐỒNG & GIẢI QUYẾT TRANH CHẤP") {
                        Text(
                            text = "• Đơn phương chấm dứt: Báo trước ít nhất 30 ngày qua ứng dụng. Xử lý vi phạm theo quy định đặt cọc bảo hộ EzRoom Escrow.\n• Giải quyết tranh chấp: Ưu tiên thương lượng hòa bình thông qua hỗ trợ đối soát dữ liệu từ Bên C. Trường hợp vi phạm pháp luật sẽ trình báo cơ quan Công an có thẩm quyền.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    HorizontalDivider(color = Neutral100)

                    // Khung chữ ký / xác nhận điện tử
                    ContractSectionItem(title = "XÁC NHẬN VÀ CHỮ KÝ ĐIỆN TỬ") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = PrimarySurface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("BÊN CHO THUÊ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryMain)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("ĐÃ PHÁT HÀNH", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SuccessEmerald)
                                    Text(contract.hostName ?: "Chủ nhà", fontSize = 10.sp, maxLines = 1)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                color = if (contract.dateSigned != null || contract.status != ContractStatus.WAITING_SIGN) SuccessEmerald.copy(alpha = 0.1f) else Neutral100,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (contract.dateSigned != null || contract.status != ContractStatus.WAITING_SIGN) SuccessEmerald.copy(alpha = 0.3f) else Neutral300)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("BÊN THUÊ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Neutral700)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (contract.dateSigned != null || contract.status != ContractStatus.WAITING_SIGN) {
                                        Text("✅ ĐÃ KÝ ĐIỆN TỬ", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SuccessEmerald)
                                        Text(contract.renterName, fontSize = 10.sp, maxLines = 1)
                                        Text(contract.dateSigned ?: "Đã xác nhận", fontSize = 9.sp, color = Neutral500)
                                    } else {
                                        Text("Chờ người thuê ký", fontSize = 10.sp, color = Neutral500)
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                color = PrimarySurface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TRUNG GIAN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryMain)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("EZROOM CHỨNG THỰC", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = PrimaryMain)
                                    Text("Trần Vũ Phong", fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
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
