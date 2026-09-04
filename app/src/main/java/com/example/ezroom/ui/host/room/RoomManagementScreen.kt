package com.example.ezroom.ui.host.room

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.host.components.RenterReviewDialog
import com.example.ezroom.ui.navigation.LocalSnackbarProvider
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// UI Component: Room Management Dashboard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    onRoomClick: (String) -> Unit = {},
    onAddRoomClick: (Property) -> Unit = {},
    onCloneRoomClick: (Room) -> Unit = {},
    onAddPropertyClick: () -> Unit = {},
    onAddStandaloneRoomClick: () -> Unit = {},
    onEditPropertyClick: (Property) -> Unit = {},
    viewModel: RoomManagementViewModel = viewModel(
        factory = viewModelFactory {
            val repo = RoomRepositoryImpl()
            RoomManagementViewModel(
                GetRoomsUseCase(repo),
                GetPropertiesUseCase(repo),
                TogglePropertyVisibilityUseCase(repo),
                DeletePropertyUseCase(repo),
                ToggleRoomVisibilityUseCase(repo),
                DeleteRoomUseCase(repo),
                SubmitAppealUseCase(repo)
            )
        }
    )
) {
    // State Management: UI State from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    var showAddOptions by remember { mutableStateOf(false) }
    
    // State Management: Deletion state
    var propertyToDelete by remember { mutableStateOf<Property?>(null) }
    var roomToDelete by remember { mutableStateOf<Room?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // State Management: Removal & Appeal
    var roomForReason by remember { mutableStateOf<Room?>(null) }
    var showRemovalReason by remember { mutableStateOf(false) }
    var showAppealForm by remember { mutableStateOf(false) }

    val showSnackbar = LocalSnackbarProvider.current
    
    var showRenterReview by remember { mutableStateOf(false) }
    var renterNameToReview by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // UI Component: Tabs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
            ) {
                TabRow(
                    selectedTabIndex = uiState.selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = PrimaryMain,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTabIndex]),
                            color = PrimaryMain,
                        )
                    },
                    divider = {} 
                ) {
                    Tab(
                        selected = uiState.selectedTabIndex == 0,
                        onClick = { viewModel.onTabSelected(0) },
                        text = { Text("Quản lý dãy", fontWeight = FontWeight.Bold) },
                    )
                    Tab(
                        selected = uiState.selectedTabIndex == 1,
                        onClick = { viewModel.onTabSelected(1) },
                        text = { Text("Phòng lẻ", fontWeight = FontWeight.Bold) },
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryMain)
                }
            } else if (uiState.selectedTabIndex == 0) {
                // UI Component: Complex Properties List
                val complexProperties = uiState.properties.filter { it.type == PropertyType.COMPLEX }
                if (complexProperties.isEmpty()) {
                    EmptyState("Bạn chưa có dãy nào", icon = Icons.Default.Apartment)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(complexProperties, key = { _, it -> it.id }) { index, property ->
                            var isVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { isVisible = true }
                            
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { 100 },
                                    animationSpec = tween(durationMillis = 400, delayMillis = index * 100)
                                ) + fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = index * 100))
                            ) {
                                val propertyRooms = uiState.rooms.filter { it.propertyId == property.id }
                                PropertyManagementCard(
                                    property = property,
                                    rooms = propertyRooms,
                                    onRoomClick = { id -> 
                                        val room = propertyRooms.find { it.id == id }
                                        if (room?.status == RoomStatus.REMOVED) {
                                            roomForReason = room
                                            showRemovalReason = true
                                        } else {
                                            onRoomClick(id)
                                        }
                                    },
                                    onCloneRoom = onCloneRoomClick,
                                    onAddRoom = { onAddRoomClick(property) },
                                    onEditProperty = onEditPropertyClick,
                                    onDeleteProperty = { 
                                        propertyToDelete = it
                                        showDeleteConfirmation = true
                                    },
                                    onToggleVisibility = { viewModel.onTogglePropertyVisibility(property.id) },
                                    onDeleteRoom = { id -> 
                                        roomToDelete = propertyRooms.find { it.id == id }
                                        showDeleteConfirmation = true
                                    },
                                    onToggleRoomVisibility = { viewModel.onToggleRoomVisibility(it) }
                                )
                            }
                        }
                    }
                }
            } else {
                // UI Component: Standalone Rooms List
                val standaloneRooms = uiState.rooms.filter { room ->
                    val prop = uiState.properties.find { it.id == room.propertyId }
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
                        itemsIndexed(standaloneRooms, key = { _, it -> it.id }) { index, room ->
                            var isVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { isVisible = true }

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { 100 },
                                    animationSpec = tween(durationMillis = 400, delayMillis = index * 100)
                                ) + fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = index * 100))
                            ) {
                                StandaloneRoomCard(
                                    room = room,
                                    onClick = { 
                                        if (room.status == RoomStatus.REMOVED) {
                                            roomForReason = room
                                            showRemovalReason = true
                                        } else {
                                            onRoomClick(room.id)
                                        }
                                    },
                                    onClone = { onCloneRoomClick(room) },
                                    onToggleVisibility = { viewModel.onToggleRoomVisibility(room.id) },
                                    onDelete = {
                                        roomToDelete = room
                                        showDeleteConfirmation = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // UI Component: Dialogs for Removal Reason and Appeal
        if (showRemovalReason && roomForReason != null) {
            RemovalReasonDialog(
                room = roomForReason!!,
                onDismiss = { showRemovalReason = false },
                onAppeal = { 
                    showRemovalReason = false
                    showAppealForm = true
                }
            )
        }

        if (showAppealForm && roomForReason != null) {
            AppealFormDialog(
                room = roomForReason!!,
                onDismiss = { showAppealForm = false },
                onSubmit = { text, images ->
                    viewModel.onSubmitAppeal(roomForReason!!.id, text, images)
                    showAppealForm = false
                    showSnackbar("Đã gửi kháng cáo thành công. Vui lòng chờ Admin xem xét.")
                }
            )
        }

        // UI Component: Deletion Dialog
        if (showDeleteConfirmation && (propertyToDelete != null || roomToDelete != null)) {
            val title = if (propertyToDelete != null) "Xác nhận xóa dãy trọ" else "Xác nhận xóa phòng"
            val text = if (propertyToDelete != null) 
                "Bạn có chắc chắn muốn xóa dãy '${propertyToDelete?.name}'? Toàn bộ các phòng bên trong cũng sẽ bị xóa."
                else "Bạn có chắc chắn muốn xóa phòng '${roomToDelete?.title}' không?"

            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(title, fontWeight = FontWeight.Bold) },
                text = { Text(text) },
                confirmButton = {
                    Button(
                        onClick = {
                            if (propertyToDelete != null) {
                                viewModel.onDeleteProperty(propertyToDelete!!.id)
                                showSnackbar("Đã xóa dãy trọ thành công")
                                propertyToDelete = null
                            } else if (roomToDelete != null) {
                                viewModel.onDeleteRoom(roomToDelete!!.id)
                                showSnackbar("Đã xóa phòng thành công")
                                roomToDelete = null
                            }
                            showDeleteConfirmation = false
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
                containerColor = Color.White
            )
        }

        // UI Component: Floating Action Button (FAB) for Adding Room / Property
        FloatingActionButton(
            onClick = { showAddOptions = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            containerColor = PrimaryMain,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm mới", modifier = Modifier.size(28.dp))
        }

        // UI Component: Add Options BottomSheet
        if (showAddOptions) {
            ModalBottomSheet(
                onDismissRequest = { showAddOptions = false },
                containerColor = Color.White,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tùy chọn thêm mới",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    AddOptionItem(
                        title = "Thêm dãy trọ / Tòa nhà",
                        desc = "Quản lý tòa nhà, dãy phòng có chung địa chỉ & tiện ích",
                        icon = Icons.Default.Apartment,
                        color = PrimaryMain,
                        onClick = {
                            showAddOptions = false
                            onAddPropertyClick()
                        }
                    )

                    AddOptionItem(
                        title = "Thêm phòng trọ lẻ",
                        desc = "Đăng phòng độc lập không thuộc tòa nhà hay dãy trọ",
                        icon = Icons.Default.MeetingRoom,
                        color = Color(0xFF0284C7),
                        onClick = {
                            showAddOptions = false
                            onAddStandaloneRoomClick()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// UI Component: Add Option Item
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

// UI Component: Property Management Card
@Composable
fun PropertyManagementCard(
    property: Property,
    rooms: List<Room>,
    onRoomClick: (String) -> Unit,
    onCloneRoom: (Room) -> Unit,
    onAddRoom: () -> Unit,
    onEditProperty: (Property) -> Unit,
    onDeleteProperty: (Property) -> Unit,
    onToggleVisibility: () -> Unit,
    onDeleteRoom: (String) -> Unit,
    onToggleRoomVisibility: (String) -> Unit
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
                            onClick = onToggleVisibility, 
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = if (property.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility, 
                                contentDescription = "Ẩn/Hiện dãy", 
                                tint = Neutral500, 
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        IconButton(onClick = { onEditProperty(property) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, "Sửa dãy", tint = PrimaryMain, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { onDeleteProperty(property) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, "Xóa dãy", tint = ErrorRose, modifier = Modifier.size(18.dp))
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
                            onDelete = { onDeleteRoom(room.id) },
                            onToggleVisibility = { onToggleRoomVisibility(room.id) }
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

// UI Component: Standalone Room Card
@Composable
fun StandaloneRoomCard(
    room: Room,
    onClick: () -> Unit,
    onClone: () -> Unit,
    onToggleVisibility: () -> Unit,
    onDelete: () -> Unit
) {
    val isDimmed = room.isUserHidden || room.status == RoomStatus.PENDING || room.status == RoomStatus.REMOVED
    val canClone = room.status != RoomStatus.REMOVED

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().alpha(if (isDimmed) 0.6f else 1.0f),
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
                    StatusBadge(status = room.status, isUserHidden = room.isUserHidden, removalInfo = room.removalInfo)
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
            
            // State Management: Action Buttons
            Row(modifier = Modifier.padding(start = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                if (canClone) {
                    IconButton(onClick = onClone, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, "Sao chép", modifier = Modifier.size(18.dp), tint = PrimaryMain)
                    }
                }

                if (room.status != RoomStatus.PENDING && room.status != RoomStatus.REMOVED) {
                    IconButton(
                        onClick = onToggleVisibility, 
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = if (room.isUserHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp), 
                            tint = Neutral500,
                        )
                    }
                }
                
                if (room.status != RoomStatus.RENTED) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "Xóa phòng", modifier = Modifier.size(18.dp), tint = ErrorRose)
                    }
                }
            }
        }
    }
}

// UI Component: Room Row Item
@Composable
fun RoomRowItem(
    room: Room, 
    onClick: () -> Unit, 
    onClone: () -> Unit,
    onDelete: () -> Unit,
    onToggleVisibility: () -> Unit
) {
    val isDimmed = room.isUserHidden || room.status == RoomStatus.PENDING || room.status == RoomStatus.REMOVED
    val canClone = room.status != RoomStatus.REMOVED

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().alpha(if (isDimmed) 0.6f else 1.0f),
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
                    StatusBadge(status = room.status, isUserHidden = room.isUserHidden, removalInfo = room.removalInfo)
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
                if (canClone) {
                    IconButton(onClick = onClone, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, "Sao chép", modifier = Modifier.size(18.dp), tint = PrimaryMain)
                    }
                }

                if (room.status != RoomStatus.PENDING && room.status != RoomStatus.REMOVED) {
                    IconButton(
                        onClick = onToggleVisibility, 
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = if (room.isUserHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility, 
                            contentDescription = "Ẩn/Hiện phòng", 
                            modifier = Modifier.size(18.dp), 
                            tint = Neutral500,
                        )
                    }
                }
                
                if (room.status != RoomStatus.RENTED) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "Xóa phòng", modifier = Modifier.size(18.dp), tint = ErrorRose)
                    }
                }
            }
        }
    }
}

// UI Component: Status Badge
@Composable
fun StatusBadge(
    status: RoomStatus, 
    isUserHidden: Boolean = false,
    removalInfo: com.example.ezroom.domain.model.RoomRemovalInfo? = null
) {
    val isAppealPending = removalInfo?.appealStatus == "PENDING" && !removalInfo.appealText.isNullOrBlank()
    val (color, text) = when {
        isAppealPending -> Color(0xFFD97706) to "Đang chờ kháng cáo"
        isUserHidden -> Neutral500 to "Đã ẩn bài"
        status == RoomStatus.ACTIVE -> SuccessEmerald to "Đang hiển thị"
        status == RoomStatus.RENTED -> Neutral500 to "Đã cho thuê"
        status == RoomStatus.PENDING -> AccentAmber to "Chờ duyệt"
        status == RoomStatus.REMOVED -> ErrorRose to "Bị gỡ"
        else -> Neutral500 to "Không xác định"
    }
    Surface(color = color.copy(alpha = 0.12f), shape = CircleShape) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

// UI Component: Dialog showing removal reason
@Composable
fun RemovalReasonDialog(
    room: Room,
    onDismiss: () -> Unit,
    onAppeal: () -> Unit
) {
    val isAppealPending = room.removalInfo?.appealStatus == "PENDING" || (!room.removalInfo?.appealText.isNullOrBlank() && room.removalInfo?.appealStatus != "REJECTED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bài đăng bị gỡ", fontWeight = FontWeight.Bold, color = ErrorRose) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Phòng: ${room.title}", style = MaterialTheme.typography.titleSmall)
                
                Surface(
                    color = ErrorRose.copy(alpha = 0.05f),
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Lý do vi phạm:", style = MaterialTheme.typography.labelMedium, color = Neutral500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = room.removalInfo?.reason ?: "Vi phạm quy chuẩn cộng đồng.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                if (isAppealPending) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kháng cáo của bạn đang chờ Admin xem xét.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Text(
                    text = "Ngày bị gỡ: ${room.removalInfo?.removedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
                
                Text(
                    text = "Bài đăng sẽ bị xóa vĩnh viễn vào ngày ${room.removalInfo?.autoDeleteDate} nếu không có kháng cáo được chấp nhận.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRose,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAppeal,
                enabled = !isAppealPending,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain)
            ) {
                Text(if (isAppealPending) "Đang chờ duyệt" else "Kháng cáo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        },
        containerColor = Color.White
    )
}

// UI Component: Form for submitting an appeal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppealFormDialog(
    room: Room,
    onDismiss: () -> Unit,
    onSubmit: (String, List<String>) -> Unit
) {
    var appealText by remember { mutableStateOf("") }
    val proofImages = remember { mutableStateListOf<android.net.Uri>() }
    
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris -> proofImages.addAll(uris) }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gửi kháng cáo", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Hãy giải trình lý do bạn cho rằng bài đăng '${room.title}' bị gỡ chưa chính xác.", style = MaterialTheme.typography.bodySmall)
                
                CustomTextField(
                    value = appealText,
                    onValueChange = { appealText = it },
                    label = "Nội dung giải trình",
                    modifier = Modifier.height(120.dp),
                    singleLine = false
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Hình ảnh minh chứng (${proofImages.size})", style = MaterialTheme.typography.labelMedium)
                    
                    if (proofImages.isEmpty()) {
                        Surface(
                            onClick = { pickerLauncher.launch("image/*") },
                            shape = MaterialTheme.shapes.small,
                            color = Neutral50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300),
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AddPhotoAlternate, null, tint = Neutral500)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tải ảnh lên", color = Neutral500)
                                }
                            }
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(proofImages) { uri ->
                                Box(modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.small)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { proofImages.remove(uri) },
                                        modifier = Modifier.align(Alignment.TopEnd).size(20.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            item {
                                Surface(
                                    onClick = { pickerLauncher.launch("image/*") },
                                    modifier = Modifier.size(80.dp),
                                    shape = MaterialTheme.shapes.small,
                                    color = Neutral50,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, null, tint = Neutral500)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(appealText, proofImages.map { it.toString() }) },
                enabled = appealText.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
            ) {
                Text("Gửi đơn")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = Color.White
    )
}

// UI Component: Empty State
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
