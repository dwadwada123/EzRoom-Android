package com.example.ezroom.ui.host.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.theme.*

data class HostStats(
    val totalRooms: Int,
    val vacantRooms: Int,
    val rentedRooms: Int,
    val expectedRevenue: String,
    val totalAppointments: Int,
    val occupancyRate: Float
)

@Composable
fun HostDashboardScreen(
    onCreateContract: () -> Unit = {}
) {
    // State definitions
    val rooms = remember { MockData.rooms }
    val appointments = remember { MockData.appointments }
    val stats = remember(rooms, appointments) {
        val total = rooms.size
        val rented = rooms.count { it.status == com.example.ezroom.data.model.RoomStatus.RENTED }
        val vacant = total - rented
        HostStats(
            totalRooms = total,
            vacantRooms = vacant,
            rentedRooms = rented,
            expectedRevenue = "2.000.000 VND",
            totalAppointments = appointments.size,
            occupancyRate = if (total > 0) (rented.toFloat() / total) else 0f
        )
    }

    // Main layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats grid row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCardItem(
                title = "Tổng số phòng",
                value = stats.totalRooms.toString(),
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f),
                color = OrangePrimary
            )
            DashboardCardItem(
                title = "Phòng trống",
                value = stats.vacantRooms.toString(),
                icon = Icons.Default.MeetingRoom,
                modifier = Modifier.weight(1f),
                color = TealAccent
            )
        }

        // Stats grid row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCardItem(
                title = "Đã thuê",
                value = stats.rentedRooms.toString(),
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f),
                color = Color(0xFF2196F3)
            )
            DashboardCardItem(
                title = "Lịch hẹn",
                value = stats.totalAppointments.toString(),
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f),
                color = Color(0xFFFF9800)
            )
        }

        // Dashboard card section
        DashboardCardItem(
            title = "Doanh thu dự kiến",
            value = stats.expectedRevenue,
            icon = Icons.Default.Payments,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF4CAF50)
        )

        // Quick actions row
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Công cụ quản lý", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCreateContract,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tạo hợp đồng mới", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Progress section container
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tỷ lệ lấp đầy", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { stats.occupancyRate },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = OrangePrimary,
                    trackColor = OrangePrimary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(stats.occupancyRate * 100).toInt()}% diện tích đã được thuê",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackgroundLight.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun DashboardCardItem(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = OrangePrimary
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = OnBackgroundLight)
            Text(text = title, fontSize = 12.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostDashboardScreenPreview() {
    EzRoomTheme {
        HostDashboardScreen()
    }
}
