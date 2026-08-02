package com.example.ezroom.ui.host.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.AccentAmber
import com.example.ezroom.ui.theme.ErrorRose
import com.example.ezroom.ui.theme.Neutral300

@Composable
fun RenterReviewDialog(
    renterName: String,
    isEditMode: Boolean = false,
    initialRating: Int = 5,
    initialComment: String = "",
    initialTags: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSubmit: (Int, List<String>, String) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var rating by remember { mutableIntStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }
    
    val tags = listOf("Thanh toán đúng hạn", "Giữ gìn vệ sinh", "Tuân thủ nội quy", "Lịch sự", "Ồn ào")
    val selectedTags = remember { mutableStateListOf<String>().apply { addAll(initialTags) } }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa đánh giá", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa đánh giá dành cho khách thuê $renterName?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                ) {
                    Text("Xác nhận xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy")
                }
            },
            containerColor = Color.White
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = if (isEditMode) "Chỉnh sửa đánh giá khách thuê" else "Đánh giá khách thuê", 
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Trải nghiệm của bạn với $renterName thế nào?")

                // Star Rating
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { index ->
                        val currentRating = index + 1
                        IconButton(
                            onClick = { rating = currentRating },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = if (currentRating <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (currentRating <= rating) AccentAmber else Neutral300,
                            )
                        }
                    }
                }

                // Tags
                Text("Chọn đặc điểm", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tags) { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag) },
                        )
                    }
                }

                // Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Nhận xét chi tiết") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    isError = (rating <= 3) && (comment.length < 20),
                )
                if ((rating <= 3) && (comment.length < 20)) {
                    Text(
                        "Vui lòng nhập ít nhất 20 ký tự khi đánh giá thấp",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, selectedTags.toList(), comment) },
                enabled = rating > 3 || comment.length >= 20,
            ) {
                Text(if (isEditMode) "CẬP NHẬT" else "Gửi đánh giá")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isEditMode && onDelete != null) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f))
                    ) {
                        Text("XÓA ĐÁNH GIÁ")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Hủy")
                }
            }
        },
        containerColor = Color.White
    )
}
