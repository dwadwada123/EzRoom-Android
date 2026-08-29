package com.example.ezroom.ui.host.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.StatusBadge
import com.example.ezroom.ui.theme.*

private val peaceSansFont = FontFamily.Default

@Composable
fun HostAppointmentCard(
    appointment: Appointment,
    showActions: Boolean,
    onAction: (AppointmentStatus) -> Unit = {},
    onReschedule: () -> Unit = {},
    onRenterClick: () -> Unit = {},
    onCreateContract: () -> Unit = {}
) {
    val renterReviews = MockData.renterReviews
    // In real app, we would match by renterId. Here we use renterName for mock.
    val renterRating = remember(appointment.renterName) {
        val matchingReviews = renterReviews.filter { it.hostName == appointment.hostName || true } // simplify
        if (matchingReviews.isEmpty()) 0.0 else matchingReviews.map { it.rating }.average()
    }

    val statusText = when (appointment.status) {
        AppointmentStatus.PENDING -> "Chờ duyệt"
        AppointmentStatus.APPROVED -> "Đã xác nhận"
        AppointmentStatus.CANCELED -> "Đã hủy"
        AppointmentStatus.RESCHEDULED -> "Đã hẹn lại"
    }

    val statusColor = when (appointment.status) {
        AppointmentStatus.PENDING -> AccentAmber
        AppointmentStatus.APPROVED -> AccentTeal
        AppointmentStatus.CANCELED -> ErrorRose
        AppointmentStatus.RESCHEDULED -> PrimaryMain
    }

    Surface(
        onClick = onRenterClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = appointment.renterName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = peaceSansFont
                        )
                        if (renterRating > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = AccentAmber.copy(alpha = 0.1f),
                                contentColor = AccentAmber,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, null, modifier = Modifier.size(10.dp))
                                    Text(
                                        text = "%.1f".format(renterRating),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = peaceSansFont
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = appointment.roomName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = peaceSansFont
                    )
                }
                StatusBadge(text = statusText, color = statusColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.date,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = peaceSansFont
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.time,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = peaceSansFont
                    )
                }
            }

            if (showActions) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAction(AppointmentStatus.CANCELED) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                        border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "Từ chối", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = peaceSansFont)
                    }

                    OutlinedButton(
                        onClick = onReschedule,
                        modifier = Modifier.weight(1.2f).height(44.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                        border = BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "Hẹn lại", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = peaceSansFont)
                    }

                    Button(
                        onClick = { onAction(AppointmentStatus.APPROVED) },
                        modifier = Modifier.weight(1.3f).height(44.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "Xác nhận", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = peaceSansFont)
                    }
                }
            } else if (appointment.status == AppointmentStatus.APPROVED) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCreateContract,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Lập hợp đồng ngay", fontWeight = FontWeight.Bold, fontFamily = peaceSansFont)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentCardPreview() {
    EzRoomTheme {
        HostAppointmentCard(
            appointment = Appointment(
                id = "1",
                roomId = "R1",
                roomName = "Phòng trọ cao cấp Quận 7",
                renterName = "Nguyễn Văn A",
                renterPhone = "0123456789",
                hostName = "Host Name",
                date = "20/05/2026",
                time = "14:00",
                note = "Ghé xem phòng",
                status = AppointmentStatus.PENDING
            ),
            showActions = true
        )
    }
}