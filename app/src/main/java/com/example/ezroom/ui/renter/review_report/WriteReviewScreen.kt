package com.example.ezroom.ui.renter.review_report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun WriteReviewScreen(
    roomTitle: String = "Phòng trọ cao cấp Quận 1",
    roomPrice: String = "Quận 1, TP. Hồ Chí Minh",
    roomImageUrl: String = "",
    onBackClick: () -> Unit = {},
    onSubmitReview: (Int, String) -> Unit = { _, _ -> },
) {
    val scope = rememberCoroutineScope()

    // States
    var rating by remember { mutableIntStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isSubmitEnabled = rating > 0 && !isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ReviewTopBar(onBackClick = onBackClick)
            },
            containerColor = White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Thẻ thông tin phòng trọ
                RoomSummaryCard(
                    roomTitle = roomTitle,
                    roomSubtitle = if (roomPrice.isNotBlank()) roomPrice else "Quận 1, TP. Hồ Chí Minh",
                    roomImageUrl = roomImageUrl
                )

                // Phần chọn mức độ hài lòng (Đánh giá sao)
                RatingSection(
                    rating = rating,
                    isLoading = isLoading,
                    onRatingSelected = { rating = it }
                )

                // Phần nhập nhận xét
                CommentSection(
                    commentText = commentText,
                    onCommentChange = { commentText = it },
                    isLoading = isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                // Nút Gửi Đánh Giá
                Button(
                    onClick = {
                        if (isSubmitEnabled) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onSubmitReview(rating, commentText)
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
                    enabled = isSubmitEnabled,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "GỬI ĐÁNH GIÁ",
                        fontFamily = peaceSansFont,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

// ================= UI COMPONENTS TÁCH RIÊNG GỌN GÀNG =================

@Composable
private fun ReviewTopBar(onBackClick: () -> Unit) {
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
            text = "Đánh giá phòng trọ",
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
private fun RoomSummaryCard(
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
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = roomImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = roomTitle,
                    fontFamily = peaceSansFont,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Neutral500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = roomSubtitle,
                        fontFamily = peaceSansFont,
                        fontSize = 13.sp,
                        color = Neutral500
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingSection(
    rating: Int,
    isLoading: Boolean,
    onRatingSelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Mức độ hài lòng của bạn?",
            fontFamily = peaceSansFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                val starPosition = index + 1
                val isSelected = starPosition <= rating
                Icon(
                    imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Rating Star $starPosition",
                    tint = if (isSelected) AccentAmber else Neutral300,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isLoading) { onRatingSelected(starPosition) }
                )
            }
        }
    }
}

@Composable
private fun CommentSection(
    commentText: String,
    onCommentChange: (String) -> Unit,
    isLoading: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Nhận xét của bạn",
            fontFamily = peaceSansFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = commentText,
            onValueChange = onCommentChange,
            placeholder = {
                Text(
                    text = "Chia sẻ trải nghiệm thực tế của bạn...",
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = Neutral500.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
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
    }
}

@Preview(showBackground = true)
@Composable
fun WriteReviewScreenPreview() {
    EzRoomTheme {
        WriteReviewScreen()
    }
}