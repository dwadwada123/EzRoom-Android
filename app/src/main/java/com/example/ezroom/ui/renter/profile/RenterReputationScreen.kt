package com.example.ezroom.ui.renter.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.domain.model.RenterReview
import com.example.ezroom.domain.usecase.GetRenterReviewsUseCase
import com.example.ezroom.ui.components.RenterReviewItem
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterReputationScreen(
    renterId: String? = null,
    onBack: () -> Unit
) {
    var reviews by remember { mutableStateOf(emptyList<RenterReview>()) }
    val getReviews = remember { GetRenterReviewsUseCase() }
    val currentUser = remember { com.example.ezroom.util.TokenManager.getUser() }
    val currentUserId = currentUser?.id ?: ""

    val targetUserId = renterId ?: currentUserId
    val scope = rememberCoroutineScope()
    LaunchedEffect(targetUserId) {
        getReviews(targetUserId).collect { reviews = it }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, targetUserId) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    getReviews(targetUserId).collect { reviews = it }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val userScore = if (renterId == null || renterId == currentUserId) {
        currentUser?.creditScore?.toDouble() ?: 5.0
    } else {
        5.0
    }

    RenterReputationContent(
        reviews = reviews,
        userScore = userScore,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterReputationContent(
    reviews: List<RenterReview>,
    userScore: Double = 5.0,
    onBack: () -> Unit
) {
    val averageRating = if (reviews.isEmpty()) {
        if (userScore > 0.0) userScore else 5.0
    } else {
        reviews.map { it.rating }.average()
    }

    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "5 sao", "4 sao", "3 sao", "Dưới 2 sao")

    val filteredList = remember(selectedFilter, reviews) {
        when (selectedFilter) {
            "5 sao" -> reviews.filter { it.rating == 5 }
            "4 sao" -> reviews.filter { it.rating == 4 }
            "3 sao" -> reviews.filter { it.rating == 3 }
            "Dưới 2 sao" -> reviews.filter { it.rating <= 2 }
            else -> reviews
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lịch sử uy tín", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Neutral50
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Điểm trung bình", style = MaterialTheme.typography.labelMedium, color = Neutral500)
                        Text(
                            text = "%.1f".format(averageRating),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryMain
                        )
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star, null,
                                    tint = if (index < averageRating.toInt()) AccentAmber else Neutral300,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(text = "Dựa trên ${reviews.size} lượt đánh giá", style = MaterialTheme.typography.bodySmall, color = Neutral500)
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryMain,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item { EmptyStatePlaceholder() }
            } else {
                items(filteredList, key = { it.id }) { review ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        RenterReviewItem(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.RateReview, null, modifier = Modifier.size(64.dp), tint = Neutral300.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Không tìm thấy kết quả phù hợp", color = Neutral500)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun RenterReputationScreenPreview() {
    val sampleReviews = listOf(
        RenterReview(
            id = "1",
            hostName = "Nguyễn Văn A",
            rating = 5,
            comment = "Người thuê rất lịch sự, giữ gìn phòng sạch sẽ và thanh toán đúng hạn.",
            date = "20/10/2023",
            tags = listOf("Sạch sẽ", "Đúng hạn")
        ),
        RenterReview(
            id = "2",
            hostName = "Trần Thị B",
            rating = 4,
            comment = "Khá tốt, tuy nhiên đôi khi trả phòng hơi trễ một chút.",
            date = "15/09/2023",
            tags = listOf("Thân thiện")
        ),
        RenterReview(
            id = "3",
            hostName = "Lê Văn C",
            rating = 3,
            comment = "Thanh toán tiền điện nước thường xuyên bị nhắc nhở.",
            date = "10/08/2023",
            tags = listOf("Ồn ào")
        )
    )
    EzRoomTheme {
        RenterReputationContent(
            reviews = sampleReviews,
            onBack = {}
        )
    }
}

