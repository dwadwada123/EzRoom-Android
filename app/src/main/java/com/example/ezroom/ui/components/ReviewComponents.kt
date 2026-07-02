package com.example.ezroom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.RenterReview
import com.example.ezroom.ui.theme.*

/**
 * Simplified Renter Review Item - Removed Reply capability as per user request.
 * Focuses on displaying host feedback and reputation.
 */
@Composable
fun RenterReviewItem(
    review: RenterReview
) {
    var showReportDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = PrimaryLight) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = PrimaryMain)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Chủ nhà ${review.hostName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { showReportDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Flag, null, tint = Neutral300, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, null,
                            tint = if (index < review.rating) AccentAmber else Neutral300,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(text = review.date, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (review.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    review.tags.forEach { tag ->
                        Surface(color = PrimarySurface, shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.1f))) {
                            Text(tag, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = PrimaryMain, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Text(text = review.comment, style = MaterialTheme.typography.bodyMedium, color = Neutral700)
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Khiếu nại đánh giá", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn cho rằng đánh giá này không đúng sự thật hoặc có nội dung vi phạm? EzRoom sẽ tiến hành xác minh.") },
            confirmButton = { Button(onClick = { showReportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)) { Text("Khiếu nại") } },
            dismissButton = { TextButton(onClick = { showReportDialog = false }) { Text("Hủy") } }
        )
    }
}

