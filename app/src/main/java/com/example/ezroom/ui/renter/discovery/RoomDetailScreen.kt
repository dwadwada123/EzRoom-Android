package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

data class RoomImage(
    val id: String,
    val label: String,
    val color: androidx.compose.ui.graphics.Color = OrangeTertiary
)

data class Amenity(
    val id: String,
    val name: String,
    val compensation: String,
    val iconIndex: Int = 0
)

data class RoomDetail(
    val id: String,
    val title: String,
    val price: String,
    val address: String,
    val detailedAddress: String,
    val floorArea: String,
    val mezzanineArea: String = "",
    val images: List<RoomImage> = emptyList(),
    val amenities: List<Amenity> = emptyList(),
    val rating: Float = 4.5f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    modifier: Modifier = Modifier,
    room: RoomDetail? = null,
    onBackClick: () -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onBookAppointment: (String) -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val defaultRoom = RoomDetail(
        id = "1",
        title = "Phòng trọ cao cấp Q7",
        price = "3.500.000₫/tháng",
        address = "Quận 7, TP.HCM",
        detailedAddress = "Số 123 Đường Nguyễn Huệ, Phường 1, Quận 7, TP.HCM",
        floorArea = "25m²",
        mezzanineArea = "8m²",
        images = listOf(
            RoomImage("1", "Ảnh phòng khách"),
            RoomImage("2", "Ảnh phòng ngủ"),
            RoomImage("3", "Ảnh phòng tắm"),
            RoomImage("4", "Ảnh bếp")
        ),
        amenities = listOf(
            Amenity("1", "WiFi", "500.000₫", 0),
            Amenity("2", "Máy giặt", "1.000.000₫", 1),
            Amenity("3", "Tủ lạnh", "2.000.000₫", 2),
            Amenity("4", "Điều hòa", "3.000.000₫", 3),
            Amenity("5", "Giờ giấc tự do", "500.000₫", 4),
            Amenity("6", "Bảo vệ 24/7", "1.500.000₫", 5)
        ),
        rating = 4.5f
    )

    val displayRoom = room ?: defaultRoom

    val amenityIcons = listOf(
        Icons.Default.Wifi,
        Icons.Default.LocalLaundryService,
        Icons.Default.Kitchen,
        Icons.Default.AcUnit,
        Icons.Default.Schedule,
        Icons.Default.Security
    )

    fun getAmenityIcon(iconIndex: Int) = amenityIcons.getOrElse(iconIndex) { Icons.Default.Star }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Chi tiết phòng trọ") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    isFavorite = !isFavorite
                    onFavoriteClick(displayRoom.id)
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) androidx.compose.material3.MaterialTheme.colorScheme.error else OnBackgroundLight
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = OnBackgroundLight
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Image Carousel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(OrangeTertiary)
                ) {
                    // Image placeholder with label
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = if (displayRoom.images.isNotEmpty()) {
                                displayRoom.images[currentImageIndex % displayRoom.images.size].label
                            } else {
                                "Ảnh phòng"
                            },
                            style = Typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier
                                .padding(12.dp)
                                .background(
                                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                                    shape = Shapes.small
                                )
                                .padding(6.dp)
                        )
                    }

                    // Navigation Buttons (Inside the image block)
                    if (displayRoom.images.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentImageIndex =
                                        if (currentImageIndex > 0) currentImageIndex - 1 else displayRoom.images.size - 1
                                },
                                modifier = Modifier
                                    .background(
                                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    tint = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentImageIndex =
                                        (currentImageIndex + 1) % displayRoom.images.size
                                },
                                modifier = Modifier
                                    .background(
                                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    tint = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Counter (1 / 4) - Positioned at Top Right
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Text(
                                text = "${currentImageIndex % displayRoom.images.size + 1} / ${displayRoom.images.size}",
                                style = Typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier
                                    .background(
                                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Image navigation dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(displayRoom.images.size.coerceAtLeast(1)) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (index == currentImageIndex % displayRoom.images.size)
                                            OrangePrimary else OnBackgroundLight.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            // Room Info Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = displayRoom.title,
                        style = Typography.headlineSmall,
                        color = OnBackgroundLight,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = displayRoom.price,
                        style = Typography.headlineSmall,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold
                    )

                    // Address
                    Column {
                        Text(
                            text = "Địa chỉ",
                            style = Typography.bodySmall,
                            color = OnBackgroundLight.copy(alpha = 0.7f)
                        )
                        Text(
                            text = displayRoom.detailedAddress,
                            style = Typography.bodyMedium,
                            color = OnBackgroundLight
                        )
                    }

                    // Area Info
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
                                text = displayRoom.floorArea,
                                style = Typography.bodyMedium,
                                color = OnBackgroundLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (displayRoom.mezzanineArea.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Diện tích gác",
                                    style = Typography.bodySmall,
                                    color = OnBackgroundLight.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = displayRoom.mezzanineArea,
                                    style = Typography.bodyMedium,
                                    color = OnBackgroundLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            val filled = index < displayRoom.rating.toInt()
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (filled) TealAccent else OnBackgroundLight.copy(alpha = 0.2f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${displayRoom.rating}",
                            style = Typography.bodySmall,
                            color = OnBackgroundLight
                        )
                    }

                    Divider(color = OnBackgroundLight.copy(alpha = 0.1f))
                }
            }

            // Amenities Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tiện ích kèm giá đền bù",
                        style = Typography.titleMedium,
                        color = OnBackgroundLight,
                        fontWeight = FontWeight.Bold
                    )

                    // Amenities Grid
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        displayRoom.amenities.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                row.forEach { amenity ->
                                    ElevatedCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(120.dp),
                                        colors = CardDefaults.elevatedCardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = getAmenityIcon(amenity.iconIndex),
                                                contentDescription = null,
                                                tint = OrangePrimary,
                                                modifier = Modifier.size(24.dp)
                                            )

                                            Text(
                                                text = amenity.name,
                                                style = Typography.bodySmall,
                                                color = OnBackgroundLight,
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )

                                            Text(
                                                text = "Đền bù: ${amenity.compensation}",
                                                style = Typography.labelSmall,
                                                color = OnBackgroundLight.copy(alpha = 0.6f),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bottom Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    showDatePicker = true
                    onBookAppointment(displayRoom.id)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = Shapes.small
            ) {
                Text(
                    "Đặt lịch hẹn xem phòng",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Chọn ngày giờ xem phòng") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Chức năng chọn ngày giờ sẽ được thêm vào sau", color = OnBackgroundLight)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomDetailScreenPreview() {
    EzRoomTheme {
        RoomDetailScreen()
    }
}
