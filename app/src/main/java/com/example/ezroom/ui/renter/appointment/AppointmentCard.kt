package com.example.ezroom.ui.renter.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

@Composable
fun RenterAppointmentCard(
    roomName: String,
    date: String,
    time: String,
    note: String,
    status: String
) {
    val statusColor = when (status) {
        "Approved" -> TealAccent
        "Canceled" -> OrangeSecondary
        else -> OrangePrimary
    }

    val statusText = when (status) {
        "Approved" -> "Đã xác nhận"
        "Canceled" -> "Đã hủy"
        else -> "Chờ duyệt"
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, OnBackgroundLight.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = roomName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnBackgroundLight)
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = OnBackgroundLight.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$date | $time", fontSize = 14.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
            }

            if (note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Lời nhắn của tôi: \"$note\"", fontSize = 13.sp, color = OnBackgroundLight.copy(alpha = 0.8f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RenterAppointmentCardPreview() {
    EzRoomTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            RenterAppointmentCard(
                roomName = "Phòng trọ cao cấp Q7",
                date = "20/05/2024",
                time = "14:00",
                note = "Em muốn qua xem phòng",
                status = "Pending"
            )
            Spacer(modifier = Modifier.height(8.dp))
            RenterAppointmentCard(
                roomName = "Căn hộ Studio",
                date = "21/05/2024",
                time = "09:00",
                note = "",
                status = "Approved"
            )
            Spacer(modifier = Modifier.height(8.dp))
            RenterAppointmentCard(
                roomName = "Phòng trọ giá rẻ",
                date = "22/05/2024",
                time = "10:00",
                note = "Hủy do bận",
                status = "Canceled"
            )
        }
    }
}
