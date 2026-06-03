package com.example.ezroom.ui.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

data class Appointment(
    val id: String,
    val roomName: String,
    val personName: String,
    val date: String,
    val time: String,
    val note: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen(
    isHost: Boolean = true,
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    val statusFilters = listOf("Pending", "Approved", "Canceled")

    val dummyData = remember {
        listOf(
            Appointment("1", "Phòng trọ cao cấp Q7", "Trần Lê Quốc Dũng", "20/05/2024", "14:00", "Em muốn qua xem phòng trực tiếp", "Pending"),
            Appointment("2", "Căn hộ Studio tiện nghi", "Trần Vũ Phong", "21/05/2024", "09:30", "", "Approved"),
            Appointment("3", "Phòng trọ giá rẻ", "Trần Tâm", "19/05/2024", "16:00", "Hẹn gặp ở đầu ngõ", "Canceled"),
            Appointment("4", "Phòng trọ cao cấp Q7", "Phạm Đức Anh Tài", "22/05/2024", "10:00", "Check inbox em nhé", "Pending")
        )
    }

    val filteredList = dummyData.filter { it.status == statusFilters[selectedTabIndex] }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "QUẢN LÝ LỊCH HẸN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SurfaceLight,
                contentColor = OrangePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = OrangePrimary
                    )
                },
                divider = { HorizontalDivider(color = OnBackgroundLight.copy(alpha = 0.1f)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) OrangePrimary else OnBackgroundLight.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Không có lịch hẹn nào",
                        color = OnBackgroundLight.copy(alpha = 0.4f),
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { item ->
                        AppointmentCard(
                            roomName = item.roomName,
                            renterName = item.personName,
                            date = item.date,
                            time = item.time,
                            note = item.note,
                            status = item.status,
                            isHost = isHost,
                            onAccept = { },
                            onReject = { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Host View")
@Composable
fun AppointmentListScreenHostPreview() {
    EzRoomTheme {
        AppointmentListScreen(
            isHost = true,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Renter View")
@Composable
fun AppointmentListScreenRenterPreview() {
    EzRoomTheme {
        AppointmentListScreen(
            isHost = false,
            onNavigateBack = {}
        )
    }
}