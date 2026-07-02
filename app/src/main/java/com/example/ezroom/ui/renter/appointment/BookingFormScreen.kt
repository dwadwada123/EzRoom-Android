package com.example.ezroom.ui.renter.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    roomName: String,
    appointment: Appointment? = null,
    onNavigateBack: () -> Unit,
    onSubmitBooking: (String, String, String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var date by remember { mutableStateOf(appointment?.date ?: "") }
    var time by remember { mutableStateOf(appointment?.time ?: "") }
    var note by remember { mutableStateOf(appointment?.note ?: "") }
    var isLoading by remember { mutableStateOf(value = false) }

    val isFormValid = date.isNotBlank() && time.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Neutral50,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (appointment == null) "ĐẶT LỊCH HẸN" else "SỬA LỊCH HẸN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Info Header
                Surface(
                    color = PrimarySurface,
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.1f)),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.HomeWork, null, tint = PrimaryMain)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Đang đặt lịch cho:", style = MaterialTheme.typography.labelSmall, color = Neutral500)
                            Text(roomName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Date Picker
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = date,
                        onValueChange = {},
                        label = "Chọn ngày xem phòng",
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = PrimaryMain) },
                        enabled = !isLoading,
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = !isLoading) {
                                showDatePicker(context, date) { date = it }
                            },
                    )
                }

                // Time Picker
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = time,
                        onValueChange = {},
                        label = "Chọn giờ xem phòng",
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = PrimaryMain) },
                        enabled = !isLoading,
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = !isLoading) {
                                showTimePicker(context, time) { time = it }
                            },
                    )
                }

                // Note
                CustomTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Lời nhắn cho chủ nhà (Tùy chọn)",
                    singleLine = false,
                    modifier = Modifier.height(120.dp),
                    enabled = !isLoading,
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = if (appointment == null) "XÁC NHẬN ĐẶT LỊCH" else "CẬP NHẬT LỊCH HẸN",
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onSubmitBooking(date, time, note)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading,
                )
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

private fun showDatePicker(context: android.content.Context, current: String, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    if (current.isNotEmpty()) {
        val parts = current.split("/")
        if (parts.size == 3) {
            try {
                calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                calendar.set(Calendar.YEAR, parts[2].toInt())
            } catch (ignored: Exception) { }
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun showTimePicker(context: android.content.Context, current: String, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    if (current.isNotEmpty()) {
        val parts = current.split(":")
        if (parts.size == 2) {
            try {
                calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                calendar.set(Calendar.MINUTE, parts[1].toInt())
            } catch (ignored: Exception) { }
        }
    }

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true,
    ).show()
}

@Preview(showBackground = true)
@Composable
fun BookingFormScreenPreview() {
    EzRoomTheme {
        BookingFormScreen(
            roomName = "Phòng 101 - EzHome Hải Châu",
            onNavigateBack = {},
            onSubmitBooking = { _, _, _ -> },
        )
    }
}
