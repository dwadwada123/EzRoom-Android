package com.example.ezroom.ui.renter.review_report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

@Composable
fun WriteReviewScreen(
    onBackClick: () -> Unit = {},
    onSubmitReview: (Int, String) -> Unit = { _, _ -> },
) {
    // Mock Data for Room Summary
    val roomTitle = "Phòng trọ cao cấp Q7 - Full nội thất"
    val roomPrice = "3.500.000₫/tháng"
    val roomImageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400"

    // States
    var rating by remember { mutableIntStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Đánh Giá Phòng Trọ",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Room Summary Card
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

            // 2. Interactive Rating Bar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mức độ hài lòng của bạn?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val starPosition = index + 1
                        val isSelected = starPosition <= rating
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Rating Star $starPosition",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { rating = starPosition }
                        )
                    }
                }
            }

            // 3. Comment Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nhận xét của bạn",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Chia sẻ trải nghiệm thực tế của bạn về phòng trọ này, không gian sống và thái độ của chủ nhà...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    minLines = 4,
                    maxLines = 6,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Submit Button
            Button(
                onClick = { onSubmitReview(rating, commentText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = rating > 0,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "GỬI ĐÁNH GIÁ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WriteReviewScreenPreview() {
    EzRoomTheme {
        WriteReviewScreen()
    }
}
