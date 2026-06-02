package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.*

data class RoomItem(
    val id: String,
    val title: String,
    val price: String,
    val address: String,
    val rating: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterHomeScreen(
    modifier: Modifier = Modifier,
    onOpenFilters: () -> Unit = {},
    onRoomClick: (RoomItem) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val categories = listOf("Phòng đơn", "Studio", "Phòng gác lửng", "Căn hộ nguyên căn")

    val sampleRooms = listOf(
        RoomItem("1", "Phòng trọ cao cấp Q7", "3.500.000₫/tháng", "Quận 7, TP.HCM", 4.5f),
        RoomItem("2", "Studio tiện nghi gần chợ", "4.200.000₫/tháng", "Quận 1, TP.HCM", 4.8f),
        RoomItem("3", "Phòng gác lửng thoáng mát", "2.800.000₫/tháng", "Bình Thạnh, TP.HCM", 4.2f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search bar with filter button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("Tìm phòng, địa điểm...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = Shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.12f),
                    focusedLeadingIconColor = OrangePrimary,
                    cursorColor = OrangePrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    showFilterDialog = true
                    onOpenFilters()
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Filter",
                    tint = OrangePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories - horizontal scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                AssistChip(
                    onClick = { /* TODO: filter by category */ },
                    label = { Text(cat, style = Typography.bodySmall) },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null, tint = OrangePrimary)
                    },
                    shape = Shapes.small
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gợi ý cho bạn",
            style = Typography.titleLarge,
            color = OnBackgroundLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of recommended rooms
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sampleRooms) { room ->
                ElevatedCard(
                    onClick = { onRoomClick(room) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.medium,
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        // Image placeholder
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(OrangeTertiary, shape = Shapes.small)
                        ) {
                            // If you want an image later, replace this Box with Image(...)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(room.title, style = Typography.titleLarge, color = OnBackgroundLight)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(room.price, style = Typography.bodyLarge, color = OnBackgroundLight)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(room.address, style = Typography.bodyMedium, color = OnBackgroundLight.copy(alpha = 0.7f))

                            Spacer(modifier = Modifier.height(8.dp))
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
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${room.rating}", style = Typography.bodySmall, color = OnBackgroundLight)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Bộ lọc nâng cao") },
            text = { Text("Tại đây sẽ có các tuỳ chọn lọc: giá, diện tích, vị trí, tiện ích...", color = OnBackgroundLight) },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RenterHomeScreenPreview() {
    EzRoomTheme {
        RenterHomeScreen()
    }
}
