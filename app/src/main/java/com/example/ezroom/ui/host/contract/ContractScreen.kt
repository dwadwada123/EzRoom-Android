package com.example.ezroom.ui.host.contract

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background // Đã thêm import này để hết lỗi 'background'
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractScreen(
    onNavigateBack: () -> Unit,
    onSignContract: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HỢP ĐỒNG ĐIỆN TỬ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = OrangePrimary)
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
                    // Quốc hiệu Tiêu ngữ
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

                    // Khung thông tin Bên A & Bên B
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
                                Text(text = "Thành viên thuê phòng trên ứng dụng", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = OnBackgroundLight)
                            }
                        }
                    }

                    HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.08f))

                    // Sửa đổi từ 'lineSpacing = 4.sp' sang 'lineHeight = 18.sp' để hết lỗi áp dụng candidates
                    ContractSectionItem(title = "ĐIỀU 1: THỜI HẠN THUÊ TRỌ") {
                        Text(
                            text = "• Thời hạn thuê là 12 tháng kể từ ngày kích hoạt hợp đồng.\n• Bên B phải cam kết ở tối thiểu đủ thời hạn nêu trên. Mọi trường hợp đơn phương chấm dứt hợp đồng sớm sẽ chịu mất toàn bộ số tiền đặt cọc.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 2: CHI PHÍ & CHI TIẾT ĐẶT CỌ") {
                        Text(
                            text = "• Số tiền đặt cọc giữ chỗ và bảo đảm tài sản cố định: 3.000.000 đ (Ba triệu đồng chẵn).\n• Số tiền này được hoàn trả 100% khi hết hạn hợp đồng và hoàn thành đầy đủ nghĩa vụ thanh toán.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }

                    ContractSectionItem(title = "ĐIỀU 3: CHÍNH SÁCH ĐỀN BÙ VÀ HƯ HỎNG") {
                        Text(
                            text = "• Bên B có trách nhiệm bảo quản các cơ sở vật chất bàn giao (Giường, tủ, máy lạnh, kệ bếp).\n• Nếu phát hiện hư hỏng do lỗi chủ quan cố ý hoặc bất cẩn, Bên B có nghĩa vụ đền bù 100% theo giá trị thị trường.\n• Khấu trừ trực tiếp vào tiền cọc nếu không hoàn trả chi phí sửa chữa phát sinh.",
                            fontSize = 13.sp, color = OnBackgroundLight, lineHeight = 18.sp
                        )
                    }
                }
            }

            // Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAgreed = !isAgreed }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            // Button Ký hợp đồng
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
    EzRoomTheme {
        ContractScreen(
            onNavigateBack = {},
            onSignContract = {}
        )
    }
}