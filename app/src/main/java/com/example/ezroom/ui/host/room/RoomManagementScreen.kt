package com.example.ezroom.ui.host.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.*
import com.example.ezroom.ui.theme.EzRoomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    // Event callbacks
    onRoomClick: (String) -> Unit = {},
    onEditClick: (Room) -> Unit = {},
    onDeleteClick: (Room) -> Unit = {},
    onHideClick: (Room) -> Unit = {}
) {
    // State definitions
    var selectedStatus by remember { mutableStateOf(RoomStatus.ACTIVE) }
    val hiddenRoomIds = remember { mutableStateListOf<String>() }

    // Mock data
    val mockRooms = remember {
        listOf(
            Room(
                id = "1",
                title = "Phòng trọ cao cấp ban công thoáng mát gần Đại học",
                price = 3500000L,
                priceFormatted = "3.500.000 ₫",
                address = "Hải Châu, Đà Nẵng",
                detailedAddress = "123 Lê Lợi, Hải Châu, Đà Nẵng",
                description = "Mô tả phòng",
                structure = RoomStructure.APARTMENT,
                floorArea = 25.0,
                mezzanineArea = 0.0,
                rating = 4.5f,
                images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
                amenities = emptyList(),
                status = RoomStatus.ACTIVE,
                latitude = 16.0,
                longitude = 108.0
            ),
            Room(
                id = "2",
                title = "Phòng full nội thất, giờ giấc tự do quận Hải Châu",
                price = 4200000L,
                priceFormatted = "4.200.000 ₫",
                address = "Hải Châu, Đà Nẵng",
                detailedAddress = "456 Hùng Vương, Hải Châu, Đà Nẵng",
                description = "Mô tả phòng",
                structure = RoomStructure.SINGLE,
                floorArea = 30.0,
                mezzanineArea = 5.0,
                rating = 4.8f,
                images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
                amenities = emptyList(),
                status = RoomStatus.ACTIVE,
                latitude = 16.1,
                longitude = 108.1
            ),
            Room(
                id = "3",
                title = "Chung cư mini giá rẻ cho sinh viên",
                price = 2800000L,
                priceFormatted = "2.800.000 ₫",
                address = "Thanh Khê, Đà Nẵng",
                detailedAddress = "789 Điện Biên Phủ, Thanh Khê, Đà Nẵng",
                description = "Mô tả phòng",
                structure = RoomStructure.APARTMENT,
                floorArea = 20.0,
                mezzanineArea = 0.0,
                rating = 4.0f,
                images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
                amenities = emptyList(),
                status = RoomStatus.RENTED,
                latitude = 16.2,
                longitude = 108.2
            )
        )
    }

    val filteredRooms = remember(selectedStatus) { 
        mockRooms.filter { it.status == selectedStatus } 
    }

    // Main layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab bar section
        TabRow(
            selectedTabIndex = selectedStatus.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedStatus.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            RoomStatus.entries.forEach { status ->
                Tab(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    text = {
                        Text(
                            text = status.title,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                            ),
                            maxLines = 1
                        )
                    }
                )
            }
        }

        // Content scroll area
        if (filteredRooms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy phòng nào ở trạng thái này.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredRooms, key = { it.id }) { room ->
                    val isHidden = hiddenRoomIds.contains(room.id)
                    RoomPostCard(
                        room = room,
                        isHidden = isHidden,
                        onCardClick = { onRoomClick(room.id) },
                        onEditClick = { onEditClick(room) },
                        onDeleteClick = { onDeleteClick(room) },
                        onHideToggle = { 
                            if (isHidden) hiddenRoomIds.remove(room.id) 
                            else hiddenRoomIds.add(room.id)
                            onHideClick(room)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomPostCard(
    room: Room,
    isHidden: Boolean = false,
    onCardClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onHideToggle: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isHidden) 0.5f else 1.0f)
            .clickable(onClick = onCardClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                val imageRes = room.images.firstOrNull()?.resId ?: android.R.drawable.ic_menu_gallery
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Room Image",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = room.priceFormatted,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Diện tích: ${room.floorArea} m²",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onHideToggle,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle visibility",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHidden) "Hiện tin" else "Ẩn tin", 
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedButton(
                    onClick = onEditClick,
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Sửa", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = onDeleteClick,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Xóa", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomManagementScreenPreview() {
    EzRoomTheme {
        RoomManagementScreen()
    }
}
