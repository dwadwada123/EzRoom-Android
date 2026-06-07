package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RoomItem(
    val id: String,
    val title: String,
    val price: String,
    val address: String,
    val rating: Float,
    val imageUrl: Any? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterHomeScreen(
    modifier: Modifier = Modifier,
    // Event callbacks
    onRoomClick: (RoomItem) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    // State definitions
    var query by remember { mutableStateOf("") }
    var currentScreen by remember { mutableStateOf("HOME") }
    var selectedFilter by remember { mutableStateOf(FilterParams()) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf("Phòng đơn", "Studio", "Gác lửng", "Nguyên căn")
    val sampleRooms = remember {
        MockData.rooms.map { room ->
            RoomItem(
                id = room.id, 
                title = room.title, 
                price = room.priceFormatted, 
                address = room.address, 
                rating = room.rating,
                imageUrl = room.images.firstOrNull()?.resId
            )
        }
    }

    fun refreshData() {
        scope.launch {
            isLoading = true
            isError = false
            delay(1200) // Simulate fetching
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    val filteredRooms = remember(selectedFilter) {
        if (selectedFilter.selectedDistrict.isNotEmpty() ||
            selectedFilter.priceRange != (1f..10f) ||
            selectedFilter.selectedAreaRange.isNotEmpty() ||
            selectedFilter.selectedAmenities.isNotEmpty()
        ) {
            sampleRooms.filter {
                val matchDistrict = if (selectedFilter.selectedDistrict.isNotEmpty())
                    it.address.contains(selectedFilter.selectedDistrict) else true
                matchDistrict
            }
        } else {
            sampleRooms
        }
    }

    // Main layout container
    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            "HOME" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    // Input fields group
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
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                            singleLine = true,
                            shape = Shapes.small,
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.12f),
                                focusedLeadingIconColor = OrangePrimary,
                                cursorColor = OrangePrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { if (!isLoading) currentScreen = "FILTER" },
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Bộ lọc",
                                tint = OrangePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Categories scroll area
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories, key = { it }) { cat ->
                            AssistChip(
                                onClick = { if (!isLoading) currentScreen = "SEARCH_RESULT" },
                                label = { Text(cat, style = Typography.bodySmall) },
                                leadingIcon = {
                                    Icon(Icons.Default.Home, contentDescription = null, tint = OrangePrimary)
                                },
                                shape = Shapes.small,
                                enabled = !isLoading
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

                    if (isError) {
                        DiscoveryEmptyState(
                            title = "Đã có lỗi xảy ra",
                            desc = "Chúng tôi không thể tải danh sách phòng. Vui lòng thử lại.",
                            onRetry = { refreshData() }
                        )
                    } else if (!isLoading && sampleRooms.isEmpty()) {
                        DiscoveryEmptyState(
                            title = "Không có phòng trống",
                            desc = "Vui lòng quay lại sau để xem thêm các bài đăng mới.",
                            onRetry = { refreshData() }
                        )
                    } else {
                        // Content scroll area
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(sampleRooms, key = { it.id }) { room ->
                                RoomCard(
                                    title = room.title,
                                    price = room.price,
                                    address = room.address,
                                    rating = room.rating,
                                    imageUrl = room.imageUrl,
                                    onClick = { if (!isLoading) onRoomClick(room) }
                                )
                            }
                        }
                    }
                }
            }
            "FILTER" -> {
                AdvancedFilterScreen(
                    onFilterApply = { params ->
                        selectedFilter = params
                        currentScreen = "SEARCH_RESULT"
                    },
                    onDismiss = { currentScreen = "HOME" }
                )
            }
            "SEARCH_RESULT" -> {
                SearchResultScreen(
                    rooms = filteredRooms,
                    filterParams = selectedFilter,
                    onRoomClick = { room -> onRoomClick(room) },
                    onBackClick = { currentScreen = "HOME" },
                    onFilterClick = { currentScreen = "FILTER" }
                )
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Composable
fun DiscoveryEmptyState(title: String, desc: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = OnBackgroundLight.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = Typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = desc, style = Typography.bodyMedium, color = OnBackgroundLight.copy(alpha = 0.6f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = Shapes.small
        ) {
            Text("Thử lại")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RenterHomeScreenPreview() {
    EzRoomTheme {
        RenterHomeScreen()
    }
}
