package com.example.ezroom.ui.host.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.Appointment
import com.example.ezroom.data.model.AppointmentStatus
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*

@Composable
fun HostAppointmentCard(
    // Event callbacks
    appointment: Appointment,
    showActions: Boolean,
    onAction: (AppointmentStatus) -> Unit = {},
    onCreateContract: () -> Unit = {}
) {
    // State definitions derived from appointment
    val statusText = when (appointment.status) {
        AppointmentStatus.PENDING -> "Chờ duyệt"
        AppointmentStatus.APPROVED -> "Đã xác nhận"
        AppointmentStatus.CANCELED -> "Đã hủy"
    }
    
    val statusColor = when (appointment.status) {
        AppointmentStatus.PENDING -> OrangeSecondary
        AppointmentStatus.APPROVED -> TealAccent
        AppointmentStatus.CANCELED -> Color.Red
    }

    // Main layout container
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Khách: ${appointment.renterName}", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Tại: ${appointment.roomName}", 
                        fontSize = 13.sp, 
                        color = OnBackgroundLight.copy(alpha = 0.6f)
                    )
                }
                StatusBadge(text = statusText, color = statusColor)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lúc: ${appointment.time} - ${appointment.date}", 
                fontSize = 13.sp, 
                color = OnBackgroundLight.copy(alpha = 0.6f)
            )

            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                // Action buttons row for Pending status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAction(AppointmentStatus.CANCELED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Từ chối", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onAction(AppointmentStatus.APPROVED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Xác nhận", fontSize = 13.sp)
                    }
                }
            } else if (appointment.status == AppointmentStatus.APPROVED) {
                Spacer(modifier = Modifier.height(12.dp))
                // Action buttons row for Approved status - Create Contract shortcut
                Button(
                    onClick = onCreateContract,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Lập hợp đồng", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentCardPreview() {
    EzRoomTheme(darkTheme = false) {
        HostAppointmentCard(
            appointment = Appointment(
                id = "1",
                roomId = "R1",
                roomName = "Phòng trọ cao cấp Q7",
                renterName = "Trần Lê Quốc Dũng",
                renterPhone = "0123456789",
                hostName = "Host Name",
                date = "20/05/2024",
                time = "14:00",
                note = "Ghé xem phòng",
                status = AppointmentStatus.PENDING
            ),
            showActions = true
        )
    }
}
