package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    rooms: List<RoomItem> = emptyList(),
    filterParams: FilterParams = FilterParams(),
    onRoomClick: (RoomItem) -> Unit = {},
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            title = { Text("Kết quả tìm kiếm") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = OnBackgroundLight
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Result count section
            item {
                Column {
                    Text(
                        text = "Tìm thấy ${rooms.size} phòng trọ",
                        style = Typography.headlineSmall,
                        color = OnBackgroundLight
                    )

                    // Applied filters display
                    if (filterParams.selectedDistrict.isNotEmpty() ||
                        filterParams.priceRange != (1f..10f) ||
                        filterParams.selectedAreaRange.isNotEmpty() ||
                        filterParams.selectedAmenities.isNotEmpty()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (filterParams.selectedDistrict.isNotEmpty()) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text(filterParams.selectedDistrict, style = Typography.labelSmall) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = OrangeTertiary
                                    )
                                )
                            }

                            if (filterParams.priceRange != (1f..10f)) {
                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            "₫${filterParams.priceRange.start.toInt()}M - ₫${filterParams.priceRange.endInclusive.toInt()}M",
                                            style = Typography.labelSmall
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = OrangeTertiary
                                    )
                                )
                            }

                            if (filterParams.selectedAreaRange.isNotEmpty()) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text(filterParams.selectedAreaRange, style = Typography.labelSmall) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = OrangeTertiary
                                    )
                                )
                            }

                            // Show filter button
                            Button(
                                onClick = onFilterClick,
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealAccent,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = Shapes.medium
                            ) {
                                Text("Bộ lọc", style = Typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = OnBackgroundLight.copy(alpha = 0.1f))
                }
            }

            // Rooms list
            if (rooms.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Không tìm thấy phòng trọ nào",
                            style = Typography.bodyLarge,
                            color = OnBackgroundLight.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Thử thay đổi bộ lọc để xem thêm kết quả",
                            style = Typography.bodyMedium,
                            color = OnBackgroundLight.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                items(rooms) { room ->
                    RoomResultCard(
                        room = room,
                        onClick = { onRoomClick(room) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoomResultCard(
    room: RoomItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(OrangeTertiary, shape = Shapes.small)
            ) {
                // Image will go here
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(room.title, style = Typography.titleMedium, color = OnBackgroundLight)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    room.price,
                    style = Typography.titleSmall,
                    color = OrangePrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    room.address,
                    style = Typography.bodySmall,
                    color = OnBackgroundLight.copy(alpha = 0.7f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        val filled = index < room.rating.toInt()
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (filled) TealAccent else OnBackgroundLight.copy(alpha = 0.2f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${room.rating}",
                        style = Typography.labelSmall,
                        color = OnBackgroundLight
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    val sampleRooms = listOf(
        RoomItem("1", "Phòng trọ cao cấp Q7", "3.500.000₫/tháng", "Quận 7, TP.HCM", 4.5f),
        RoomItem("2", "Studio tiện nghi gần chợ", "4.200.000₫/tháng", "Quận 1, TP.HCM", 4.8f),
        RoomItem("3", "Phòng gác lửng thoáng mát", "2.800.000₫/tháng", "Bình Thạnh, TP.HCM", 4.2f),
        RoomItem("4", "Căn hộ dịch vụ mới", "5.500.000₫/tháng", "Quận 3, TP.HCM", 4.9f)
    )
    EzRoomTheme {
        SearchResultScreen(
            rooms = sampleRooms,
            filterParams = FilterParams(
                selectedDistrict = "Quận 7",
                priceRange = 2f..6f
            )
        )
    }
}
