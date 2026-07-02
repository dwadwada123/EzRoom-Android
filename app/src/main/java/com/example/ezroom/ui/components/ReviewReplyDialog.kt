package com.example.ezroom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
