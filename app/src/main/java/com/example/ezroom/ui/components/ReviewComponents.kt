package com.example.ezroom.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.RenterReview
import com.example.ezroom.ui.theme.*

@Composable
fun RenterReviewItem(
    review: RenterReview,
) {
    var showReportDialog by remember { mutableStateOf(value = false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = PrimarySurface,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = PrimaryMain)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = review.hostName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                
                IconButton(onClick = { showReportDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Flag, null, tint = Neutral300, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, null,
                            tint = if (index < review.rating) AccentAmber else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                Text(text = review.date, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }

            // Tags
            if (review.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    review.tags.forEach { tag ->
                        Surface(
                            color = PrimarySurface,
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryMain,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = review.comment, style = MaterialTheme.typography.bodyMedium, color = Neutral700)
            
            // Note: Renter reply removed per user request
        }
    }

    if (showReportDialog) {
        ReviewReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reason, photos ->
                // Simulation: Logic to send report with photos
                showReportDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewReportDialog(
    onDismiss: () -> Unit,
    onConfirm: (reason: String, photos: List<Uri>) -> Unit
) {
    val reasons = listOf(
        "Thông tin không chính xác",
        "Ngôn ngữ không phù hợp",
        "Spam / Quảng cáo",
        "Lừa đảo",
        "Khác"
    )
    var selectedReason by remember { mutableStateOf("") }
    var detailText by remember { mutableStateOf("") }
    val attachedPhotos = remember { mutableStateListOf<Uri>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Báo cáo đánh giá", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Vui lòng chọn lý do bạn muốn báo cáo đánh giá này:", style = MaterialTheme.typography.bodyMedium)
                
                // Reason Dropdown/Selection
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Chọn lý do") },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        enabled = false, // Use Box clickable
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.7f)) {
                        reasons.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(reason) },
                                onClick = {
                                    selectedReason = reason
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedReason == "Khác") {
                    OutlinedTextField(
                        value = detailText,
                        onValueChange = { detailText = it },
                        label = { Text("Chi tiết lý do") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                // Photos section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ảnh minh chứng (không bắt buộc)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { /* In real app, open image picker */ },
                            modifier = Modifier.size(60.dp),
                            shape = MaterialTheme.shapes.small,
                            color = Neutral100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AddAPhoto, null, tint = Neutral500)
                            }
                        }
                        
                        // Placeholder for attached photos
                        attachedPhotos.forEach { _ ->
                            Box(modifier = Modifier.size(60.dp).clip(MaterialTheme.shapes.small).background(Neutral100))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(if (selectedReason == "Khác") detailText else selectedReason, attachedPhotos.toList()) },
                enabled = selectedReason.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
            ) {
                Text("Gửi báo cáo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
        containerColor = Color.White
    )
}
