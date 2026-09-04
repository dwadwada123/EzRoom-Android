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
    val context = androidx.compose.ui.platform.LocalContext.current

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
                actions = {
                    IconButton(onClick = { com.example.ezroom.util.PdfExporter.exportContractPdf(context, contract) }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Xuất PDF Hợp đồng",
                            tint = PrimaryMain
                        )
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
            var isAgreed by remember { mutableStateOf(false) }

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
                                if (terminateReason.isNotBlank() && !isTerminating) {
                                    isTerminating = true
                                    scope.launch {
                                        val repo = ContractRepositoryImpl()
                                        repo.terminateContract(contract.id, terminateReason.ifBlank { "Chủ nhà chấm dứt hợp đồng sớm" }, "HOST")
                                        isTerminating = false
                                        showTerminateDialog = false
                                        onNavigateBack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                            enabled = terminateReason.isNotBlank() && !isTerminating
                        ) {
                            Text(if (isTerminating) "ĐANG XỬ LÝ..." else "XÁC NHẬN CHẤM DỨT", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTerminateDialog = false }, enabled = !isTerminating) {
                            Text("HỦY BỎ", color = Neutral500)
                        }
                    }
                )
            }

            // UI Component: Contextual Host Actions
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val isDraft = contract.status == ContractStatus.DRAFT || contract.id.isBlank()
                    when {
                        isDraft -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { if (!isSending) isAgreed = !isAgreed }
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isAgreed,
                                    onCheckedChange = { if (!isSending) isAgreed = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Tôi (Bên A - Chủ nhà) đã kiểm tra kỹ và đồng ý với toàn bộ điều khoản hợp đồng",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            PrimaryButton(
                                text = if (isSending) "ĐANG GỬI HỢP ĐỒNG..." else "KÝ & GỬI HỢP ĐỒNG CHO NGƯỜI THUÊ",
                                onClick = {
                                    scope.launch {
                                        isSending = true
                                        try {
                                            val isZeroDeposit = contract.depositAmount == 0L
                                            val currentHost = com.example.ezroom.util.TokenManager.getUser()
                                            val currentHostName = contract.hostName?.takeIf { it.isNotBlank() } ?: currentHost?.name ?: "Chủ nhà"
                                            val currentHostId = contract.hostId?.takeIf { it.isNotBlank() } ?: currentHost?.id ?: ""
                                            val finalContract = contract.copy(
                                                hostName = currentHostName,
                                                hostId = currentHostId,
                                                status = ContractStatus.WAITING_SIGN,
                                                depositStatus = if (isZeroDeposit) DepositStatus.FROZEN else contract.depositStatus
                                            )
                                            ContractRepositoryImpl().createContract(finalContract)
                                            isSending = false
                                            onSignContract(TransactionType.DEPOSIT)
                                        } catch (e: Exception) {
                                            isSending = false
                                            android.widget.Toast.makeText(context, "Lỗi gửi hợp đồng: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isAgreed && !isSending
                            )
                        }
                        contract.status == ContractStatus.WAITING_SIGN -> {
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
                        contract.status == ContractStatus.WAITING_DEPOSIT -> {
                            Surface(
                                color = PrimaryMain.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💳 Người thuê đã ký - Đang chờ thanh toán tiền cọc (${formatter.format(contract.depositAmount)})",
                                    color = PrimaryMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        contract.status == ContractStatus.ACTIVE -> {
                            OutlinedButton(
                                onClick = { showTerminateDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                                border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.3f))
                            ) {
                                Text("CHẤM DỨT HỢP ĐỒNG SỚM", fontWeight = FontWeight.Bold)
                            }
                        }
                        contract.status == ContractStatus.TERMINATED || contract.status == ContractStatus.CANCELLED -> {
                            Surface(
                                color = ErrorRose.copy(alpha = 0.08f),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            FintechStatusBanner(contract)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Neutral300)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        text = "(Số: ${contract.id.ifBlank { "HD-EZROOM-2026" }})",
                        fontSize = 11.sp,
                        color = Neutral500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Neutral100)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("CĂN CỨ PHÁP LÝ GIAO KẾT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "• Căn cứ Bộ luật Dân sự số 91/2015/QH13 ngày 24/11/2015;\n• Căn cứ Luật Giao dịch điện tử số 20/2023/QH15 ngày 22/06/2023;\n• Căn cứ Luật Nhà ở số 27/2023/QH15 ngày 27/11/2023;\n• Căn cứ Luật Kinh doanh bất động sản số 29/2023/QH15 ngày 28/11/2023;\n• Căn cứ Luật Thương mại số 36/2005/QH11 ngày 14/06/2005;\n• Căn cứ Nghị định số 52/2013/NĐ-CP & Nghị định số 85/2021/NĐ-CP về TMĐT;\n• Thông qua nền tảng công nghệ quản lý và thuê trọ trực tuyến EzRoom.",
                            fontSize = 12.sp, lineHeight = 18.sp, color = Neutral700
                        )
                    }

                    HorizontalDivider(color = Neutral100)

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("CÁC BÊN THAM GIA HỢP ĐỒNG", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)

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
                                Text("• Tên đơn vị: Nền tảng Công nghệ EzRoom Escrow", fontSize = 12.sp)
                                Text("• MST: 0123456789 | Đại diện: Trần Vũ Phong", fontSize = 11.sp, color = Neutral500)
                            }
                        }
                    }

                    HorizontalDivider(color = Neutral100)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 1: THÔNG TIN TÀI SẢN THUÊ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        val displayRoomName = contract.roomName.takeIf { !it.isNullOrBlank() } ?: "Phòng trọ"
                        val displayAddress = contract.address?.takeIf { it.isNotBlank() } ?: "Theo bài đăng phòng trọ"
                        Text(
                            text = "• Phòng trọ số: $displayRoomName\n• Địa chỉ: $displayAddress\n• Mục đích thuê: Dùng để ở, sinh hoạt văn minh\n• Thời hạn thuê: Từ ngày ${contract.startDate} đến ngày ${contract.endDate}",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 2: GIÁ THUÊ, TIỀN CỌC VÀ PHƯƠNG THỨC THANH TOÁN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "1. Tiền đặt cọc bảo hộ (Escrow): ${formatter.format(contract.depositAmount)}\n2. Cơ chế bảo vệ: Tiền cọc được phong tỏa an toàn trên EzRoom Escrow và giải ngân cho Chủ nhà khi hợp đồng có hiệu lực.\n3. Thanh toán tiền phòng: Vào ngày 05 hàng tháng qua hệ thống ứng dụng EzRoom.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 3: PHÍ DỊCH VỤ NỀN TẢNG", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "1. Hàng tháng Bên A phải thanh toán phí tiện ích cho Bên C với số tiền bằng 1.5% giá thuê phòng/tháng(đã bao gồm thuế GTGT).\n2. Mọi chi phí sẽ được khấu trừ tự động qua hệ thống khi thanh toán tiền phòng. Bên C có nghĩa vụ xuất hóa đơn điện tử hợp pháp theo quy định pháp luật.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 4: QUYỀN VÀ NGHĨA VỤ CỦA CÁC BÊN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "1. Quyền và nghĩa vụ Bên A:\n• Giao phòng và trang thiết bị đúng tình trạng đã thỏa thuận.\n• Đảm bảo quyền sử dụng riêng tư trọn vẹn của Bên B. Bên A không được tự ý vào phòng thuê khi chưa thông báo trước ít nhất 24 giờ và chưa được sự đồng ý của Bên B (trừ các trường hợp khẩn cấp nguy hiểm đến tính mạng/tài sản).\n• Chịu trách nhiệm làm thủ tục đăng ký tạm trú cho Bên B sau khi nhận đủ giấy tờ tùy thân.\n\n2. Quyền và nghĩa vụ Bên B:\n• Trả tiền thuê phòng và chi phí sinh hoạt đúng hạn.\n• Sử dụng phòng đúng mục đích, giữ gìn an ninh trật tự, vệ sinh chung và tuân thủ các quy định mà chủ trọ đã đưa ra (nếu có).\n• Không được tự ý sửa chữa kết cấu phòng hoặc cho thuê lại nếu chưa có sự đồng ý bằng văn bản của Bên A.\n\n3. Quyền và nghĩa vụ Bên C (EzRoom):\n• Cung cấp nền tảng ứng dụng vận hành ổn định để Bên A và Bên B tạo lập, ký kết hợp đồng và lưu trữ dữ liệu an toàn.\n• Đóng vai trò trung gian đối soát tài chính, bảo vệ khoản tiền cọc minh bạch qua EzRoom Escrow.\n• Hỗ trợ trích xuất dữ liệu khi có tranh chấp hoặc theo yêu cầu từ cơ quan Nhà nước có thẩm quyền.\n• Bên C hoạt động với tư cách là bên trung gian cung cấp nền tảng kết nối. Bên C không chịu trách nhiệm đối với quyền sở hữu hợp pháp của phòng trọ từ Bên A, chất lượng thực tế của phòng, cũng như các hành vi vi phạm nghĩa vụ thanh toán hoặc vi phạm pháp luật cá nhân phát sinh giữa Bên A và Bên B trong suốt thời gian thuê trọ.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 5: CHẤM DỨT HỢP ĐỒNG & GIẢI QUYẾT TRANH CHẤP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "1. Đơn phương chấm dứt: Bên muốn chấm dứt hợp đồng trước hạn phải thông báo trước ít nhất 30 ngày qua ứng dụng. Nếu Bên B đơn phương dọn đi trước thời hạn hợp đồng mà không thông báo trước đủ 30 ngày (hoất tự ý bỏ đi không bàn giao), Bên B sẽ bị mất 100% số tiền đặt cọc. Số tiền này sẽ thuộc về Bên A. Ngược lại Nếu Bên A đơn phương lấy lại phòng trước hạn mà không thông báo trước đủ 30 ngày hoặc vi phạm nghiêm trọng cam kết (như tăng giá sai thỏa thuận, cắt điện nước ép khách dọn đi), Bên A phải hoàn trả 100% tiền cọc, đồng thời phải bồi thường thêm cho Bên B một khoản tiền bằng đúng với số tiền đã đặt cọc.\n2. Giải quyết tranh chấp: Các bên ưu tiên thương lượng hòa bình. Trường hợp xuất hiện hành vi bạo lực hoặc vi phạm pháp luật, Bên C có quyền trích xuất hồ sơ và trình báo cơ quan Công an xử lý theo quy định.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ĐIỀU 6: CAM KẾT VÀ HIỆU LỰC HỢP ĐỒNG", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
                        Text(
                            text = "Các bên cam kết hành vi xác nhận thông qua tài khoản cá nhân trên ứng dụng EzRoom là ý chí tự nguyện, đích danh và có giá trị pháp lý ràng buộc tương đương với việc kí tên trực tiếp trên giấy. Các bên sẽ từ bỏ quyền khiếu nại và phủ nhận hiệu lực của Hợp đồng với lý do Hợp đồng được giao kết thông qua hình thức dữ liệu số này. Hợp đồng này có hiệu lực kể từ thời điểm Bên B thao tác xác nhận ký và hoàn tất đặt cọc thành công trên hệ thống EzRoom.",
                            fontSize = 12.sp, lineHeight = 19.sp
                        )
                    }

                    HorizontalDivider(color = Neutral100)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("XÁC NHẬN VÀ CHỮ KÝ ĐIỆN TỬ 3 BÊN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryMain)
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
        contract.status == ContractStatus.DRAFT || contract.id.isBlank() ->
            Triple(PrimaryMain, Icons.Default.Info, "Bản thảo hợp đồng - Vui lòng kiểm tra kỹ trước khi ký và gửi cho người thuê.")
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

@Preview(showBackground = true)
@Composable
fun HostContractScreenPreview() {
    val dummyContract = Contract(
        id = "1",
        roomId = "101",
        roomName = "Phòng 101 - Tòa nhà A",
        address = "123 Đường ABC, Quận 1, TP.HCM",
        renterName = "Nguyễn Văn A",
        renterPhone = "0987654321",
        hostName = "Lê Thị B",
        startDate = "01/10/2024",
        endDate = "01/10/2025",
        depositAmount = 2000000L,
        depositStatus = DepositStatus.FROZEN,
        status = ContractStatus.ACTIVE,
        dateCreated = "20/09/2024",
        disburseDate = "05/10/2024",
        isProtected = true
    )
    EzRoomTheme {
        HostContractScreen(
            contract = dummyContract,
            onNavigateBack = {},
            onSignContract = {}
        )
    }
}
