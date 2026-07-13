package com.example.ezroom.ui.renter.favorite

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.ErrorRose
import com.example.ezroom.ui.theme.Neutral50
import com.example.ezroom.ui.theme.PrimaryMain

// Data Model: Saved Room UI State
data class SavedRoomUI(
    val id: String,
    val title: String,
    val address: String,
    val price: String,
    val imageUrl: String,
    val rating: Float = 4.5f,
)

// UI Component: Saved Rooms Screen
@Composable
fun SavedRoomsScreen(
    onRoomClick: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    // State Management: Mock data for saved rooms
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

    // State Management: Dialog states
    var showUnfavoriteDialog by remember { mutableStateOf(false) }
    var roomToUnfavorite by remember { mutableStateOf<SavedRoomUI?>(null) }
    var showUnfavoriteAllDialog by remember { mutableStateOf(false) }

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
            Column(modifier = Modifier.fillMaxSize()) {
                // UI Component: Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showUnfavoriteAllDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = ErrorRose)
                    ) {
                        Icon(Icons.Default.DeleteSweep, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bỏ thích tất cả", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }

                // UI Component: Animated List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(savedRooms, key = { _, it -> it.id }) { index, room ->
                        var isVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            isVisible = true
                        }
                        
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { 100 },
                                animationSpec = tween(durationMillis = 400, delayMillis = index * 100)
                            ) + fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = index * 100))
                        ) {
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
                                            roomToUnfavorite = room
                                            showUnfavoriteDialog = true
                                        },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                    }
                                }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }

        // UI Component: Single Unfavorite Dialog
        if (showUnfavoriteDialog && roomToUnfavorite != null) {
            AlertDialog(
                onDismissRequest = { showUnfavoriteDialog = false },
                title = { Text("Bỏ yêu thích?", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn bỏ phòng '${roomToUnfavorite?.title}' khỏi danh sách yêu thích?") },
                confirmButton = {
                    Button(
                        onClick = {
                            savedRooms = savedRooms.filter { it.id != roomToUnfavorite?.id }
                            onShowSnackbar("Đã xóa khỏi danh sách yêu thích")
                            showUnfavoriteDialog = false
                            roomToUnfavorite = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Bỏ thích")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnfavoriteDialog = false }) {
                        Text("Giữ lại")
                    }
                },
                containerColor = Color.White
            )
        }

        // UI Component: Unfavorite All Dialog
        if (showUnfavoriteAllDialog) {
            AlertDialog(
                onDismissRequest = { showUnfavoriteAllDialog = false },
                title = { Text("Bỏ thích tất cả?", fontWeight = FontWeight.Bold) },
                text = { Text("Toàn bộ các phòng trong danh sách yêu thích của bạn sẽ bị xóa. Bạn có chắc chắn?") },
                confirmButton = {
                    Button(
                        onClick = {
                            savedRooms = emptyList()
                            onShowSnackbar("Đã xóa tất cả phòng yêu thích")
                            showUnfavoriteAllDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Xóa tất cả")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnfavoriteAllDialog = false }) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
