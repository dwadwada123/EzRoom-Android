package com.example.ezroom.ui.renter.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.OrangePrimary
import com.example.ezroom.ui.theme.OrangeTertiary
import com.example.ezroom.ui.theme.OnPrimaryLight
import com.example.ezroom.ui.theme.BackgroundLight
import com.example.ezroom.ui.theme.SurfaceLight
import com.example.ezroom.ui.theme.OnBackgroundLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    roomName: String = "Phòng trọ sinh viên",
    onNavigateBack: () -> Unit,
    onSubmitBooking: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ĐẶT LỊCH XEM PHÒNG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OrangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = OrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bạn đang đặt lịch hẹn xem phòng:\n$roomName",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackgroundLight
            )

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày xem phòng") },
                placeholder = { Text("Chọn ngày") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Chọn ngày", tint = OrangePrimary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker(context) { date ->
                            selectedDate = date
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = OnBackgroundLight,
                    disabledBorderColor = OnBackgroundLight.copy(alpha = 0.1f),
                    disabledPlaceholderColor = OnBackgroundLight.copy(alpha = 0.6f),
                    disabledLabelColor = OnBackgroundLight.copy(alpha = 0.6f),
                    disabledTrailingIconColor = OrangePrimary
                ),
                enabled = false
            )

            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ xem phòng") },
                placeholder = { Text("Chọn giờ") },
                trailingIcon = {
                    Icon(Icons.Default.Notifications, contentDescription = "Chọn giờ", tint = OrangePrimary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePicker(context) { time ->
                            selectedTime = time
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = OnBackgroundLight,
                    disabledBorderColor = OnBackgroundLight.copy(alpha = 0.1f),
                    disabledPlaceholderColor = OnBackgroundLight.copy(alpha = 0.6f),
                    disabledLabelColor = OnBackgroundLight.copy(alpha = 0.6f),
                    disabledTrailingIconColor = OrangePrimary
                ),
                enabled = false
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Lời nhắn cho Chủ trọ (Tùy chọn)") },
                placeholder = { Text("Ví dụ: Mình có thể đến vào buổi chiều...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackgroundLight,
                    unfocusedTextColor = OnBackgroundLight,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = OnBackgroundLight.copy(alpha = 0.1f),
                    focusedLabelColor = OrangePrimary,
                    unfocusedLabelColor = OnBackgroundLight.copy(alpha = 0.6f),
                    focusedPlaceholderColor = OnBackgroundLight.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = OnBackgroundLight.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        onSubmitBooking(selectedDate, selectedTime, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = OnPrimaryLight,
                    disabledContainerColor = OrangeTertiary,
                    disabledContentColor = OnPrimaryLight
                )
            ) {
                Text(
                    text = "XÁC NHẬN ĐẶT LỊCH",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        },
        year, month, day
    ).show()
}

@Preview(showBackground = true)
@Composable
fun BookingFormScreenPreview() {
    EzRoomTheme {
        BookingFormScreen(
            roomName = "Phòng trọ cao cấp Quận 7",
            onNavigateBack = {},
            onSubmitBooking = { _, _, _ -> }
        )
    }
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        },
        hour, minute, true
    ).show()
}
