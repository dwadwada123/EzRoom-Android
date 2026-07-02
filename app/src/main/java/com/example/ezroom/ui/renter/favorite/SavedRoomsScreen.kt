package com.example.ezroom.ui.renter.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.Neutral50

data class SavedRoomUI(
    val id: String,
    val title: String,
    val address: String,
    val price: String,
    val imageUrl: String,
    val rating: Float = 4.5f,
)

@Composable
fun SavedRoomsScreen(
    onRoomClick: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    // Mock saved rooms with full price
    var savedRooms by remember { 
        mutableStateOf(
            listOf(
                SavedRoomUI(
                    id = "room_1",
                    title = "Phòng trọ cao cấp Quận 7",
                    address = "Quận 7, TP.HCM",
                    price = "3.500.000 đ",
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400",
                ),
                SavedRoomUI(
                    id = "room_2",
                    title = "Chung cư mini Bình Thạnh",
                    address = "Bình Thạnh, TP.HCM",
                    price = "5.000.000 đ",
                    imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=400",
                ),
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        if (savedRooms.isEmpty()) {
            EmptyState(
                title = "Chưa có phòng yêu thích",
                description = "Hãy khám phá và lưu lại những căn phòng bạn ưng ý nhất.",
                icon = Icons.Default.Favorite,
                actionText = "Khám phá ngay",
                onAction = onNavigateToExplore,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                            IconButton(
                                onClick = {
                                    savedRooms = savedRooms.filter { it.id != room.id }
                                    onShowSnackbar("Đã xóa khỏi danh sách yêu thích")
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}
