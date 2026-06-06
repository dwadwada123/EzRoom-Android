package com.example.ezroom.ui.renter.favorite

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezroom.ui.theme.EzRoomTheme

data class SavedRoom(
    val id: String,
    val title: String,
    val price: String,
    val address: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRoomsScreen(
    onRoomClick: (String) -> Unit = {},
    onNavigateToExplore: () -> Unit = {}
) {
    // Mock Data initial state
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phòng đã lưu", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (savedRooms.isEmpty()) {
                EmptyState(onNavigateToExplore)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(savedRooms, key = { it.id }) { room ->
                        SavedRoomItem(
                            room = room,
                            onClick = { onRoomClick(room.id) },
                            onRemove = { savedRooms.remove(room) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(onNavigateToExplore: () -> Unit) {
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToExplore,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Khám phá phòng ngay")
        }
    }
}

@Composable
fun SavedRoomItem(
    room: SavedRoom,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Room Image
            AsyncImage(
                model = room.imageUrl,
                contentDescription = room.title,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            // Room Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.price,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Favorite Icon
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove",
                    tint = Color.Red // Or MaterialTheme.colorScheme.primary
                )
            }
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

@Preview(showBackground = true)
@Composable
fun EmptySavedRoomsPreview() {
    EzRoomTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyState(onNavigateToExplore = {})
        }
    }
}
