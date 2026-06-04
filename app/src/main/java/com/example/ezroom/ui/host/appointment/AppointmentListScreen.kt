package com.example.ezroom.ui.host.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

// Đã thêm personName vào đây để fix lỗi constructor và lỗi Unresolved reference
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
fun HostAppointmentListScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    val statusFilters = listOf("Pending", "Approved", "Canceled")

    // Dữ liệu giả lập chạy mượt mà với 7 tham số
    val dummyData = remember {
        listOf(
            Appointment("1", "Phòng trọ cao cấp Q7", "Trần Lê Quốc Dũng", "20/05/2024", "14:00", "Em qua xem phòng", "Pending"),
            Appointment("4", "Phòng trọ cao cấp Q7", "Phạm Đức Anh Tài", "22/05/2024", "10:00", "Check inbox", "Pending")
        )
    }

    val filteredList = dummyData.filter { it.status == statusFilters[selectedTabIndex] }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("DUYỆT LỊCH XEM PHÒNG", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary) },
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
                    HostAppointmentCard(
                        appointment = item,
                        showActions = selectedTabIndex == 0 // Chỉ hiện nút bấm khi ở Tab "Chờ duyệt"
                    )
                }
            }
        }
    }
}

@Composable
fun HostAppointmentCard(
    appointment: Appointment,
    showActions: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Khách: ${appointment.personName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Tại: ${appointment.roomName}", fontSize = 13.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
            Text("Lúc: ${appointment.time} - ${appointment.date}", fontSize = 13.sp, color = OnBackgroundLight.copy(alpha = 0.6f))

            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nút Từ chối (Đỏ)
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Từ chối", fontSize = 13.sp)
                    }

                    // Nút Xác nhận (Xanh)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xác nhận", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentListScreenPreview() {
    EzRoomTheme {
        HostAppointmentListScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentCardPreview() {
    EzRoomTheme {
        HostAppointmentCard(
            appointment = Appointment(
                id = "1",
                roomName = "Phòng trọ cao cấp Q7",
                personName = "Trần Lê Quốc Dũng",
                date = "20/05/2024",
                time = "14:00",
                note = "Em qua xem phòng",
                status = "Pending"
            ),
            showActions = true
        )
    }
}