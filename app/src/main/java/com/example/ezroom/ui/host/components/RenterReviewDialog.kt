package com.example.ezroom.ui.host.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterReviewDialog(
    renterName: String,
    onDismiss: () -> Unit,
    onSubmit: (Int, List<String>, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val availableTags = listOf("Thanh toán đúng hạn", "Giữ gìn vệ sinh", "Tuân thủ nội quy", "Thân thiện", "Giao tiếp tốt")
    val selectedTags = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Đánh giá người thuê",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Hãy chia sẻ trải nghiệm của bạn với khách thuê $renterName để giúp cộng đồng EzRoom tin cậy hơn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral700
                )

                // Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val pos = index + 1
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (pos <= rating) AccentAmber else Neutral300,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = pos }
                        )
                    }
                }

                // Tags Selection (Simplified Grid without FlowRow)
                Text(
                    text = "Đặc điểm nổi bật",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTags.chunked(2).forEach { rowTags ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                    },
                                    label = { Text(tag) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryMain,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowTags.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(if (rating <= 3) "Lý do cụ thể (Bắt buộc khi < 4 sao)" else "Nhận xét thêm (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = MaterialTheme.shapes.medium,
                    isError = rating <= 3 && comment.length < 20
                )
                if (rating <= 3 && comment.length < 20) {
                    Text("Vui lòng nhập ít nhất 20 ký tự để minh bạch lý do.", color = ErrorRose, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, selectedTags.toList(), comment) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain),
                enabled = rating > 3 || comment.length >= 20
            ) {
                Text("Gửi đánh giá")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.medium
    )
}

