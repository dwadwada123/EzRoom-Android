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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostAppointmentListScreen(
    onNavigateBack: () -> Unit = {},
    onCreateContract: () -> Unit = {},
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
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    
    var selectedAppointmentId by remember { mutableStateOf("") }
    var selectedActionStatus by remember { mutableStateOf<AppointmentStatus?>(null) }
    var showActionConfirmation by remember { mutableStateOf(false) }
    
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pill-shaped Tab Selection (Like Renter Side for consistency)
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
                            onRenterClick = { onRenterClick(appointment.renterName) }
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
                    viewModel.rescheduleAppointment(appointmentToReschedule!!.id, newDate, newTime)
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
                                viewModel.updateAppointmentStatus(selectedAppointmentId, status)
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

// UI Component: Appointment Card for Host
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onReschedule: () -> Unit,
    onRenterClick: () -> Unit
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
            
            if (appointment.status == AppointmentStatus.PENDING || appointment.status == AppointmentStatus.RESCHEDULED) {
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
    var date by remember { mutableStateOf(appointment.date) }
    var time by remember { mutableStateOf(appointment.time) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đề xuất thời gian mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Ngày") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Giờ") },
                    modifier = Modifier.fillMaxWidth()
                )
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
