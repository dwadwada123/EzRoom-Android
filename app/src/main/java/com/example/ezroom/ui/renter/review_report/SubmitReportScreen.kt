package com.example.ezroom.ui.renter.review_report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SubmitReportScreen(
    onBackClick: () -> Unit = {},
    onSubmitReport: (reason: String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    // Mock Data for Room Summary
    val roomTitle = "Phòng trọ cao cấp Q7 - Full nội thất"
    val roomPrice = "3.500.000₫/tháng"
    val roomImageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400"

    // Predefined Reasons
    val reasons = listOf(
        "Thông tin phòng trọ ảo, sai sự thật",
        "Giá phòng không đúng với thực tế niêm yết",
        "Chủ nhà có dấu hiệu lừa đảo tiền cọc",
        "Phòng đã cho thuê nhưng không ẩn bài đăng",
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
                CommonTopAppBar(
                    title = "Báo cáo vi phạm",
                    onBackClick = onBackClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Room Summary Card
                Text(
                    text = "Phòng trọ bị báo cáo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                .size(80.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = roomTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = roomPrice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Select Reason Section
                Column(modifier = Modifier.selectableGroup()) {
                    Text(
                        text = "Chọn lý do vi phạm",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    reasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
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
                                    selectedColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // Detailed Description Box
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Chi tiết vi phạm",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = detailedDescription,
                        onValueChange = { detailedDescription = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = if (isOtherSelected) 
                                    "Vui lòng cung cấp thêm thông tin chi tiết..." 
                                else "Mô tả thêm (không bắt buộc)...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        minLines = 4,
                        maxLines = 6,
                        shape = MaterialTheme.shapes.medium,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isOtherSelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            focusedLabelColor = if (isOtherSelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    )
                    if (isOtherSelected && detailedDescription.isBlank()) {
                        Text(
                            text = "* Bắt buộc nhập chi tiết cho lý do khác",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action button
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
                    enabled = isSubmitEnabled && !isLoading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "GỬI BÁO CÁO",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        if (isLoading) {
            LoadingWidget()
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
