package com.example.ezroom.ui.host.appointment

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.AppointmentRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetAppointmentsUseCase
import com.example.ezroom.domain.usecase.UpdateAppointmentStatusUseCase
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostAppointmentListScreen(
    onNavigateBack: () -> Unit = {},
    onCreateContract: (Appointment) -> Unit = {},
    onRenterClick: (String) -> Unit = {},
    viewModel: HostAppointmentViewModel = viewModel(
        factory = viewModelFactory {
            val repository = AppointmentRepositoryImpl()
            HostAppointmentViewModel(
                GetAppointmentsUseCase(repository),
                UpdateAppointmentStatusUseCase(repository),
                repository
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadAppointments()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    HostAppointmentListContent(
        uiState = uiState,
        onTabSelected = { viewModel.onTabSelected(it) },
        onUpdateStatus = { id, status -> viewModel.updateAppointmentStatus(id, status) },
        onReschedule = { id, date, time -> viewModel.rescheduleAppointment(id, date, time) },
        onRenterClick = onRenterClick,
        onCreateContract = onCreateContract
    )
}

// UI Component: Appointment Card for Host
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onReschedule: () -> Unit,
    onRenterClick: () -> Unit,
    onCreateContract: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onRenterClick() }
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(PrimaryLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = PrimaryMain)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = appointment.renterName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(14.dp), tint = Neutral500)
                    }
                    Text(text = appointment.renterPhone, style = MaterialTheme.typography.bodySmall, color = Neutral500)
                }
                StatusChip(status = appointment.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Neutral100)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, null, modifier = Modifier.size(16.dp), tint = Neutral500)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = appointment.roomName, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = Neutral500)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${appointment.date} - ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
            }
            
            if (appointment.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ghi chú: ${appointment.note}", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Neutral500,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            
            if (appointment.status == AppointmentStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Main Action
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Xác nhận lịch hẹn", fontWeight = FontWeight.Bold)
                    }
                    
                    // Secondary Actions Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Từ chối")
                        }
                        OutlinedButton(
                            onClick = onReschedule,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hẹn lại")
                        }
                    }
                }
            } else if (appointment.status == AppointmentStatus.RESCHEDULED) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRose),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hủy lịch")
                    }
                    OutlinedButton(
                        onClick = onReschedule,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hẹn lại")
                    }
                }
            } else if (appointment.status == AppointmentStatus.APPROVED) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCreateContract,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lập hợp đồng ngay", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: AppointmentStatus) {
    val (color, text) = when(status) {
        AppointmentStatus.PENDING -> AccentAmber to "Chờ xác nhận"
        AppointmentStatus.APPROVED -> SuccessEmerald to "Đã xác nhận"
        AppointmentStatus.CANCELED -> ErrorRose to "Đã hủy"
        AppointmentStatus.RESCHEDULED -> PrimaryMain to "Đã hẹn lại"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyAppointments() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EventBusy, null, modifier = Modifier.size(64.dp), tint = Neutral300)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Không có lịch hẹn nào", color = Neutral500)
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostAppointmentListContent(
    uiState: HostAppointmentUiState,
    onTabSelected: (Int) -> Unit,
    onUpdateStatus: (String, AppointmentStatus) -> Unit,
    onReschedule: (String, String, String) -> Unit,
    onRenterClick: (String) -> Unit,
    onCreateContract: (Appointment) -> Unit
) {
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    
    var selectedAppointmentId by remember { mutableStateOf("") }
    var selectedActionStatus by remember { mutableStateOf<AppointmentStatus?>(null) }
    var showActionConfirmation by remember { mutableStateOf(false) }
    
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
                            onClick = { onTabSelected(index) },
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
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryMain)
                }
            } else if (uiState.appointments.isEmpty()) {
                EmptyAppointments()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.appointments, key = { it.id }) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onApprove = {
                                selectedAppointmentId = appointment.id
                                selectedActionStatus = AppointmentStatus.APPROVED
                                showActionConfirmation = true
                            },
                            onCancel = {
                                selectedAppointmentId = appointment.id
                                selectedActionStatus = AppointmentStatus.CANCELED
                                showActionConfirmation = true
                            },
                            onReschedule = {
                                appointmentToReschedule = appointment
                                showRescheduleDialog = true
                            },
                            onRenterClick = { onRenterClick(appointment.renterId ?: "") },
                            onCreateContract = { onCreateContract(appointment) }
                        )
                    }
                }
            }
        }
        
        // Reschedule Dialog
        if (showRescheduleDialog && appointmentToReschedule != null) {
            RescheduleDialog(
                appointment = appointmentToReschedule!!,
                onDismiss = { showRescheduleDialog = false },
                onConfirm = { newDate, newTime ->
                    onReschedule(appointmentToReschedule!!.id, newDate, newTime)
                    showRescheduleDialog = false
                }
            )
        }

        // Action Confirmation Dialog
        if (showActionConfirmation) {
            val isApprove = selectedActionStatus == AppointmentStatus.APPROVED
            AlertDialog(
                onDismissRequest = { showActionConfirmation = false },
                title = { Text(if (isApprove) "Xác nhận lịch hẹn" else "Hủy lịch hẹn", fontWeight = FontWeight.Bold) },
                text = { 
                    Text(
                        if (isApprove) 
                            "Hệ thống sẽ gửi thông báo xác nhận đến khách thuê. Bạn có chắc chắn?" 
                        else "Lịch hẹn này sẽ bị hủy và thông báo sẽ được gửi tới khách. Bạn có chắc chắn?"
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedActionStatus?.let { status ->
                                onUpdateStatus(selectedAppointmentId, status)
                            }
                            showActionConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isApprove) SuccessEmerald else ErrorRose
                        )
                    ) {
                        Text(if (isApprove) "Xác nhận" else "Xác nhận hủy")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showActionConfirmation = false }) {
                        Text("Bỏ qua")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentListScreenPreview() {
    val sampleAppointments = listOf(
        Appointment(
            id = "1",
            roomId = "room1",
            roomName = "Phòng trọ cao cấp Quận 7",
            renterId = "renter1",
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Lê Văn Chủ",
            date = "25/05/2024",
            time = "10:00",
            note = "Muốn xem phòng vào buổi sáng",
            status = AppointmentStatus.PENDING
        ),
        Appointment(
            id = "2",
            roomId = "room2",
            roomName = "Căn hộ Studio dịch vụ",
            renterId = "renter2",
            renterName = "Trần Thị B",
            renterPhone = "0907654321",
            hostName = "Lê Văn Chủ",
            date = "26/05/2024",
            time = "15:30",
            note = "",
            status = AppointmentStatus.APPROVED
        )
    )
    
    EzRoomTheme {
        HostAppointmentListContent(
            uiState = HostAppointmentUiState(
                appointments = sampleAppointments,
                isLoading = false,
                selectedTabIndex = 0
            ),
            onTabSelected = {},
            onUpdateStatus = { _, _ -> },
            onReschedule = { _, _, _ -> },
            onRenterClick = {},
            onCreateContract = {}
        )
    }
}
