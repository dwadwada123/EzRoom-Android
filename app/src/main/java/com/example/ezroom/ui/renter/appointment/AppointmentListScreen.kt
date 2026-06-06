package com.example.ezroom.ui.renter.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

// Data class có thể để ở folder model dùng chung
data class Appointment(
    val id: String,
    val roomName: String,
    val date: String,
    val time: String,
    val note: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterAppointmentListScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    val statusFilters = listOf("Pending", "Approved", "Canceled")

    val dummyData = remember {
        listOf(
            Appointment("1", "Phòng trọ cao cấp Q7", "20/05/2024", "14:00", "Em muốn qua xem phòng", "Pending"),
            Appointment("2", "Căn hộ Studio tiện nghi", "21/05/2024", "09:30", "", "Approved")
        )
    }

    val filteredList = dummyData.filter { it.status == statusFilters[selectedTabIndex] }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("LỊCH HẸN CỦA TÔI", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = OrangePrimary) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SurfaceLight,
                contentColor = OrangePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = OrangePrimary)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title, fontSize = 14.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { item ->
                    RenterAppointmentCard(item)
                }
            }
        }
    }
}

@Composable
fun RenterAppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(appointment.roomName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Thời gian: ${appointment.time} - ${appointment.date}", fontSize = 13.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
            if (appointment.note.isNotEmpty()) {
                Text("Lời nhắn: ${appointment.note}", fontSize = 13.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RenterAppointmentListScreenPreview() {
    EzRoomTheme {
        RenterAppointmentListScreen(onNavigateBack = {})
    }
}