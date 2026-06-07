package com.example.ezroom.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.renter.discovery.RenterHomeScreen
import com.example.ezroom.ui.renter.favorite.SavedRoomsScreen
import com.example.ezroom.ui.renter.appointment.RenterAppointmentListScreen
import com.example.ezroom.ui.renter.invoice.RenterInvoiceListScreen
import com.example.ezroom.ui.chat.ChatListScreen
import com.example.ezroom.ui.host.appointment.HostAppointmentListScreen
import com.example.ezroom.ui.host.overview.HostDashboardScreen
import com.example.ezroom.ui.host.room.RoomManagementScreen
import com.example.ezroom.ui.theme.OrangePrimary

// Navigation routing
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Explore : BottomNavItem("explore", "Khám phá", Icons.Default.Search)
    object Favorite : BottomNavItem("favorite", "Yêu thích", Icons.Default.FavoriteBorder)
    object RenterAppointments : BottomNavItem("renter_appointments", "Lịch hẹn", Icons.Default.DateRange)
    object RenterMessages : BottomNavItem("renter_messages", "Tin nhắn", Icons.AutoMirrored.Filled.Chat)
    object RenterInvoices : BottomNavItem("renter_invoices", "Hóa đơn", Icons.Default.ReceiptLong)

    object Management : BottomNavItem("management", "Quản lý", Icons.Default.HomeWork)
    object HostRooms : BottomNavItem("host_rooms", "Phòng", Icons.Default.Bed)
    object HostAppointments : BottomNavItem("host_appointments", "Lịch hẹn", Icons.Default.Event)
    object HostMessages : BottomNavItem("host_messages", "Tin nhắn", Icons.Default.Email)
    object HostInvoices : BottomNavItem("host_invoices", "Hóa đơn", Icons.Default.ReceiptLong)
    object HostProfile : BottomNavItem("host_profile", "Cá nhân", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterMainScreen(
    // Event callbacks
    onRoomClick: (String) -> Unit = {},
    onInvoiceClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onEditAppointment: (String, String) -> Unit = { _, _ -> }
) {
    // State definitions
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.Favorite,
        BottomNavItem.RenterAppointments,
        BottomNavItem.RenterMessages,
        BottomNavItem.RenterInvoices
    )
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    // Main layout container
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = items[selectedItem].title.uppercase(), 
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = OrangePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = OrangePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            EzBottomNavigationBar(
                items = items,
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (items[selectedItem]) {
                BottomNavItem.Explore -> RenterHomeScreen(
                    onRoomClick = { roomItem -> onRoomClick(roomItem.id) }
                )
                BottomNavItem.Favorite -> SavedRoomsScreen(
                    onRoomClick = onRoomClick
                )
                BottomNavItem.RenterAppointments -> RenterAppointmentListScreen(
                    onNavigateBack = { selectedItem = 0 },
                    onEditAppointment = { appt -> onEditAppointment(appt.roomId, appt.id) }
                )
                BottomNavItem.RenterMessages -> ChatListScreen(
                    onNavigateBack = { selectedItem = 0 },
                    onConversationClick = { _, userName -> 
                        onChatClick(userName)
                    }
                )
                BottomNavItem.RenterInvoices -> RenterInvoiceListScreen(
                    onNavigateBack = { selectedItem = 0 },
                    onInvoiceClick = onInvoiceClick
                )
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostMainScreen(
    // Event callbacks
    onRoomClick: (String) -> Unit = {},
    onInvoiceClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onFabClick: () -> Unit = {},
    onEditRoomClick: (String) -> Unit = {},
    onCreateContractClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {
    // State definitions
    val items = listOf(
        BottomNavItem.Management,
        BottomNavItem.HostRooms,
        BottomNavItem.HostAppointments,
        BottomNavItem.HostMessages,
        BottomNavItem.HostInvoices
    )
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    // Main layout container
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = items[selectedItem].title.uppercase(), 
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = OrangePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = OrangePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            EzBottomNavigationBar(
                items = items,
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        },
        floatingActionButton = {
            // FAB conditional rendering
            when (items[selectedItem]) {
                BottomNavItem.HostRooms -> {
                    FloatingActionButton(
                        onClick = onFabClick,
                        containerColor = OrangePrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm phòng")
                    }
                }
                BottomNavItem.HostInvoices -> {
                    FloatingActionButton(
                        onClick = { onInvoiceClick("create") },
                        containerColor = OrangePrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = "Lập hóa đơn")
                    }
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (items[selectedItem]) {
                BottomNavItem.Management -> HostDashboardScreen(
                    onCreateContract = onCreateContractClick
                )
                BottomNavItem.HostRooms -> RoomManagementScreen(
                    onRoomClick = onRoomClick,
                    onEditClick = { room -> onEditRoomClick(room.id) }
                )
                BottomNavItem.HostAppointments -> HostAppointmentListScreen(
                    onNavigateBack = { selectedItem = 0 },
                    onCreateContract = onCreateContractClick
                )
                BottomNavItem.HostMessages -> ChatListScreen(
                    onNavigateBack = { selectedItem = 0 },
                    onConversationClick = { _, userName -> 
                        onChatClick(userName)
                    }
                )
                BottomNavItem.HostInvoices -> com.example.ezroom.ui.host.invoice.HostInvoiceListScreen(
                    onNavigateToCreate = { onInvoiceClick("create") },
                    onInvoiceClick = { invoiceId -> onInvoiceClick(invoiceId) }
                )
                else -> {}
            }
        }
    }
}

@Composable
fun EzBottomNavigationBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    // Main layout container
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { 
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        maxLines = 1,
                        softWrap = false
                    ) 
                },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    selectedTextColor = OrangePrimary,
                    indicatorColor = OrangePrimary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun PlaceholderContent(title: String) {
    // Main layout container
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
