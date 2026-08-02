package com.example.ezroom.ui.renter.discovery

import android.content.Intent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.ezroom.domain.model.*
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.PrimaryButton
import kotlinx.coroutines.launch
import com.example.ezroom.ui.components.SecondaryButton
import com.example.ezroom.ui.components.UtilityPriceItem
import com.example.ezroom.ui.navigation.LocalSnackbarProvider
import com.example.ezroom.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * EzRoom Room Detail Screen
 * Features: Bento Grid layout, Glassmorphism, and Staggered entry animations.
 */
// UI Component: Room Detail for Renter
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RenterRoomDetailScreen(
    modifier: Modifier = Modifier,
    room: Room? = null,
    onBackClick: () -> Unit = {},
    onBookAppointment: (String) -> Unit = {},
    onNavigateToChat: (hostId: String, hostName: String, hostPhone: String) -> Unit = { _, _, _ -> },
    onNavigateToReport: (roomId: String) -> Unit = {},
    onNavigateToWriteReview: (roomId: String) -> Unit = {},
) {
    // UI Logic: Context and Snackbar
    val context = LocalContext.current
    val showSnackbar = LocalSnackbarProvider.current
    val scope = rememberCoroutineScope()
    
    val repo = remember { com.example.ezroom.data.repository.RoomRepositoryImpl() }
    
    // State Management: Current room and property context
    var currentRoom by remember(room) { mutableStateOf(room ?: mockRoomData()) }
    var property by remember { mutableStateOf<com.example.ezroom.domain.model.Property?>(null) }
    var roomsInProperty by remember { mutableStateOf<List<Room>>(emptyList()) }

    LaunchedEffect(currentRoom.propertyId) {
        val pid = currentRoom.propertyId
        if (pid != null) {
            property = repo.getPropertyById(pid)
            val fetchedRooms = repo.getRoomsByPropertyId(pid)
            roomsInProperty = fetchedRooms
            
            if (currentRoom.status == RoomStatus.RENTED) {
                val alternative = fetchedRooms.find { it.status == RoomStatus.ACTIVE && !it.isUserHidden }
                if (alternative != null) {
                    currentRoom = alternative
                }
            }
        } else {
            property = null
            roomsInProperty = emptyList()
        }
    }

    // State Management: Interaction states
    var user by remember { mutableStateOf(com.example.ezroom.util.TokenManager.getUser()) }
    var isFavorite by remember(currentRoom.id) { mutableStateOf(currentRoom.id in (user?.favoriteRoomIds ?: emptyList())) }
    var showUnfavoriteDialog by remember { mutableStateOf(false) }
    
    // Reviews state
    var fetchedReviews by remember { mutableStateOf<List<com.example.ezroom.domain.model.RoomReview>>(emptyList()) }
    var averageRating by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(currentRoom.id) {
        val reviews = repo.getRoomReviews(currentRoom.id)
        fetchedReviews = reviews
        averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else 0f
    }
    
    // UI Animation: Entrance visibility
    val visibleState = remember { MutableTransitionState(false) }.apply { targetState = true }

    Scaffold(
        bottomBar = {
            // UI Component: Action Bar
            AnimatedVisibility(
                visibleState = visibleState,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .shadow(24.dp, shape = CircleShape),
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape,
                    tonalElevation = 8.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { onNavigateToChat(currentRoom.hostId ?: "", currentRoom.hostName ?: "Chủ nhà", currentRoom.hostPhone ?: "") },
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = PrimarySurface,
                            contentColor = PrimaryMain
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Nhắn tin",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        PrimaryButton(
                            text = if (currentRoom.status == RoomStatus.RENTED) "PHÒNG ĐÃ THUÊ" else "ĐẶT LỊCH HẸN",
                            onClick = { if (currentRoom.status != RoomStatus.RENTED) onBookAppointment(currentRoom.id) },
                            modifier = Modifier.weight(1f),
                            enabled = currentRoom.status != RoomStatus.RENTED
                        )
                    }
                }
            }
        },
        containerColor = Neutral50
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero Image Header
                item {
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { currentRoom.images.size.coerceAtLeast(1) })
                    val coroutineScope = rememberCoroutineScope()
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    ) {
                        androidx.compose.foundation.pager.HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val image = currentRoom.images.getOrNull(page)
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = image?.url?.takeIf { it.isNotBlank() } ?: image?.resId,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.5f),
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
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
                        
                        // Floating Category Badge (Property Name)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = CircleShape
                        ) {
                            Text(
                                text = property?.name ?: "Phòng lẻ",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Navigation Arrows
                        if (currentRoom.images.size > 1) {
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
                                            if (pagerState.currentPage < currentRoom.images.size - 1) {
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
                                    text = "${pagerState.currentPage + 1}/${currentRoom.images.size}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Room Selector for Complex Properties
                if (property?.type == PropertyType.COMPLEX) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(
                                text = "Chọn phòng trong dãy này",
                                style = MaterialTheme.typography.labelLarge,
                                color = Neutral500,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            var expanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryMain),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = currentRoom.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryMain
                                        )
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryMain)
                                    }
                                }
                                
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
                                ) {
                                    // Logic: Find all rooms belonging to this property from global rooms list
                                    
                                    roomsInProperty
                                        .filter { !it.isUserHidden && it.status != RoomStatus.PENDING && it.status != RoomStatus.REMOVED }
                                        .forEach { roomInProp ->
                                            val isRented = roomInProp.status == RoomStatus.RENTED
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = roomInProp.title,
                                                            modifier = Modifier.weight(1f),
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = if (isRented) Neutral300 else MaterialTheme.colorScheme.onSurface,
                                                            fontWeight = if (roomInProp.id == currentRoom.id) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                        if (isRented) {
                                                            Text(
                                                                text = "(Hết phòng)", 
                                                                style = MaterialTheme.typography.labelSmall, 
                                                                color = ErrorRose.copy(alpha = 0.6f)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    if (!isRented) {
                                                        currentRoom = roomInProp
                                                        expanded = false
                                                    }
                                                },
                                                enabled = !isRented
                                            )
                                        }
                                }
                            }
                        }
                    }
                }

                // Bento Title & Rating
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = currentRoom.title,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentRoom.priceFormatted,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            Surface(
                                color = AccentAmber.copy(alpha = 0.1f),
                                contentColor = AccentAmber,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = String.format("%.1f", averageRating), fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(6.dp).size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = property?.address ?: currentRoom.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Bento Stats Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            BentoStatCard(
                                icon = Icons.Default.SquareFoot,
                                label = "Tổng diện tích",
                                value = "${currentRoom.floorArea} m²",
                                modifier = Modifier.weight(1f)
                            )
                            BentoStatCard(
                                icon = Icons.Default.Group,
                                label = "Sức chứa",
                                value = if (currentRoom.capacity > 0) "${currentRoom.capacity} người" else "Không rõ",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Detailed Areas List (if any)
                        if (currentRoom.detailedAreas.isNotEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Chi tiết diện tích",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Neutral500,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    currentRoom.detailedAreas.forEach { area ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = area.roomName, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                text = "${area.areaValue} m²", 
                                                style = MaterialTheme.typography.bodyMedium, 
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryMain
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Utility Prices Section (Transparency)
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = "Chi phí dịch vụ",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                UtilityPriceItem(
                                    icon = Icons.Default.Bolt,
                                    label = "Giá điện",
                                    price = "${currentRoom.electricityPrice}đ/kWh",
                                    color = AccentAmber,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                VerticalDivider(modifier = Modifier.height(40.dp).align(Alignment.CenterVertically), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                
                                UtilityPriceItem(
                                    icon = Icons.Default.WaterDrop,
                                    label = "Giá nước",
                                    price = "${currentRoom.waterPrice}đ/m³",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Host Bento
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!currentRoom.hostAvatarUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = currentRoom.hostAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp).clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentRoom.hostName ?: "Chủ nhà",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { onNavigateToChat(currentRoom.hostId ?: "", currentRoom.hostName ?: "Chủ nhà", currentRoom.hostPhone ?: "0000000000") },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }

                // Bento Amenities
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        if (currentRoom.propertyId != null && currentRoom.propertyId != "standalone") {
                            Text(
                                text = "Tiện ích tòa nhà",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val commonAmenities = property?.commonAmenities ?: emptyList()
                                if (commonAmenities.isEmpty()) {
                                    Text("Theo tiêu chuẩn EzRoom", style = MaterialTheme.typography.bodySmall, color = Neutral500)
                                } else {
                                    commonAmenities.forEach { amenity ->
                                        BentoAmenityBadge(amenity.name, isCommon = true)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        Text(
                            text = "Tiện ích trong phòng",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryMain
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            currentRoom.amenities.forEach { amenity ->
                                BentoAmenityBadge(amenity.name, isCommon = false)
                            }
                        }
                    }
                }

                // Map Bento
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Vị trí",
                                style = MaterialTheme.typography.titleLarge
                            )
                            TextButton(
                                onClick = {
                                    val gmmIntentUri = "google.navigation:q=${currentRoom.latitude},${currentRoom.longitude}".toUri()
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chỉ đường", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            shape = MaterialTheme.shapes.medium,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            val roomLatLng = LatLng(currentRoom.latitude, currentRoom.longitude)
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(roomLatLng, 15f)
                                },
                                uiSettings = MapUiSettings(zoomGesturesEnabled = false, scrollGesturesEnabled = false)
                            ) {
                                Marker(state = MarkerState(position = roomLatLng))
                            }
                        }
                    }
                }

                // Reviews Section
                item {
                    val reviews = fetchedReviews
                    var selectedFilter by remember { mutableStateOf("Tất cả") }
                    val filters = listOf("Tất cả", "5 sao", "4 sao", "3 sao", "Dưới 2 sao")
                    
                    val filteredReviews = remember(selectedFilter, reviews) {
                        when (selectedFilter) {
                            "5 sao" -> reviews.filter { it.rating == 5 }
                            "4 sao" -> reviews.filter { it.rating == 4 }
                            "3 sao" -> reviews.filter { it.rating == 3 }
                            "Dưới 2 sao" -> reviews.filter { it.rating <= 2 }
                            else -> reviews
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Đánh giá (${reviews.size})",
                                style = MaterialTheme.typography.titleLarge
                            )
                            TextButton(onClick = { onNavigateToWriteReview(currentRoom.id) }) {
                                Text("Viết đánh giá", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        LazyRow(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        
                        if (filteredReviews.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    text = "Không có đánh giá phù hợp.",
                                    modifier = Modifier.padding(24.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                filteredReviews.forEach { review ->
                                    ReviewCard(review)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // Top Actions (Sticky Glassmorphism)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.3f),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        onClick = { onNavigateToReport(currentRoom.id) },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.3f),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Báo cáo",
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Surface(
                        onClick = { 
                            if (isFavorite) {
                                showUnfavoriteDialog = true
                            } else {
                                scope.launch {
                                    try {
                                        val api = com.example.ezroom.data.remote.UserProfileApi.create()
                                        val r = api.addFavorite(
                                            com.example.ezroom.data.remote.AddFavoriteRequest(user?.id ?: "", currentRoom.id)
                                        )
                                        if (r.success) {
                                            val updatedUser = user?.copy(favoriteRoomIds = r.favoriteRoomIds)
                                            if (updatedUser != null) {
                                                com.example.ezroom.util.TokenManager.saveUser(updatedUser)
                                                user = updatedUser
                                            }
                                            isFavorite = true
                                            showSnackbar("Đã thêm vào yêu thích")
                                        }
                                    } catch (e: Exception) {
                                        // Fallback
                                    }
                                }
                            }
                        },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.3f),
                        contentColor = if (isFavorite) Color.Red else Color.White
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Yêu thích",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
        
        if (showUnfavoriteDialog) {
            AlertDialog(
                onDismissRequest = { showUnfavoriteDialog = false },
                title = { Text("Bỏ yêu thích?", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn bỏ phòng này khỏi danh sách yêu thích?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val api = com.example.ezroom.data.remote.UserProfileApi.create()
                                    val r = api.removeFavorite(
                                        com.example.ezroom.data.remote.RemoveFavoriteRequest(user?.id ?: "", currentRoom.id)
                                    )
                                    if (r.success) {
                                        val updatedUser = user?.copy(favoriteRoomIds = r.favoriteRoomIds)
                                        if (updatedUser != null) {
                                            com.example.ezroom.util.TokenManager.saveUser(updatedUser)
                                            user = updatedUser
                                        }
                                        isFavorite = false
                                        showSnackbar("Đã xóa khỏi yêu thích")
                                    }
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            }
                            showUnfavoriteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Bỏ thích")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnfavoriteDialog = false }) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun BentoStatCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun BentoAmenityBadge(name: String, isCommon: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (isCommon) PrimaryMain.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp, 
            if (isCommon) PrimaryMain.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
        tonalElevation = if (isCommon) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isCommon) PrimaryMain else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                if (isCommon) {
                    Text(text = "Chung", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = PrimaryMain.copy(alpha = 0.7f))
                }
                Text(text = name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: RoomReview,
    canReply: Boolean = false,
    onReply: () -> Unit = {},
    onEditReply: () -> Unit = {},
    onDeleteReply: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isReportedState by remember { mutableStateOf(review.isReported) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = review.userName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                
                IconButton(onClick = { showReportDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Báo cáo vi phạm",
                        tint = if (isReportedState) ErrorRose else Neutral300,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, null,
                            tint = if (index < review.rating) AccentAmber else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(text = review.date, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = review.comment, style = MaterialTheme.typography.bodyMedium, color = Neutral700)

            // Reply Section
            if (review.hostReply != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimarySurface,
                    shape = MaterialTheme.shapes.small
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Phản hồi từ chủ nhà:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain,
                                modifier = Modifier.weight(1f)
                            )
                            if (canReply) {
                                Box {
                                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(14.dp), tint = PrimaryMain)
                                    }
                                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                        DropdownMenuItem(
                                            text = { Text("Chỉnh sửa", fontSize = 14.sp) },
                                            onClick = { showMenu = false; onEditReply() },
                                            leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Xóa phản hồi", fontSize = 14.sp, color = ErrorRose) },
                                            onClick = { showMenu = false; onDeleteReply() },
                                            leadingIcon = { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = ErrorRose) }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = review.hostReply, style = MaterialTheme.typography.bodySmall, color = Neutral700)
                    }
                }
            } else if (canReply) {
                TextButton(onClick = onReply, contentPadding = PaddingValues(0.dp)) {
                    Text("Phản hồi khách thuê", style = MaterialTheme.typography.labelMedium, color = PrimaryMain)
                }
            }
        }
    }

    if (showReportDialog) {
        com.example.ezroom.ui.components.ReviewReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reasonText, photoUris ->
                showReportDialog = false
                scope.launch {
                    try {
                        val uploadedUrls = mutableListOf<String>()
                        val mediaApi = com.example.ezroom.data.remote.MediaApi.create()
                        for (uri in photoUris) {
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes()
                                inputStream?.close()
                                if (bytes != null) {
                                    val reqFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                                    val bodyPart = okhttp3.MultipartBody.Part.createFormData("file", "proof_${System.currentTimeMillis()}.jpg", reqFile)
                                    val uploadRes = mediaApi.uploadMedia(bodyPart)
                                    if (uploadRes.success && !uploadRes.url.isNullOrBlank()) {
                                        uploadedUrls.add(uploadRes.url)
                                    }
                                }
                            } catch (err: Exception) {
                                android.util.Log.e("ReviewCard", "Upload image error", err)
                            }
                        }

                        val api = com.example.ezroom.data.remote.RoomReviewApi.create()
                        val req = com.example.ezroom.data.remote.ReportReviewRequest(
                            reason = reasonText,
                            proofImages = uploadedUrls
                        )
                        api.reportRoomReview(review.id, req)
                        isReportedState = true
                        android.widget.Toast.makeText(context, "Đã gửi báo cáo vi phạm kèm ${uploadedUrls.size} ảnh minh chứng tới Admin", android.widget.Toast.LENGTH_SHORT).show()
                    } catch (e: retrofit2.HttpException) {
                        val errorJson = e.response()?.errorBody()?.string()
                        val msg = try {
                            org.json.JSONObject(errorJson ?: "").optString("error")
                        } catch (_: Exception) { null }
                        android.widget.Toast.makeText(
                            context,
                            if (!msg.isNullOrEmpty()) msg else "Bạn đã báo cáo đánh giá này rồi. Báo cáo đang chờ Admin xử lý.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        isReportedState = true
                        android.widget.Toast.makeText(context, "Đã gửi báo cáo vi phạm tới Quản trị viên", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

private fun mockRoomData() = Room(
    id = "1",
    title = "Phòng trọ cao cấp ban công thoáng mát",
    price = 3500000L,
    priceFormatted = "3.500.000 ₫",
    address = "Quận 7, TP.HCM",
    detailedAddress = "123 Nguyễn Huệ, Quận 7, TP.HCM",
    description = "Mô tả chi tiết phòng trọ.",
    structure = RoomStructure.APARTMENT,
    floorArea = 25.0,
    mezzanineArea = 8.0,
    detailedAreas = emptyList(),
    rating = 4.8f,
    reviewCount = 2,
    reviews = listOf(
        RoomReview("r1", "Nguyễn Văn A", null, 5, "Phòng rất sạch sẽ, chủ nhà nhiệt tình hỗ trợ.", "12/06/2026"),
        RoomReview("r2", "Lê Thị B", null, 4, "Giá cả hợp lý, khu vực an ninh tốt.", "10/06/2026")
    ),
    images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
    amenities = listOf(Amenity("WiFi"), Amenity("Điều hòa"), Amenity("Bảo vệ 24/7")),
    latitude = 10.762622,
    longitude = 106.660172
)

@Preview(showBackground = true)
@Composable
fun RenterRoomDetailScreenPreview() {
    EzRoomTheme {
        RenterRoomDetailScreen()
    }
}
