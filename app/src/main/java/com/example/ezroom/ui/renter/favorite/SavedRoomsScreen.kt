package com.example.ezroom.ui.renter.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.EzRoomTheme

data class SavedRoom(
    val id: String,
    val title: String,
    val price: String,
    val address: String,
    val imageUrl: String,
    val rating: Float = 4.5f
)

@Composable
fun SavedRoomsScreen(
    // Event callbacks
    onRoomClick: (String) -> Unit = {},
    onNavigateToExplore: () -> Unit = {}
) {
    // State definitions
    val savedRooms = remember {
        mutableStateListOf(
            SavedRoom(
                id = "1",
                title = "Phòng trọ cao cấp trung tâm Quận 1",
                price = "5.500.000 đ/tháng",
                address = "Đa Kao, Quận 1, TP.HCM",
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400"
            ),
            SavedRoom(
                id = "2",
                title = "Căn hộ mini đầy đủ tiện nghi",
                price = "3.800.000 đ/tháng",
                address = "Phường 25, Bình Thạnh, TP.HCM",
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=400"
            ),
            SavedRoom(
                id = "3",
                title = "Phòng trọ giá rẻ cho sinh viên",
                price = "2.500.000 đ/tháng",
                address = "Linh Trung, Thủ Đức, TP.HCM",
                imageUrl = "https://images.unsplash.com/photo-1554995207-c18c203602cb?q=80&w=400"
            )
        )
    }

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        if (savedRooms.isEmpty()) {
            EmptySavedState(onNavigateToExplore)
        } else {
            // Content scroll area
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedRooms, key = { it.id }) { room ->
                    RoomCard(
                        title = room.title,
                        price = room.price,
                        address = room.address,
                        rating = room.rating,
                        imageUrl = room.imageUrl,
                        onClick = { onRoomClick(room.id) },
                        trailingIcon = {
                            IconButton(onClick = { savedRooms.remove(room) }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Xóa",
                                    tint = Color.Red
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(onNavigateToExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bạn chưa lưu phòng trọ nào.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Action buttons row
        Button(
            onClick = onNavigateToExplore,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Khám phá phòng ngay")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedRoomsScreenPreview() {
    EzRoomTheme {
        SavedRoomsScreen()
    }
}
