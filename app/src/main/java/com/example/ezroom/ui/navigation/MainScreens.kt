package com.example.ezroom.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.ezroom.domain.model.*
import com.example.ezroom.ui.chat.ChatListScreen
import com.example.ezroom.ui.host.appointment.HostAppointmentListScreen
import com.example.ezroom.ui.host.overview.HostDashboardScreen
import com.example.ezroom.ui.host.room.RoomManagementScreen
import com.example.ezroom.ui.renter.discovery.RenterHomeScreen
import com.example.ezroom.ui.renter.invoice.RenterInvoiceListScreen
import com.example.ezroom.ui.theme.EzRoomTheme

// Data Model: Navigation Item
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    // Renter Items
    object Discovery : BottomNavItem("Khám phá", Icons.Default.Search)
    object RenterSaved : BottomNavItem("Yêu thích", Icons.Default.Favorite)
    object RenterAppointments : BottomNavItem("Lịch hẹn", Icons.Default.DateRange)
    object RenterMessages : BottomNavItem("Tin nhắn", Icons.AutoMirrored.Filled.Chat)
    object RenterInvoices : BottomNavItem("Hóa đơn", Icons.AutoMirrored.Filled.ReceiptLong)

    // Host Items
    object Management : BottomNavItem("Quản lý", Icons.Default.Dashboard)
    object HostRooms : BottomNavItem("Phòng trọ", Icons.Default.HomeWork)
    object HostAppointments : BottomNavItem("Lịch hẹn", Icons.AutoMirrored.Filled.EventNote)
    object HostMessages : BottomNavItem("Tin nhắn", Icons.Default.QuestionAnswer)
    object HostInvoices : BottomNavItem("Hóa đơn", Icons.Default.Payments)
}

// UI Component: Main Renter Dashboard
@Composable
fun RenterMainScreen(
    onRoomClick: (String) -> Unit = {},
    onInvoiceClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onEditAppointment: (String, String) -> Unit = { _, _ -> },
    onNavigateToFilter: () -> Unit = {},
    onShowSnackbar: (String) -> Unit = {},
) {
    val items = listOf(
        BottomNavItem.Discovery,
        BottomNavItem.RenterSaved,
        BottomNavItem.RenterAppointments,
        BottomNavItem.RenterMessages,
        BottomNavItem.RenterInvoices
    )
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            // UI Component: Top Bar
            ModernMainTopBar(
                title = items[selectedItem].title,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(
                    targetState = items[selectedItem],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "MainContentTransition",
                ) { targetItem ->
                    when (targetItem) {
                        // Renter Action: Browse Rooms
                        BottomNavItem.Discovery -> RenterHomeScreen(
                            onRoomClick = { room -> onRoomClick(room.id) },
                            onNavigateToFilter = onNavigateToFilter,
                        )
                        // Renter Action: View Favorites
                        BottomNavItem.RenterSaved -> com.example.ezroom.ui.renter.favorite.SavedRoomsScreen(
                            onRoomClick = onRoomClick,
                            onNavigateToExplore = { selectedItem = 0 },
                            onShowSnackbar = onShowSnackbar,
                        )
                        // Renter Action: View Appointments
                        BottomNavItem.RenterAppointments -> com.example.ezroom.ui.renter.appointment.RenterAppointmentListScreen(
                            onNavigateBack = { selectedItem = 0 },
                            onEditAppointment = { appointment -> 
                                onEditAppointment(appointment.roomId, appointment.id)
                            }
                        )
                        // Renter Action: Messages
                        BottomNavItem.RenterMessages -> ChatListScreen(
                            onConversationClick = { _, userName -> onChatClick(userName) }
                        )
                        // Renter Action: Invoices
                        BottomNavItem.RenterInvoices -> RenterInvoiceListScreen(
                            onNavigateBack = { selectedItem = 0 },
                            onInvoiceClick = onInvoiceClick
                        )
                        else -> {}
                    }
                }
            }

            // UI Component: Floating Navigation Dock
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .zIndex(10f)
            ) {
                FloatingDockNavigationBar(
                    items = items,
                    selectedIndex = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            }
        }
    }
}

// UI Component: Main Host Dashboard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostMainScreen(
    onRoomClick: (String) -> Unit = {},
    onInvoiceClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCloneRoomClick: (String) -> Unit = {},
    onCreateContractClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onAddRoomToProperty: (String) -> Unit = {},
    onAddPropertyClick: () -> Unit = {},
    onAddStandaloneRoomClick: () -> Unit = {},
    onRenterReputationClick: (String) -> Unit = {},
    onEditPropertyClick: (String) -> Unit = {}
) {
    val items = listOf(
        BottomNavItem.Management,
        BottomNavItem.HostRooms,
        BottomNavItem.HostAppointments,
        BottomNavItem.HostMessages,
        BottomNavItem.HostInvoices
    )
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            // UI Component: Top Bar
            ModernMainTopBar(
                title = items[selectedItem].title,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(
                    targetState = items[selectedItem],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "HostContentTransition"
                ) { targetItem ->
                    when (targetItem) {
                        // Host Action: Stats Overview
                        BottomNavItem.Management -> HostDashboardScreen(
                            onCreateContract = onCreateContractClick
                        )
                        // Host Action: Room Management
                        BottomNavItem.HostRooms -> RoomManagementScreen(
                            onRoomClick = onRoomClick,
                            onCloneRoomClick = { room -> onCloneRoomClick(room.id) },
                            onAddRoomClick = { property -> onAddRoomToProperty(property.id) },
                            onAddPropertyClick = onAddPropertyClick,
                            onAddStandaloneRoomClick = onAddStandaloneRoomClick,
                            onEditPropertyClick = { property -> onEditPropertyClick(property.id) }
                        )
                        // Host Action: Appointments
                        BottomNavItem.HostAppointments -> HostAppointmentListScreen(
                            onNavigateBack = { selectedItem = 0 },
                            onCreateContract = onCreateContractClick,
                            onRenterClick = onRenterReputationClick
                        )
                        // Host Action: Messages
                        BottomNavItem.HostMessages -> ChatListScreen(
                            onConversationClick = { _, userName -> onChatClick(userName) }
                        )
                        // Host Action: Invoices
                        BottomNavItem.HostInvoices -> com.example.ezroom.ui.host.invoice.HostInvoiceListScreen(
                            onNavigateToCreate = { onInvoiceClick("create") },
                            onInvoiceClick = { invoiceId -> onInvoiceClick(invoiceId) }
                        )
                        else -> {}
                    }
                }
            }

            // UI Component: Floating Navigation Dock
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .zIndex(10f)
            ) {
                FloatingDockNavigationBar(
                    items = items,
                    selectedIndex = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            }
        }
    }
}

// UI Component: Modern Glassmorphic Top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernMainTopBar(
    title: String,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Surface(
        color = Color.White.copy(alpha = 0.95f), // Glassmorphism
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CenterAlignedTopAppBar(
            title = { 
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                ) 
            },
            navigationIcon = {
                // UI Component: Notifications
                IconButton(onClick = onNotificationClick) {
                    BadgedBox(badge = { Badge { Text("3") } }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                }
            },
            actions = {
                // UI Component: Profile
                IconButton(onClick = onProfileClick) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
        )
    }
}

// UI Component: Floating Navigation Dock Implementation
@Composable
fun FloatingDockNavigationBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    val density = LocalDensity.current
    
    // UI Logic: Layout weights
    val selectedWeight = 2.5f
    val unselectedWeight = 1f
    val totalWeight = selectedWeight + ((items.size - 1) * unselectedWeight)

    Surface(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .shadow(24.dp, shape = CircleShape, ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))),
        ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 6.dp)
                .fillMaxWidth(),
        ) {
            val constraints = this
            val maxWidthPx = with(density) { constraints.maxWidth.toPx() }
            
            // UI Logic: Animation position calculation
            val targetWidthPx = (selectedWeight / totalWeight) * maxWidthPx
            val targetOffsetPx = (selectedIndex * unselectedWeight / totalWeight) * maxWidthPx

            val animatedWidth by animateFloatAsState(
                targetValue = targetWidthPx,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "IndicatorWidth",
            )
            val animatedOffset by animateFloatAsState(
                targetValue = targetOffsetPx,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "IndicatorOffset",
            )

            // UI Component: Animated Indicator
            Box(
                modifier = Modifier
                    .offset(x = with(density) { animatedOffset.toDp() })
                    .width(with(density) { animatedWidth.toDp() })
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )

            // UI Component: Navigation Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) { 1.1f } else { 1.0f },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "IconScale"
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) { MaterialTheme.colorScheme.onPrimary } 
                                       else { MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) },
                        animationSpec = tween(300),
                        label = "ContentColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(if (isSelected) { selectedWeight } else { unselectedWeight })
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onItemSelected(index) }
                            )
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = contentColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer(scaleX = scale, scaleY = scale)
                            )
                            
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                                exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = contentColor,
                                    modifier = Modifier.padding(start = 4.dp),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
