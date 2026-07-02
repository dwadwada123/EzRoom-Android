package com.example.ezroom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewReplyDialog(
    title: String = "Phản hồi đánh giá",
    reviewerName: String,
    originalComment: String,
    initialText: String = "",
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var replyText by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialText.isEmpty()) title else "Chỉnh sửa phản hồi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(color = Neutral50, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = reviewerName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Neutral500)
                        Text(text = originalComment, style = MaterialTheme.typography.bodySmall, color = Neutral700)
                    }
                }
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Nội dung phản hồi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(replyText) },
                enabled = replyText.isNotBlank() && replyText != initialText,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain)
            ) {
                Text(if (initialText.isEmpty()) "Gửi phản hồi" else "Cập nhật")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.medium
    )
}

