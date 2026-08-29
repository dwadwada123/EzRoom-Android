package com.example.ezroom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.EzRoomTheme

@Composable
fun ReviewReplyDialog(
    title: String,
    reviewerName: String,
    originalComment: String,
    initialText: String = "",
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var replyText by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Phản hồi đánh giá của $reviewerName",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "\"$originalComment\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Nhập phản hồi...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(replyText) },
                enabled = replyText.isNotBlank() && (replyText != initialText),
            ) {
                Text("Gửi phản hồi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun ReviewReplyDialogPreview() {
    EzRoomTheme {
        // Wrap in a Box to provide a container for the Dialog in preview
        Box(modifier = Modifier.fillMaxSize()) {
            ReviewReplyDialog(
                title = "Phản hồi đánh giá",
                reviewerName = "Nguyễn Văn A",
                originalComment = "Người thuê rất lịch sự, giữ gìn vệ sinh phòng ốc sạch sẽ và đóng tiền đúng hạn. Rất mong được tiếp tục cho bạn thuê.",
                onDismiss = {},
                onSubmit = {}
            )
        }
    }
}
