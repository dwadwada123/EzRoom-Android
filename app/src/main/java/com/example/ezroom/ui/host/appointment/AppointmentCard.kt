package com.example.ezroom.ui.host.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.*

@Composable
fun HostAppointmentCard(
    roomName: String,
    renterName: String,
    date: String,
    time: String,
    note: String,
    status: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (status) {
        "Approved" -> TealAccent
        "Canceled" -> Color.Red
        else -> OrangePrimary
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, OnBackgroundLight.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = roomName, fontSize = 14.sp, color = OrangePrimary, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = renterName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackgroundLight)

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = OnBackgroundLight.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$date | $time", fontSize = 14.sp, color = OnBackgroundLight)
            }

            if (note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Khách nhắn: \"$note\"", fontSize = 14.sp, color = OnBackgroundLight.copy(alpha = 0.6f))
            }

            // Chỉ hiện nút bấm tương tác nhanh khi trạng thái là Chờ duyệt (Pending)
            if (status == "Pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Từ chối", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Text("Xác nhận", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                // Hiển thị trạng thái bằng chữ nếu đã xử lý xong
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if(status == "Approved") "✓ Đã đồng ý cho xem phòng" else "✕ Đã từ chối",
                    color = statusColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentCardBasePreview() {
    EzRoomTheme {
        HostAppointmentCard(
            roomName = "Phòng trọ cao cấp Q7",
            renterName = "Trần Lê Quốc Dũng",
            date = "20/05/2024",
            time = "14:00",
            note = "Em qua xem phòng",
            status = "Pending",
            onAccept = {},
            onReject = {}
        )
    }
}
