package com.example.ezroom.ui.host.room

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.*
import com.example.ezroom.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostRoomDetailScreen(
    // Event callbacks
    modifier: Modifier = Modifier,
    room: Room? = null,
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {}
) {
    // State definitions
    var currentImageIndex by remember { mutableStateOf(0) }

    val displayRoom = remember(room) { room ?: mockRoomData() }

    // Main layout container
    Scaffold(
        topBar = {
            // Top app bar
            TopAppBar(
                title = { 
                    Text(
                        text = "Chi tiết phòng trọ",
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    titleContentColor = OnBackgroundLight
                )
            )
        },
        bottomBar = {
            // Action buttons row (Bottom Action Bar)
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = SurfaceLight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onEditClick(displayRoom.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = Shapes.small
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHỈNH SỬA BÀI ĐĂNG",
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        // Content scroll area
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Image gallery section
            item {
                ImageGallery(
                    images = displayRoom.images,
                    currentIndex = currentImageIndex,
                    onIndexChange = { currentImageIndex = it }
                )
            }

            // Basic info group
            item {
                BasicInfoSection(displayRoom)
            }

            // Location section
            item {
                LocationSection(displayRoom)
            }

            // Amenities section
            item {
                AmenitiesSection(displayRoom.amenities)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ImageGallery(
    images: List<RoomImage>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(OrangeTertiary)
    ) {
        if (images.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onIndexChange(if (currentIndex > 0) currentIndex - 1 else images.size - 1)
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Trước",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onIndexChange((currentIndex + 1) % images.size)
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Sau",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "${currentIndex % images.size + 1} / ${images.size}",
                    style = Typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(images.size.coerceAtLeast(1)) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (index == currentIndex % images.size)
                                OrangePrimary else OnBackgroundLight.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun BasicInfoSection(room: Room) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = room.title,
            style = Typography.headlineSmall,
            color = OnBackgroundLight,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = room.priceFormatted,
            style = Typography.headlineSmall,
            color = OrangePrimary,
            fontWeight = FontWeight.Bold
        )

        Column {
            Text(
                text = "Địa chỉ",
                style = Typography.bodySmall,
                color = OnBackgroundLight.copy(alpha = 0.7f)
            )
            Text(
                text = room.detailedAddress,
                style = Typography.bodyMedium,
                color = OnBackgroundLight
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Diện tích sàn",
                    style = Typography.bodySmall,
                    color = OnBackgroundLight.copy(alpha = 0.7f)
                )
                Text(
                    text = "${room.floorArea} m²",
                    style = Typography.bodyMedium,
                    color = OnBackgroundLight,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (room.mezzanineArea > 0) {
                Column {
                    Text(
                        text = "Diện tích gác",
                        style = Typography.bodySmall,
                        color = OnBackgroundLight.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${room.mezzanineArea} m²",
                        style = Typography.bodyMedium,
                        color = OnBackgroundLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (room.detailedAreas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundLight.copy(alpha = 0.5f)),
                shape = Shapes.small
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Chi tiết diện tích:", style = Typography.labelSmall, color = OnBackgroundLight.copy(alpha = 0.6f))
                    room.detailedAreas.forEach { area ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(area.roomName, style = Typography.bodySmall, color = OnBackgroundLight)
                            Text("${area.areaValue} m²", style = Typography.bodySmall, color = OnBackgroundLight, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                val filled = index < room.rating.toInt()
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (filled) TealAccent else OnBackgroundLight.copy(alpha = 0.2f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${room.rating}",
                style = Typography.bodySmall,
                color = OnBackgroundLight
            )
        }

        HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.1f))
    }
}

@Composable
private fun LocationSection(room: Room) {
    val context = LocalContext.current
    val roomLatLng = LatLng(room.latitude, room.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(roomLatLng, 15f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Vị trí trên bản đồ",
            style = Typography.titleMedium,
            color = OnBackgroundLight,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(Shapes.medium)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false,
                    tiltGesturesEnabled = false
                )
            ) {
                Marker(
                    state = rememberMarkerState(position = roomLatLng),
                    title = room.title
                )
            }

            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${room.latitude},${room.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Chỉ đường", fontSize = 12.sp)
            }
        }

        HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.1f))
    }
}

@Composable
private fun AmenitiesSection(amenities: List<Amenity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tiện ích và bồi thường",
            style = Typography.titleMedium,
            color = OnBackgroundLight,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            amenities.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { amenity ->
                        ElevatedCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = getAmenityIcon(amenity.name),
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = amenity.name,
                                    style = Typography.bodySmall,
                                    color = OnBackgroundLight,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    if (row.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun getAmenityIcon(name: String) = when (name) {
    "WiFi" -> Icons.Default.Wifi
    "Điều hòa" -> Icons.Default.AcUnit
    else -> Icons.Default.Star
}

private fun mockRoomData() = Room(
    id = "1",
    title = "Phòng trọ cao cấp Quận 7",
    price = 3500000L,
    priceFormatted = "3.500.000 ₫",
    address = "Quận 7, TP.HCM",
    detailedAddress = "123 Nguyễn Huệ, Quận 7, TP.HCM",
    description = "Mô tả chi tiết phòng trọ.",
    structure = RoomStructure.APARTMENT,
    floorArea = 25.0,
    mezzanineArea = 8.0,
    detailedAreas = listOf(
        DetailedArea("1", "Phòng ngủ 1", 12.0),
        DetailedArea("2", "Phòng khách", 10.0),
        DetailedArea("3", "WC", 3.0)
    ),
    rating = 4.5f,
    images = listOf(
        RoomImage(resId = android.R.drawable.ic_menu_gallery),
        RoomImage(resId = android.R.drawable.ic_menu_gallery)
    ),
    amenities = listOf(
        Amenity("WiFi", null),
        Amenity("Điều hòa", null)
    ),
    latitude = 10.762622,
    longitude = 106.660172
)

@Preview(showBackground = true)
@Composable
fun HostRoomDetailScreenPreview() {
    EzRoomTheme {
        HostRoomDetailScreen()
    }
}
