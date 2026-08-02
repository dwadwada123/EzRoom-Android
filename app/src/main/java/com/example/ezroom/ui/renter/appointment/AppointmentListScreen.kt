package com.example.ezroom.ui.renter.appointment

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.AppointmentRepositoryImpl
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.usecase.GetAppointmentsUseCase
import com.example.ezroom.domain.usecase.UpdateAppointmentStatusUseCase
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * EzRoom Appointment List
 * Features: Staggered entrance, Pill-shaped tabs, and Bento card design.
 */
@Composable
fun RenterAppointmentListScreen(
    onNavigateBack: () -> Unit,
    onEditAppointment: (Appointment) -> Unit = {},
    onCancelAppointment: (Appointment) -> Unit = {},
    viewModel: AppointmentViewModel = viewModel(
        factory = viewModelFactory {
            val repository = AppointmentRepositoryImpl()
            AppointmentViewModel(
                GetAppointmentsUseCase(repository),
                UpdateAppointmentStatusUseCase(repository)
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    
    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pill-shaped Tab Selection
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = uiState.selectedTabIndex == index
                        val backgroundColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            label = "TabBg"
                        )
                        val contentColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "TabContent"
                        )

                        Surface(
                            onClick = { viewModel.onTabSelected(index) },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            color = backgroundColor,
                            contentColor = contentColor
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (!uiState.isLoading && uiState.appointments.isEmpty() && uiState.error == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    EmptyState(
                        title = "Trống",
                        description = "Bạn chưa có lịch hẹn nào ở mục này.",
                        actionText = "Khám phá phòng ngay",
                        onAction = onNavigateBack
                    )
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    EmptyState(
                        title = "Lỗi",
                        description = uiState.error ?: "",
                        actionText = "Thử lại",
                        onAction = { viewModel.loadAppointments() }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(
                        items = uiState.appointments, 
                        key = { _, it -> it.id },
                        contentType = { _, _ -> "AppointmentCard" }
                    ) { index, item ->
                        AnimatedVisibility(
                            visible = !uiState.isLoading,
                            enter = slideInVertically(initialOffsetY = { 50 * (index + 1) }) + fadeIn()
                        ) {
                            RenterAppointmentBentoCard(
                                appointment = item,
                                onEditClick = { 
                                    appointmentToReschedule = item
                                    showRescheduleDialog = true
                                },
                                onCancelClick = { 
                                    appointmentToCancel = item
                                    showCancelDialog = true
                                },
                                onApproveClick = {
                                    viewModel.approveAppointment(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        if (showCancelDialog && appointmentToCancel != null) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Xác nhận hủy lịch", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn hủy lịch hẹn xem phòng '${appointmentToCancel?.roomName}' không?") },
                confirmButton = {
                    Button(
                        onClick = {
                            appointmentToCancel?.let {
                                viewModel.cancelAppointment(it.id)
                                onCancelAppointment(it)
                            }
                            showCancelDialog = false
                            appointmentToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Xác nhận hủy")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Giữ lại")
                    }
                },
                containerColor = Color.White
            )
        }

        if (showRescheduleDialog && appointmentToReschedule != null) {
            RescheduleDialog(
                appointment = appointmentToReschedule!!,
                onDismiss = {
                    showRescheduleDialog = false
                    appointmentToReschedule = null
                },
                onConfirm = { newDate, newTime ->
                    viewModel.rescheduleAppointment(appointmentToReschedule!!.id, newDate, newTime)
                    showRescheduleDialog = false
                    appointmentToReschedule = null
                }
            )
        }

        if (uiState.isLoading) {
            LoadingWidget()
        }
    }
}

// UI Component: Appointment Card for Renter
@Composable
fun RenterAppointmentBentoCard(
    appointment: Appointment,
    onEditClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onApproveClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, 
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = appointment.roomName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Lịch xem phòng",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time & Date Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = appointment.date, style = MaterialTheme.typography.labelLarge)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = appointment.time, style = MaterialTheme.typography.labelLarge)
                }
            }

            if (appointment.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lời nhắn: \"${appointment.note}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            if (appointment.status == AppointmentStatus.RESCHEDULED) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
                    ) {
                        Text(text = "Đồng ý", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                        ) {
                            Text(text = "Hủy lịch", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(text = "Thay đổi", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            } else if (appointment.status == AppointmentStatus.PENDING) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Text(text = "Hủy lịch", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Thay đổi", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RenterAppointmentListScreenPreview() {
    EzRoomTheme {
        RenterAppointmentListScreen(onNavigateBack = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf(appointment.date) }
    var time by remember { mutableStateOf(appointment.time) }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        if (date.isNotEmpty()) {
            val parts = date.split("/")
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
                date = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun openTimePicker() {
        val calendar = Calendar.getInstance()
        if (time.isNotEmpty()) {
            val parts = time.split(":")
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
                time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đề xuất thời gian mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    onClick = { openDatePicker() },
                    shape = MaterialTheme.shapes.small
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Chọn ngày xem phòng") },
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Chọn ngày", tint = com.example.ezroom.ui.theme.PrimaryMain)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Surface(
                    onClick = { openTimePicker() },
                    shape = MaterialTheme.shapes.small
                ) {
                    OutlinedTextField(
                        value = time,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Chọn giờ xem phòng") },
                        trailingIcon = {
                            IconButton(onClick = { openTimePicker() }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Chọn giờ", tint = com.example.ezroom.ui.theme.PrimaryMain)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(date, time) }) {
                Text("Gửi đề xuất")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = Color.White
    )
}
