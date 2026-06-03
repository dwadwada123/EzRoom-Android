package com.example.ezroom.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Renter Tabs
    object Explore : BottomNavItem("explore", "Khám phá", Icons.Default.Search)
    object RenterAppointments : BottomNavItem("renter_appointments", "Lịch hẹn", Icons.Default.DateRange)
    object RenterMessages : BottomNavItem("renter_messages", "Tin nhắn", Icons.AutoMirrored.Filled.Chat)
    object RenterProfile : BottomNavItem("renter_profile", "Cá nhân", Icons.Default.Person)

    // Host Tabs
    object Management : BottomNavItem("management", "Quản lý", Icons.Default.HomeWork)
    object HostAppointments : BottomNavItem("host_appointments", "Lịch hẹn", Icons.Default.Event)
    object HostMessages : BottomNavItem("host_messages", "Tin nhắn", Icons.Default.Email)
    object HostProfile : BottomNavItem("host_profile", "Cá nhân", Icons.Default.AccountCircle)
}

@Composable
fun RenterMainScreen() {
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.RenterAppointments,
        BottomNavItem.RenterMessages,
        BottomNavItem.RenterProfile
    )
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for actual screens
            PlaceholderContent(items[selectedItem].title)
        }
    }
}

@Composable
fun HostMainScreen() {
    val items = listOf(
        BottomNavItem.Management,
        BottomNavItem.HostAppointments,
        BottomNavItem.HostMessages,
        BottomNavItem.HostProfile
    )
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for actual screens
            PlaceholderContent(items[selectedItem].title)
        }
    }
}

@Composable
fun EzBottomNavigationBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun PlaceholderContent(title: String) {
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
