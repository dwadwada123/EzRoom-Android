package com.example.ezroom.ui.appointment

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
fun AppointmentCard(
    roomName: String,
    renterName: String,
    date: String,
    time: String,
    note: String,
    status: String,
    isHost: Boolean = true,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {}
) {
    val statusColor = when (status) {
        "Approved" -> SuccessColor
        "Canceled" -> ErrorColor
        else -> OrangePrimary
    }

    val statusText = when (status) {
        "Approved" -> "Đã xác nhận"
        "Canceled" -> "Đã hủy"
        else -> "Chờ duyệt"
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = SurfaceLight
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(OutlineLight)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = roomName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundLight,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = OnSurfaceVariantLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = renterName,
                    fontSize = 14.sp,
                    color = OnSurfaceVariantLight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = OnSurfaceVariantLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$date | $time",
                    fontSize = 14.sp,
                    color = OnSurfaceVariantLight
                )
            }

            if (note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Ghi chú: \"$note\"",
                    fontSize = 14.sp,
                    color = OnBackgroundLight,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (isHost && status == "Pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ErrorColor
                        ),
                        border = BorderStroke(1.dp, ErrorColor)
                    ) {
                        Text("Từ chối", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Xác nhận", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentCardPreview() {
    EzRoomTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppointmentCard(
                roomName = "Phòng trọ cao cấp Quận 7",
                renterName = "Nguyễn Văn A",
                date = "25/10/2023",
                time = "14:00",
                note = "Tôi muốn xem phòng vào buổi chiều.",
                status = "Pending",
                isHost = true
            )
        }
    }
}