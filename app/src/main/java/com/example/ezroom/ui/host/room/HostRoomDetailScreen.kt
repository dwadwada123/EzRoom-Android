package com.example.ezroom.ui.host.room

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezroom.domain.model.*
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.ReviewReplyDialog
import com.example.ezroom.ui.components.UtilityPriceItem
import com.example.ezroom.ui.renter.discovery.BentoAmenityBadge
import com.example.ezroom.ui.renter.discovery.BentoStatCard
import com.example.ezroom.ui.renter.discovery.ReviewCard
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostRoomDetailScreen(
    modifier: Modifier = Modifier,
    room: Room? = null,
    onBackClick: () -> Unit = {},
    onEditClick: (Room) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Initial room data
    val displayRoom = remember(room) { room ?: mockRoomWithRenters() }
    val reviews = remember { mutableStateListOf<RoomReview>() }

    // Fetch room reviews left by renters
    LaunchedEffect(displayRoom.id) {
        reviews.clear()
        reviews.addAll(displayRoom.reviews)
        try {
            val roomReviewApi = com.example.ezroom.data.remote.RoomReviewApi.create()
            val remoteReviews: List<com.example.ezroom.data.remote.RoomReviewResponse> = roomReviewApi.getRoomReviews(displayRoom.id)
            if (remoteReviews.isNotEmpty()) {
                val mapped = remoteReviews.map { res: com.example.ezroom.data.remote.RoomReviewResponse ->
                    RoomReview(
                        id = res.id,
                        userName = res.reviewerName,
                        rating = res.rating,
                        comment = res.comment,
                        date = res.createdAt.take(10)
                    )
                }
                reviews.clear()
                reviews.addAll(mapped)
            }
        } catch (e: Exception) {
            android.util.Log.e("HostRoomDetail", "Failed to fetch room reviews", e)
        }
    }

    var showRenterReview by remember { mutableStateOf(false) }
    var renterNameToReview by remember { mutableStateOf("") }
    var renterIdToReview by remember { mutableStateOf("") }
    var renterPhoneToReview by remember { mutableStateOf("") }
    var existingReviewToEdit by remember { mutableStateOf<com.example.ezroom.domain.model.RenterReview?>(null) }
    
    // Map of renterId/renterPhone -> RenterReview
    var existingReviewsMap by remember { mutableStateOf<Map<String, com.example.ezroom.domain.model.RenterReview>>(emptyMap()) }

    // Fetch existing renter reviews from MongoDB Backend
    LaunchedEffect(displayRoom.currentRenter, displayRoom.pastRenters) {
        val allRenters = listOfNotNull(displayRoom.currentRenter) + displayRoom.pastRenters
        val newMap = mutableMapOf<String, com.example.ezroom.domain.model.RenterReview>()
        
        allRenters.forEach { renter ->
            val keysToTry = listOfNotNull(renter.phone.takeIf { it.isNotBlank() }, renter.id.takeIf { it.isNotBlank() })
            for (key in keysToTry) {
                try {
                    val api = com.example.ezroom.data.remote.RenterReviewApi.create()
                    val list = api.getRenterReviews(key)
                    if (list.isNotEmpty()) {
                        val currentHostName = com.example.ezroom.util.TokenManager.getUser()?.name ?: "Chủ nhà"
                        val match = list.find { it.hostName == currentHostName || it.renterId == renter.id || it.renterId == renter.phone } ?: list.firstOrNull()
                        if (match != null) {
                            newMap[renter.phone.ifBlank { renter.id }] = match
                            newMap[renter.id] = match
                            if (renter.phone.isNotBlank()) newMap[renter.phone] = match
                            break
                        }
                    }
                } catch (e: Exception) { /* fallback */ }
            }
        }
        existingReviewsMap = newMap
    }

    // Filter & Reply State
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "5 sao", "4 sao", "3 sao", "Dưới 2 sao", "Chưa phản hồi")
    
    var showReplyDialog by remember { mutableStateOf(false) }
    var reviewToReply by remember { mutableStateOf<RoomReview?>(null) }
    var isEditingReply by remember { mutableStateOf(false) }
    
    val filteredReviews = remember(selectedFilter, reviews.toList()) {
        when (selectedFilter) {
            "5 sao" -> reviews.filter { it.rating == 5 }
            "4 sao" -> reviews.filter { it.rating == 4 }
            "3 sao" -> reviews.filter { it.rating == 3 }
            "Dưới 2 sao" -> reviews.filter { it.rating <= 2 }
            "Chưa phản hồi" -> reviews.filter { it.hostReply == null }
            else -> reviews
        }
    }

    val visibleState = remember { MutableTransitionState(false) }.apply { targetState = true }

    if (showRenterReview) {
        val currentHostName = com.example.ezroom.util.TokenManager.getUser()?.name ?: "Chủ nhà"
        val reviewToEdit = existingReviewToEdit

        com.example.ezroom.ui.host.components.RenterReviewDialog(
            renterName = renterNameToReview,
            isEditMode = reviewToEdit != null,
            initialRating = reviewToEdit?.rating ?: 5,
            initialComment = reviewToEdit?.comment ?: "",
            initialTags = reviewToEdit?.tags ?: emptyList(),
            onDismiss = { showRenterReview = false },
            onSubmit = { ratingScore, selectedTags, commentText ->
                scope.launch {
                    try {
                        val api = com.example.ezroom.data.remote.RenterReviewApi.create()
                        val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                        
                        if (reviewToEdit != null) {
                            // Update existing review in MongoDB
                            val updated = reviewToEdit.copy(
                                rating = ratingScore,
                                tags = selectedTags,
                                comment = commentText,
                                date = currentDate
                            )
                            api.updateRenterReview(updated.id, updated)
                            existingReviewsMap = existingReviewsMap.toMutableMap().apply {
                                if (renterPhoneToReview.isNotBlank()) put(renterPhoneToReview, updated)
                                if (renterIdToReview.isNotBlank()) put(renterIdToReview, updated)
                            }
                        } else {
                            // Create new review in MongoDB
                            val targetRenterId = renterPhoneToReview.ifBlank { renterIdToReview }
                            val newReview = com.example.ezroom.domain.model.RenterReview(
                                id = java.util.UUID.randomUUID().toString(),
                                renterId = targetRenterId,
                                hostName = currentHostName,
                                rating = ratingScore,
                                tags = selectedTags,
                                comment = commentText,
                                date = currentDate
                            )
                            val res = api.createRenterReview(newReview)
                            android.util.Log.d("HostRoomDetail", "Create review res: $res")
                            existingReviewsMap = existingReviewsMap.toMutableMap().apply {
                                if (renterPhoneToReview.isNotBlank()) put(renterPhoneToReview, newReview)
                                if (renterIdToReview.isNotBlank()) put(renterIdToReview, newReview)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("HostRoomDetail", "Error saving renter review", e)
                    }
                    showRenterReview = false
                }
            },
            onDelete = {
                scope.launch {
                    try {
                        if (reviewToEdit != null) {
                            val api = com.example.ezroom.data.remote.RenterReviewApi.create()
                            api.deleteRenterReview(reviewToEdit.id)
                            existingReviewsMap = existingReviewsMap.toMutableMap().apply {
                                if (renterPhoneToReview.isNotBlank()) remove(renterPhoneToReview)
                                if (renterIdToReview.isNotBlank()) remove(renterIdToReview)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("HostRoomDetail", "Error deleting review", e)
                    }
                    showRenterReview = false
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Xóa phòng trọ?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Bạn có chắc muốn xóa phòng \"${displayRoom.title}\"? Hành động này không thể hoàn tác.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteClick(displayRoom.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            if (displayRoom.status != com.example.ezroom.domain.model.RoomStatus.REMOVED) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                 ) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp).shadow(24.dp, shape = CircleShape),
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                        tonalElevation = 8.dp,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Only show Delete button for non-rented rooms
                            if (displayRoom.status != com.example.ezroom.domain.model.RoomStatus.RENTED) {
                                Surface(
                                    onClick = { showDeleteConfirm = true },
                                    modifier = Modifier.size(56.dp), 
                                    shape = CircleShape, 
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), 
                                    contentColor = MaterialTheme.colorScheme.error
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Delete, contentDescription = "Xóa bài") }
                                }
                            }
                            
                            PrimaryButton(text = "CHỈNH SỬA", onClick = { onEditClick(displayRoom) }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        containerColor = Neutral50,
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
            LazyColumn(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                if (displayRoom.status == com.example.ezroom.domain.model.RoomStatus.REMOVED) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 60.dp),
                            color = ErrorRose.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRose)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Phòng trọ này đã bị Admin gỡ bỏ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ErrorRose
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val reasonText = displayRoom.removalInfo?.reason ?: "Vi phạm tiêu chuẩn cộng đồng"
                                val dateText = displayRoom.removalInfo?.removedDate?.takeIf { it.isNotBlank() } ?: "Gần đây"
                                Text(text = "Lý do: $reasonText", style = MaterialTheme.typography.bodyMedium, color = Neutral900)
                                Text(text = "Thời gian gỡ: $dateText", style = MaterialTheme.typography.bodySmall, color = Neutral500)
                            }
                        }
                    }
                }

                item {
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { displayRoom.images.size.coerceAtLeast(1) })
                    val coroutineScope = rememberCoroutineScope()

                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))) {
                        androidx.compose.foundation.pager.HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val image = displayRoom.images.getOrNull(page)
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = image?.url?.takeIf { it.isNotBlank() } ?: image?.resId,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                )
                                
                                // Tag Badge
                                if (!image?.category.isNullOrBlank()) {
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        contentColor = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = image?.category ?: "",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
                            color = if (displayRoom.status == RoomStatus.ACTIVE) SuccessEmerald else AccentAmber,
                            contentColor = Color.White,
                            shape = CircleShape,
                        ) {
                            Text(
                                text = displayRoom.status.title,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }

                        // Navigation Arrows
                        if (displayRoom.images.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    onClick = { 
                                        coroutineScope.launch {
                                            if (pagerState.currentPage > 0) {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", modifier = Modifier.padding(8.dp))
                                }
                                
                                Surface(
                                    onClick = { 
                                        coroutineScope.launch {
                                            if (pagerState.currentPage < displayRoom.images.size - 1) {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.padding(8.dp))
                                }
                            }
                            
                            // Page indicator
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1}/${displayRoom.images.size}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(text = displayRoom.title, style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = displayRoom.priceFormatted, style = MaterialTheme.typography.titleLarge, color = PrimaryMain, fontWeight = FontWeight.ExtraBold)
                            Surface(color = AccentAmber.copy(alpha = 0.1f), contentColor = AccentAmber, shape = MaterialTheme.shapes.small) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = displayRoom.rating.toString(), fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            BentoStatCard(Icons.Default.SquareFoot, "Tổng diện tích", "${displayRoom.floorArea} m²", Modifier.weight(1f))
                            BentoStatCard(Icons.Default.Group, "Sức chứa", if (displayRoom.capacity > 0) "${displayRoom.capacity} người" else "Không rõ", Modifier.weight(1f))
                        }
                        if (displayRoom.detailedAreas.isNotEmpty()) {
                            Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Chi tiết diện tích", style = MaterialTheme.typography.labelMedium, color = Neutral500, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    displayRoom.detailedAreas.forEach { area ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(area.roomName, style = MaterialTheme.typography.bodyMedium)
                                            Text("${area.areaValue} m²", fontWeight = FontWeight.Bold, color = PrimaryMain)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Renters Management
                if (displayRoom.currentRenter != null || displayRoom.pastRenters.isNotEmpty() || displayRoom.status == RoomStatus.RENTED) {
                    item {
                        var isRentersExpanded by remember { mutableStateOf(true) }
                        
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Surface(
                                onClick = { isRentersExpanded = !isRentersExpanded },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100),
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Group, null, tint = PrimaryMain)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Quản lý khách thuê",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isRentersExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Neutral300,
                                    )
                                }
                            }
                            
                            AnimatedVisibility(visible = isRentersExpanded) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    // Current Renter
                                    val currentRenter = displayRoom.currentRenter
                                    if (currentRenter != null) {
                                        val existingCurrent = existingReviewsMap[currentRenter.phone] ?: existingReviewsMap[currentRenter.id]
                                        val hasReviewedCurrent = existingCurrent != null

                                        Text(
                                            text = "Đang ở",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = SuccessEmerald,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 8.dp),
                                        )
                                        RenterManagementCard(
                                            renter = currentRenter,
                                            isCurrent = true,
                                            hasReviewed = hasReviewedCurrent,
                                            onRateClick = {
                                                renterNameToReview = currentRenter.name
                                                renterIdToReview = currentRenter.id
                                                renterPhoneToReview = currentRenter.phone
                                                existingReviewToEdit = existingCurrent
                                                showRenterReview = true
                                            },
                                        )
                                    }
                                    
                                    // Past Renters
                                    if (displayRoom.pastRenters.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Lịch sử khách cũ",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Neutral500,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 8.dp),
                                        )
                                        displayRoom.pastRenters.forEach { renter ->
                                            val existingPast = existingReviewsMap[renter.phone] ?: existingReviewsMap[renter.id]
                                            val hasReviewedPast = existingPast != null

                                            RenterManagementCard(
                                                renter = renter,
                                                isCurrent = false,
                                                hasReviewed = hasReviewedPast,
                                                onRateClick = {
                                                    renterNameToReview = renter.name
                                                    renterIdToReview = renter.id
                                                    renterPhoneToReview = renter.phone
                                                    existingReviewToEdit = existingPast
                                                    showRenterReview = true
                                                },
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Reviews Section
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text("Đánh giá từ khách thuê", style = MaterialTheme.typography.titleLarge)
                        
                        LazyRow(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filters) { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryMain, selectedLabelColor = Color.White),
                                )
                            }
                        }

                        if (filteredReviews.isEmpty()) {
                            Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = Neutral50) {
                                Text("Không có đánh giá phù hợp.", modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center, color = Neutral500)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                filteredReviews.forEach { review ->
                                    ReviewCard(
                                        review = review,
                                        canReply = true,
                                        onReply = {
                                            reviewToReply = review
                                            isEditingReply = false
                                            showReplyDialog = true
                                        },
                                        onEditReply = {
                                            reviewToReply = review
                                            isEditingReply = true
                                            showReplyDialog = true
                                        },
                                        onDeleteReply = {
                                            val index = reviews.indexOfFirst { it.id == review.id }
                                            if (index != -1) { reviews[index] = reviews[index].copy(hostReply = null) }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            IconButton(
                onClick = onBackClick, 
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 24.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        }

        if (showReplyDialog && reviewToReply != null) {
            ReviewReplyDialog(
                title = "Phản hồi khách thuê",
                reviewerName = reviewToReply!!.userName,
                originalComment = reviewToReply!!.comment,
                initialText = if (isEditingReply) reviewToReply!!.hostReply ?: "" else "",
                onDismiss = { showReplyDialog = false },
                onSubmit = { text ->
                    val index = reviews.indexOfFirst { it.id == reviewToReply!!.id }
                    if (index != -1) { reviews[index] = reviews[index].copy(hostReply = text) }
                    showReplyDialog = false
                },
            )
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Xác nhận xóa phòng", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn xóa phòng '${displayRoom.title}'? Mọi dữ liệu về lịch sử thuê và bài đăng sẽ bị mất hoàn toàn.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirm = false
                            onDeleteClick(displayRoom.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Xác nhận xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Giữ lại phòng")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun RenterManagementCard(
    renter: RenterInfo,
    isCurrent: Boolean,
    hasReviewed: Boolean = false,
    onRateClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isCurrent) PrimarySurface else Neutral100,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isCurrent) PrimaryMain else Neutral500,
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = renter.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = renter.stayPeriod,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500,
                )
            }
            
            Button(
                onClick = onRateClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasReviewed) AccentAmber.copy(alpha = 0.15f) else (if (isCurrent) PrimaryMain else Neutral100),
                    contentColor = if (hasReviewed) AccentAmber else (if (isCurrent) Color.White else Neutral700),
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp),
            ) {
                Icon(
                    imageVector = if (hasReviewed) Icons.Default.Edit else Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (hasReviewed) "Sửa đánh giá" else "Đánh giá",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (hasReviewed) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

private fun mockRoomWithRenters() = Room(
    id = "1", title = "Phòng trọ cao cấp Quận 7", price = 3500000L, priceFormatted = "3.500.000 ₫",
    address = "Quận 7, TP.HCM", detailedAddress = "123 Nguyễn Huệ, Quận 7, TP.HCM", description = "Mô tả chi tiết phòng trọ.",
    structure = RoomStructure.APARTMENT, floorArea = 25.0, mezzanineArea = 8.0, detailedAreas = emptyList(),
    rating = 4.8f, reviewCount = 2, reviews = listOf(
        RoomReview("r1", "Nguyễn Văn A", null, 5, "Phòng sạch sẽ.", "12/06/2026"),
        RoomReview("r2", "Lê Thị B", null, 4, "Vị trí tốt.", "10/06/2026")
    ), images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
    amenities = listOf(Amenity("WiFi"), Amenity("Điều hòa")), latitude = 10.762622, longitude = 106.660172,
    currentRenter = RenterInfo("u1", "Nguyễn Văn A", "0901234567", null, "01/2024 - Hiện tại", true),
    pastRenters = listOf(
        RenterInfo("u2", "Trần Thị B", "0907654321", null, "01/2023 - 12/2023", false)
    )
)

@Preview(showBackground = true)
@Composable
fun HostRoomDetailScreenPreview() { EzRoomTheme { HostRoomDetailScreen() } }
