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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.ErrorRose
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.Neutral50
import com.example.ezroom.ui.theme.PrimaryMain
import com.example.ezroom.data.model.MockData
import kotlinx.coroutines.launch
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.data.remote.UserProfileApi
import com.example.ezroom.data.remote.AuthApi
import com.example.ezroom.domain.model.Room


// Data Model: Saved Room UI State
data class SavedRoomUI(
    val id: String,
    val title: String,
    val address: String,
    val price: String,
    val imageUrl: String,
    val rating: Float = 0f,
)

// UI Component: Saved Rooms Screen
@Composable
fun SavedRoomsScreen(
    onRoomClick: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    userProfileApi: UserProfileApi = remember { NetworkClient.createService<UserProfileApi>() },
    authApi: AuthApi = remember { NetworkClient.createService<AuthApi>() }
) {
    val scope = rememberCoroutineScope()
    var favoriteIds by remember { mutableStateOf(emptyList<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    val user = remember { com.example.ezroom.util.TokenManager.getUser() }
    var allRooms by remember { mutableStateOf(emptyList<Room>()) }

    LaunchedEffect(Unit) {
        try {
            val response = authApi.getProfile()
            if (response.success && response.user != null) {
                com.example.ezroom.util.TokenManager.saveUser(response.user)
                favoriteIds = response.user.favoriteRoomIds
            }
            val roomApi = NetworkClient.createService<com.example.ezroom.data.remote.RoomApi>()
            allRooms = roomApi.getRooms().map { r ->
                Room(
                    id = r.resolvedId,
                    propertyId = r.propertyId,
                    title = r.title,
                    price = r.price,
                    priceFormatted = "${r.price} đ",
                    electricityPrice = r.electricityPrice,
                    waterPrice = r.waterPrice,
                    address = r.address,
                    detailedAddress = r.detailedAddress,
                    description = r.description,
                    structure = try { com.example.ezroom.domain.model.RoomStructure.valueOf(r.structure) } catch (e: Exception) { com.example.ezroom.domain.model.RoomStructure.SINGLE },
                    floorArea = r.floorArea,
                    images = r.images?.map { img ->
                        com.example.ezroom.domain.model.RoomImage(
                            url = img["url"] ?: "",
                            category = img["category"] ?: "Khác"
                        )
                    } ?: emptyList(),
                    amenities = emptyList(),
                    status = try { com.example.ezroom.domain.model.RoomStatus.valueOf(r.status) } catch (e: Exception) { com.example.ezroom.domain.model.RoomStatus.ACTIVE },
                    latitude = r.latitude,
                    longitude = r.longitude,
                    rating = r.rating,
                    reviewCount = r.reviewCount
                )
            }
        } catch (e: Exception) {
            favoriteIds = com.example.ezroom.util.TokenManager.getUser()?.favoriteRoomIds ?: emptyList()
        } finally {
            isLoading = false
        }
    }


    val savedRooms = remember(favoriteIds, allRooms) {
        allRooms.filter { it.id in favoriteIds }.map { room ->
            val image = room.images.firstOrNull()
            val imageUrl = image?.url?.takeIf { it.isNotBlank() } ?: ""
            SavedRoomUI(
                id = room.id,
                title = room.title,
                address = room.address,
                price = room.priceFormatted,
                imageUrl = imageUrl,
                rating = room.rating
            )
        }
    }


    // State Management: Dialog states
    var showUnfavoriteDialog by remember { mutableStateOf(false) }
    var roomToUnfavorite by remember { mutableStateOf<SavedRoomUI?>(null) }
    var showUnfavoriteAllDialog by remember { mutableStateOf(false) }

    SavedRoomsContent(
        savedRooms = savedRooms,
        onRoomClick = onRoomClick,
        onNavigateToExplore = onNavigateToExplore,
        onUnfavoriteClick = { room ->
            roomToUnfavorite = room
            showUnfavoriteDialog = true
        },
        onUnfavoriteAllClick = { showUnfavoriteAllDialog = true }
    )

    // UI Component: Single Unfavorite Dialog
        if (showUnfavoriteDialog && roomToUnfavorite != null) {
            AlertDialog(
                onDismissRequest = { showUnfavoriteDialog = false },
                title = { Text("Bỏ yêu thích?", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn bỏ phòng '${roomToUnfavorite?.title}' khỏi danh sách yêu thích?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val r = userProfileApi.removeFavorite(
                                        com.example.ezroom.data.remote.RemoveFavoriteRequest(user?.id ?: "", roomToUnfavorite?.id ?: "")
                                    )
                                    if (r.success) {
                                        favoriteIds = r.favoriteRoomIds
                                        // Sync user state
                                        val updatedUser = user?.copy(favoriteRoomIds = r.favoriteRoomIds)
                                        if (updatedUser != null) {
                                            com.example.ezroom.util.TokenManager.saveUser(updatedUser)
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Error handling
                                }
                                onShowSnackbar("Đã xóa khỏi danh sách yêu thích")
                                showUnfavoriteDialog = false
                                roomToUnfavorite = null
                            }
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

        // Unfavorite All Dialog
        if (showUnfavoriteAllDialog) {
            AlertDialog(
                onDismissRequest = { showUnfavoriteAllDialog = false },
                title = { Text("Bỏ thích tất cả?", fontWeight = FontWeight.Bold) },
                text = { Text("Toàn bộ các phòng trong danh sách yêu thích của bạn sẽ bị xóa. Bạn có chắc chắn?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    var lastFavoriteIds = favoriteIds
                                    for (id in favoriteIds) {
                                        val r = userProfileApi.removeFavorite(
                                            com.example.ezroom.data.remote.RemoveFavoriteRequest(user?.id ?: "", id)
                                        )
                                        if (r.success) lastFavoriteIds = r.favoriteRoomIds
                                    }
                                    favoriteIds = emptyList()
                                    // Sync user state
                                    val updatedUser = user?.copy(favoriteRoomIds = emptyList())
                                    if (updatedUser != null) {
                                        com.example.ezroom.util.TokenManager.saveUser(updatedUser)
                                    }
                                } catch (e: Exception) {
                                    // Error handling
                                }
                                onShowSnackbar("Đã xóa tất cả phòng yêu thích")
                                showUnfavoriteAllDialog = false
                            }
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

@Composable
private fun SavedRoomsContent(
    savedRooms: List<SavedRoomUI>,
    onRoomClick: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onUnfavoriteClick: (SavedRoomUI) -> Unit,
    onUnfavoriteAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Neutral50)) {
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
                        onClick = onUnfavoriteAllClick,
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
                                        onClick = { onUnfavoriteClick(room) },
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
    }
}

@Preview(showBackground = true)
@Composable
fun SavedRoomsScreenPreview() {
    val sampleRooms = listOf(
        SavedRoomUI(
            id = "1",
            title = "Phòng trọ cao cấp Quận 7",
            address = "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
            price = "3.500.000 đ",
            imageUrl = "",
            rating = 4.5f
        ),
        SavedRoomUI(
            id = "2",
            title = "Căn hộ dịch vụ tiện nghi",
            address = "456 Lê Văn Sỹ, Quận 3, TP.HCM",
            price = "5.000.000 đ",
            imageUrl = "",
            rating = 4.8f
        )
    )
    EzRoomTheme {
        SavedRoomsContent(
            savedRooms = sampleRooms,
            onRoomClick = {},
            onNavigateToExplore = {},
            onUnfavoriteClick = {},
            onUnfavoriteAllClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SavedRoomsScreenEmptyPreview() {
    EzRoomTheme {
        SavedRoomsContent(
            savedRooms = emptyList(),
            onRoomClick = {},
            onNavigateToExplore = {},
            onUnfavoriteClick = {},
            onUnfavoriteAllClick = {}
        )
    }
}
