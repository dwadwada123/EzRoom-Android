package com.example.ezroom.ui.host.room

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ezroom.domain.model.*
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.host.components.RenterReviewDialog
import com.example.ezroom.ui.navigation.LocalSnackbarProvider
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    onRoomClick: (String) -> Unit = {},
    onAddRoomClick: (Property) -> Unit = {},
    onCloneRoomClick: (Room) -> Unit = {},
    onAddPropertyClick: () -> Unit = {},
    onAddStandaloneRoomClick: () -> Unit = {},
    onEditPropertyClick: (Property) -> Unit = {},
) {
    val properties = MockData.properties
    val allRooms = MockData.rooms
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddOptions by remember { mutableStateOf(false) }
    
    // Deletion state
    var propertyToDelete by remember { mutableStateOf<Property?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val showSnackbar = LocalSnackbarProvider.current
    
    var showRenterReview by remember { mutableStateOf(false) }
    var renterNameToReview by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs - Aligned directly at the top of the screen content
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = PrimaryMain,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryMain,
                        )
                    },
                    divider = {} // Remove default divider for cleaner look
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Quản lý dãy", fontWeight = FontWeight.Bold) },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Phòng lẻ", fontWeight = FontWeight.Bold) },
                    )
                }
            }

            if (selectedTab == 0) {
                val complexProperties = properties.filter { it.type == PropertyType.COMPLEX }
                if (complexProperties.isEmpty()) {
                    EmptyState("Bạn chưa có dãy nào", icon = Icons.Default.Apartment)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(complexProperties, key = { it.id }) { property ->
                            val propertyRooms = allRooms.filter { it.propertyId == property.id }
                            PropertyManagementCard(
                                property = property,
                                rooms = propertyRooms,
                                onRoomClick = onRoomClick,
                                onCloneRoom = onCloneRoomClick,
                                onAddRoom = { onAddRoomClick(property) },
                                onEditProperty = onEditPropertyClick,
                                onDeleteProperty = { 
                                    propertyToDelete = it
                                    showDeleteConfirmation = true
                                },
                            )
                        }
                    }
                }
            } else {
                val standaloneRooms = allRooms.filter { room ->
                    val prop = properties.find { it.id == room.propertyId }
                    prop?.type == PropertyType.SINGLE || room.propertyId == null
                }
                
                if (standaloneRooms.isEmpty()) {
                    EmptyState("Bạn chưa có phòng lẻ nào", icon = Icons.Default.Home)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(standaloneRooms, key = { it.id }) { room ->
                            StandaloneRoomCard(
                                room = room,
                                onClick = { onRoomClick(room.id) },
                                onClone = { onCloneRoomClick(room) }
                            )
                        }
                    }
                }
            }
        }

        // Add Button (FAB)
        FloatingActionButton(
            onClick = { showAddOptions = true },
            containerColor = PrimaryMain,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp)
                .zIndex(5f)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm")
        }

        // Add Dialog
        if (showAddOptions) {
            AlertDialog(
                onDismissRequest = { showAddOptions = false },
                title = { Text("Bạn muốn đăng tin gì?", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AddOptionItem(
                            title = "Tạo dãy / Tòa nhà",
                            desc = "Quản lý nhiều phòng tại một địa chỉ",
                            icon = Icons.Default.Apartment,
                            color = PrimaryMain,
                            onClick = {
                                showAddOptions = false
                                onAddPropertyClick()
                            },
                        )
                        AddOptionItem(
                            title = "Đăng phòng lẻ",
                            desc = "Nhà nguyên căn hoặc phòng cho thuê lẻ",
                            icon = Icons.Default.Home,
                            color = AccentTeal,
                            onClick = {
                                showAddOptions = false
                                onAddStandaloneRoomClick()
                            },
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAddOptions = false }) {
                        Text("Đóng")
                    }
                },
                containerColor = Color.White,
                shape = MaterialTheme.shapes.medium,
            )
        }

        // Review Dialog
        if (showRenterReview) {
            RenterReviewDialog(
                renterName = renterNameToReview,
                onDismiss = { showRenterReview = false },
                onSubmit = { _, _, _ -> showRenterReview = false },
            )
        }

        // Property Delete Confirmation
        if (showDeleteConfirmation && propertyToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Xác nhận xóa dãy trọ", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn xóa dãy '${propertyToDelete?.name}'? Toàn bộ các phòng bên trong cũng sẽ bị xóa.") },
                confirmButton = {
                    Button(
                        onClick = {
                            val propertyId = propertyToDelete?.id
                            val index = MockData.properties.indexOfFirst { it.id == propertyId }
                            if (index != -1) {
                                MockData.properties.removeAt(index)
                                MockData.rooms.removeAll { it.propertyId == propertyId }
                                showSnackbar("Đã xóa dãy trọ thành công")
                            }
                            showDeleteConfirmation = false
                            propertyToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White,
            )
        }
    }
}

@Composable
fun AddOptionItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(color, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }
        }
    }
}

@Composable
fun PropertyManagementCard(
    property: Property,
    rooms: List<Room>,
    onRoomClick: (String) -> Unit,
    onCloneRoom: (Room) -> Unit,
    onAddRoom: () -> Unit,
    onEditProperty: (Property) -> Unit,
    onDeleteProperty: (Property) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(value = false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (property.isHidden) 0.6f else 1.0f),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300),
        shadowElevation = 2.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(PrimaryLight, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Apartment, null, tint = PrimaryMain)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.name, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = property.address, 
                        style = MaterialTheme.typography.bodySmall, 
                        color = Neutral500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { 
                                val index = MockData.properties.indexOfFirst { it.id == property.id }
                                if (index != -1) {
                                    val current = MockData.properties[index]
                                    MockData.properties[index] = current.copy(isHidden = !current.isHidden)
                                }
                            }, 
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                imageVector = if (property.isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                                contentDescription = "Ẩn/Hiện dãy", 
                                tint = Neutral500, 
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        IconButton(onClick = { onEditProperty(property) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, "Sửa dãy", tint = PrimaryMain, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { onDeleteProperty(property) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, "Xóa dãy", tint = ErrorRose, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.clickable { isExpanded = !isExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "${rooms.size} phòng", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryMain)
                        Icon(imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = Neutral300)
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.background(Neutral50).padding(12.dp)) {
                    rooms.forEach { room ->
                        RoomRowItem(
                            room = room,
                            onClick = { onRoomClick(room.id) },
                            onClone = { onCloneRoom(room) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    OutlinedButton(
                        onClick = onAddRoom,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Thêm phòng mới vào dãy", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun StandaloneRoomCard(
    room: Room,
    onClick: () -> Unit,
    onClone: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().alpha(if (room.isUserHidden) 0.6f else 1.0f),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFF0FDFA), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Home, null, tint = AccentTeal)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.title, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = room.status, isUserHidden = room.isUserHidden)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.priceFormatted, 
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), 
                        color = PrimaryMain, 
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
            
            // Action Buttons
            Row(modifier = Modifier.padding(start = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClone, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ContentCopy, "Sao chép", modifier = Modifier.size(18.dp), tint = PrimaryMain)
                }

                if (room.status != RoomStatus.PENDING) {
                    IconButton(
                        onClick = { 
                            val index = MockData.rooms.indexOfFirst { it.id == room.id }
                            if (index != -1) {
                                val current = MockData.rooms[index]
                                MockData.rooms[index] = current.copy(isUserHidden = !current.isUserHidden)
                            }
                        }, 
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = if (room.isUserHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp), 
                            tint = Neutral500,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoomRowItem(
    room: Room, 
    onClick: () -> Unit, 
    onClone: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().alpha(if (room.isUserHidden) 0.6f else 1.0f),
        shape = MaterialTheme.shapes.small,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.title, 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = room.status, isUserHidden = room.isUserHidden)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.priceFormatted, 
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), 
                        color = PrimaryMain, 
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
            Row(modifier = Modifier.padding(start = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClone, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ContentCopy, "Sao chép", modifier = Modifier.size(18.dp), tint = PrimaryMain)
                }

                if (room.status != RoomStatus.PENDING) {
                    IconButton(
                        onClick = { 
                            val index = MockData.rooms.indexOfFirst { it.id == room.id }
                            if (index != -1) {
                                val current = MockData.rooms[index]
                                MockData.rooms[index] = current.copy(isUserHidden = !current.isUserHidden)
                            }
                        }, 
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = if (room.isUserHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp), 
                            tint = Neutral500,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: RoomStatus, isUserHidden: Boolean = false) {
    val (color, text) = when {
        isUserHidden -> Neutral500 to "Đã ẩn bài"
        status == RoomStatus.ACTIVE -> SuccessEmerald to "Đang hiển thị"
        status == RoomStatus.RENTED -> Neutral500 to "Đã cho thuê"
        status == RoomStatus.PENDING -> AccentAmber to "Chờ duyệt"
        else -> Neutral500 to "Không xác định"
    }
    Surface(color = color.copy(alpha = 0.1f), shape = CircleShape) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyState(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(64.dp), tint = Neutral300.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Neutral500)
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
