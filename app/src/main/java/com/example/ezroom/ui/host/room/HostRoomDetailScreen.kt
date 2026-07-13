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
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Initial room data
    val displayRoom = remember(room) { room ?: mockRoomWithRenters() }
    val reviews = remember { mutableStateListOf<RoomReview>().apply { addAll(displayRoom.reviews) } }

    var showRenterReview by remember { mutableStateOf(value = false) }
    var renterNameToReview by remember { mutableStateOf("") }

    // Filter & Reply State
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "5 sao", "4 sao", "3 sao", "Dưới 2 sao", "Chưa phản hồi")
    
    var showReplyDialog by remember { mutableStateOf(value = false) }
    var reviewToReply by remember { mutableStateOf<RoomReview?>(null) }
    var isEditingReply by remember { mutableStateOf(value = false) }
    
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
        com.example.ezroom.ui.host.components.RenterReviewDialog(
            renterName = renterNameToReview,
            onDismiss = { showRenterReview = false },
            onSubmit = { _, _, _ -> showRenterReview = false },
        )
    }

    Scaffold(
        bottomBar = {
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
                        Surface(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(56.dp), 
                            shape = CircleShape, 
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), 
                            contentColor = MaterialTheme.colorScheme.error
                        ) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Delete, contentDescription = "Xóa bài") }
                        }
                        
                        PrimaryButton(text = "CHỈNH SỬA", onClick = { onEditClick(displayRoom.id) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        containerColor = Neutral50,
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
            LazyColumn(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))) {
                        AsyncImage(
                            model = displayRoom.images.firstOrNull()?.resId,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        )
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
                            BentoStatCard(Icons.Default.Group, "Sức chứa", "2-3 người", Modifier.weight(1f))
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
                if (displayRoom.currentRenter != null || displayRoom.pastRenters.isNotEmpty()) {
                    item {
                        var isRentersExpanded by remember { mutableStateOf(value = false) }
                        
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
                                            onRateClick = {
                                                renterNameToReview = currentRenter.name
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
                                            RenterManagementCard(
                                                renter = renter,
                                                isCurrent = false,
                                                onRateClick = {
                                                    renterNameToReview = renter.name
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
                    containerColor = if (isCurrent) PrimaryMain else Neutral100,
                    contentColor = if (isCurrent) Color.White else Neutral700,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp),
            ) {
                Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Đánh giá", style = MaterialTheme.typography.labelSmall)
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
