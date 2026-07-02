package com.example.ezroom.ui.renter.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    // Event callbacks
    roomName: String = "Phòng trọ sinh viên",
    appointment: Appointment? = null,
    onNavigateBack: () -> Unit,
    onSubmitBooking: (String, String, String) -> Unit
) {
    // State definitions
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = selectedDate.isNotEmpty() && selectedTime.isNotEmpty()

    // Sync state for Edit Mode
    LaunchedEffect(appointment) {
        appointment?.let {
            selectedDate = it.date
            selectedTime = it.time
            note = it.note
        }
    }

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Neutral50,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (appointment != null) "CẬP NHẬT LỊCH HẸN" else "ĐẶT LỊCH XEM PHÒNG",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack, 
                            enabled = !isLoading,
                            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = if (appointment != null) 
                            "Bạn đang cập nhật lịch hẹn xem phòng:\n$roomName"
                        else 
                            "Bạn đang đặt lịch hẹn xem phòng:\n$roomName",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Input fields group: Date Picker
                CustomTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = "Ngày xem phòng",
                    placeholder = "Chọn ngày",
                    readOnly = true,
                    enabled = !isLoading,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) {
                            showDatePicker(context, selectedDate) { date -> selectedDate = date }
                        }
                )

                // Input fields group: Time Picker
                CustomTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = "Giờ xem phòng",
                    placeholder = "Chọn giờ",
                    readOnly = true,
                    enabled = !isLoading,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) {
                            showTimePicker(context, selectedTime) { time -> selectedTime = time }
                        }
                )

                // Input fields group: Notes
                CustomTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Lời nhắn cho Chủ trọ (Tùy chọn)",
                    placeholder = "Ví dụ: Mình có thể đến vào buổi chiều...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    singleLine = false,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons row
                PrimaryButton(
                    text = if (appointment != null) "CẬP NHẬT LỊCH HẸN" else "XÁC NHẬN ĐẶT LỊCH",
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onSubmitBooking(selectedDate, selectedTime, note)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = isFormValid && !isLoading
                )
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

/**
 * Shows system DatePickerDialog with pre-filled date if available
 */
private fun showDatePicker(
    context: Context, 
    initialDate: String, 
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    if (initialDate.isNotEmpty()) {
        try {
            val parts = initialDate.split("/")
            if (parts.size == 3) {
                calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                calendar.set(Calendar.YEAR, parts[2].toInt())
            }
        } catch (e: Exception) { }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

/**
 * Shows system TimePickerDialog with pre-filled time if available
 */
private fun showTimePicker(
    context: Context, 
    initialTime: String, 
    onTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    if (initialTime.isNotEmpty()) {
        try {
            val parts = initialTime.split(":")
            if (parts.size == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                calendar.set(Calendar.MINUTE, parts[1].toInt())
            }
        } catch (e: Exception) { }
    }

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format("%02d:%02d", hour, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
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

