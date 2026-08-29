package com.example.ezroom.ui.renter.review_report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Biến fallback giúp tránh lỗi Unresolved reference 'peaceSansFont'
private val peaceSansFont = FontFamily.Default

@Composable
fun SubmitReportScreen(
    roomTitle: String = "Phòng trọ cao cấp Quận 1",
    roomPrice: String = "Quận 1, TP. Hồ Chí Minh",
    roomImageUrl: String = "",
    onBackClick: () -> Unit = {},
    onSubmitReport: (reason: String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    // Danh sách lý do cố định
    val reasons = listOf(
        "Thông tin ảo",
        "Giá sai thực tế",
        "Lừa đảo tiền cọc",
        "Phòng đã cho thuê",
        "Lý do khác"
    )

    // States
    var selectedReason by remember { mutableStateOf("") }
    var detailedDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val isOtherSelected = selectedReason == "Lý do khác"
    val isSubmitEnabled = selectedReason.isNotEmpty() && (!isOtherSelected || detailedDescription.isNotBlank())

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ReportTopBar(onBackClick = onBackClick)
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
                // Thẻ thông tin phòng bị báo cáo
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Phòng trọ bị báo cáo",
                        fontFamily = peaceSansFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ReportedRoomCard(
                        roomTitle = roomTitle,
                        roomSubtitle = if (roomPrice.isNotBlank()) roomPrice else "Quận 1, TP. Hồ Chí Minh",
                        roomImageUrl = roomImageUrl
                    )
                }

                // Nhóm lựa chọn lý do vi phạm
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Chọn lý do vi phạm",
                        fontFamily = peaceSansFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.selectableGroup()) {
                        reasons.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .selectable(
                                        selected = (reason == selectedReason),
                                        onClick = { if (!isLoading) selectedReason = reason },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (reason == selectedReason),
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryMain,
                                        unselectedColor = Neutral300
                                    ),
                                    enabled = !isLoading
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reason,
                                    fontFamily = peaceSansFont,
                                    fontSize = 14.sp,
                                    color = Neutral900
                                )
                            }
                        }
                    }
                }

                // Ô nhập chi tiết vi phạm
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Chi tiết vi phạm",
                        fontFamily = peaceSansFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = detailedDescription,
                        onValueChange = { detailedDescription = it },
                        placeholder = {
                            Text(
                                text = if (isOtherSelected)
                                    "Vui lòng cung cấp thêm thông tin chi tiết..."
                                else "Mô tả thêm (không bắt buộc)...",
                                fontFamily = peaceSansFont,
                                fontSize = 14.sp,
                                color = Neutral500.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            disabledContainerColor = SurfaceCard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = peaceSansFont,
                            fontSize = 14.sp,
                            color = Neutral900
                        )
                    )
                    if (isOtherSelected && detailedDescription.isBlank()) {
                        Text(
                            text = "* Bắt buộc nhập chi tiết cho lý do khác",
                            color = ErrorRose,
                            fontFamily = peaceSansFont,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nút bấm gửi báo cáo
                Button(
                    onClick = {
                        if (isSubmitEnabled) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                val finalReason = if (isOtherSelected) "Lý do khác: $detailedDescription" else selectedReason
                                onSubmitReport(finalReason)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryMain,
                        disabledContainerColor = PrimaryMain.copy(alpha = 0.4f),
                        contentColor = White,
                        disabledContentColor = White.copy(alpha = 0.8f)
                    ),
                    enabled = isSubmitEnabled && !isLoading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "GỬI BÁO CÁO",
                        fontFamily = peaceSansFont,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

// ================= UI COMPONENTS PHỤ TÁCH RIÊNG =================

@Composable
private fun ReportTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Neutral900,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = "Báo cáo vi phạm",
            fontFamily = peaceSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Neutral900,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 48.dp)
        )
    }
}

@Composable
private fun ReportedRoomCard(
    roomTitle: String,
    roomSubtitle: String,
    roomImageUrl: String
) {
    Surface(
        color = White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = roomImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = roomTitle,
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = roomSubtitle,
                    fontFamily = peaceSansFont,
                    fontSize = 12.sp,
                    color = Neutral500
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubmitReportScreenPreview() {
    EzRoomTheme {
        SubmitReportScreen()
    }
}