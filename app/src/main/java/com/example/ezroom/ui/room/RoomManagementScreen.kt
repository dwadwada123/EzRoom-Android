package com.example.ezroom.ui.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

// 1. Định nghĩa các Trạng thái Phòng
enum class RoomStatus(val title: String) {
    ACTIVE("Đang hiển thị"),
    RENTED("Đã cho thuê"),
    PENDING("Chờ kiểm duyệt")
}

// 2. Data Class mô phỏng dữ liệu Phòng trọ
data class RoomPost(
    val id: String,
    val title: String,
    val price: String,
    val area: Double,
    val imageUrl: Int, // Thay bằng String nếu dùng Coil/Glide loaded từ URL
    val status: RoomStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    onEditClick: (RoomPost) -> Unit = {},
    onDeleteClick: (RoomPost) -> Unit = {},
    onHideClick: (RoomPost) -> Unit = {}
) {
    // Trạng thái tab bộ lọc đang chọn
    var selectedStatus by remember { mutableStateOf(RoomStatus.ACTIVE) }

    // Dữ liệu giả lập (Mock Data) để hiển thị lên UI
    val mockRooms = remember {
        listOf(
            RoomPost("1", "Phòng trọ cao cấp ban công thoáng mát gần Đại học", "3.500.000 đ", 25.0, android.R.drawable.ic_menu_gallery, RoomStatus.ACTIVE),
            RoomPost("2", "Phòng full nội thất, giờ giấc tự do quận Hải Châu", "4.200.000 đ", 30.0, android.R.drawable.ic_menu_gallery, RoomStatus.ACTIVE),
            RoomPost("3", "Chung cư mini giá rẻ cho sinh viên", "2.800.000 đ", 20.0, android.R.drawable.ic_menu_gallery, RoomStatus.RENTED),
            RoomPost("4", "Kí túc xá tiện nghi máy lạnh, giường tầng", "1.500.000 đ", 45.0, android.R.drawable.ic_menu_gallery, RoomStatus.PENDING)
        )
    }

    // Lọc danh sách theo Tab đang chọn
    val filteredRooms = mockRooms.filter { it.status == selectedStatus }

    Scaffold(
        topBar = {
            // Tận dụng CommonTopAppBar có sẵn trong thư mục components của bạn
            CommonTopAppBar(title = "Quản lý phòng trọ")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- THANH TRẠNG THÁI PHÒNG (TAB NGANG) ---
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // --- DANH SÁCH BÀI ĐĂNG ---
            if (filteredRooms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có phòng nào ở trạng thái này.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Chỉnh sửa: Dùng màu hệ thống hỗ trợ Dark Mode
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredRooms, key = { it.id }) { room ->
                        RoomPostCard(
                            room = room,
                            onEditClick = { onEditClick(room) },
                            onDeleteClick = { onDeleteClick(room) },
                            onHideClick = { onHideClick(room) }
                        )
                    }
                }
            }
        }
    }
}

// --- THIẾT KẾ CARD HIỂN THỊ TỪNG PHÒNG TRỌ ---
@Composable
fun RoomPostCard(
    room: RoomPost,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onHideClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // Kế thừa RoundedCornerShape(12.dp) từ Shape.kt của bạn
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Phần thông tin tóm tắt (Ảnh + Text)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Ảnh đại diện phòng trọ
                Image(
                    painter = painterResource(id = room.imageUrl),
                    contentDescription = "Room Image",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(MaterialTheme.shapes.medium) // Dùng bo góc medium đồng bộ
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Nội dung text thông tin phòng
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Tiêu đề bài đăng
                    Text(
                        text = room.title,
                        style = MaterialTheme.typography.titleLarge, // fontSize = 18.sp từ Type.kt của bạn
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Giá thuê
                    Text(
                        text = room.price,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary // Dùng OrangePrimary làm điểm nhấn giá tiền
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Diện tích
                    Text(
                        text = "Diện tích: ${room.area} m²",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Chỉnh sửa: Dùng màu hệ thống hỗ trợ Dark Mode
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            // --- CÁC NÚT CHỨC NĂNG NHANH ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút Ẩn Tin
                TextButton(
                    onClick = onHideClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 8.dp) // Chỉnh sửa: Thu nhỏ padding để tránh tràn hàng
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Hide",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Ẩn tin", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(4.dp)) // Chỉnh sửa: Giảm khoảng cách giữa các nút

                // Nút Sửa
                OutlinedButton(
                    onClick = onEditClick,
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), // Chỉnh sửa: Tối ưu không gian
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

                Spacer(modifier = Modifier.width(4.dp)) // Chỉnh sửa: Giảm khoảng cách giữa các nút

                // Nút Xóa
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

// Chỉnh sửa: Thêm Preview để xem trước giao diện trong Android Studio
@Preview(showBackground = true)
@Composable
fun RoomManagementScreenPreview() {
    EzRoomTheme {
        RoomManagementScreen()
    }
}