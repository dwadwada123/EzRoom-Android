package com.example.ezroom.ui.host.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.util.*

@Composable
fun HostAppointmentListScreen(
    onNavigateBack: () -> Unit,
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
    
    // Reschedule state
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }

    // Confirmation state
    var showActionConfirmation by remember { mutableStateOf(false) }
    var selectedActionStatus by remember { mutableStateOf<AppointmentStatus?>(null) }
    var selectedAppointmentId by remember { mutableStateOf("") }
    
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Pill-shaped Tab Selection
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                color = Color.White,
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

            if (!uiState.isLoading && uiState.appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EventAvailable, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không có lịch hẹn nào",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.appointments, key = { it.id }) { item ->
                        HostAppointmentCard(
                            appointment = item,
                            showActions = uiState.selectedTabIndex == 0,
                            onAction = { newStatus ->
                                selectedAppointmentId = item.id
                                selectedActionStatus = newStatus
                                showActionConfirmation = true
                            },
                            onReschedule = {
                                appointmentToReschedule = item
                                showRescheduleDialog = true
                            },
                            onRenterClick = { onRenterClick("Nguyễn Văn A") }, // Mock name
                            onCreateContract = onCreateContract
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            LoadingWidget()
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
            AlertDialog(
                onDismissRequest = { showActionConfirmation = false },
                title = { Text("Xác nhận", fontWeight = FontWeight.Bold) },
                text = { 
                    Text(
                        if (selectedActionStatus == AppointmentStatus.APPROVED) 
                            "Bạn có chắc chắn muốn xác nhận lịch hẹn này?" 
                        else "Bạn có chắc chắn muốn hủy lịch hẹn này?"
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
                            containerColor = if (selectedActionStatus == AppointmentStatus.APPROVED) PrimaryMain else ErrorRose
                        )
                    ) {
                        Text("Đồng ý")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showActionConfirmation = false }) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun RescheduleDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current
    var newDate by remember { mutableStateOf(appointment.date) }
    var newTime by remember { mutableStateOf(appointment.time) }
    
    val calendar = Calendar.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hẹn lại lịch mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Đề xuất thời gian khác cho khách thuê ${appointment.renterName}.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Date Selection
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(context, { _, y, m, d ->
                            newDate = "$d/${m + 1}/$y"
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Ngày: $newDate")
                    }
                }

                // Time Selection
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(context, { _, h, min ->
                            newTime = String.format(Locale.getDefault(), "%02d:%02d", h, min)
                        }, 12, 0, true).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Giờ: $newTime")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newDate, newTime) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryMain)
            ) {
                Text("Gửi đề nghị")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HostAppointmentListScreenPreview() {
    EzRoomTheme {
        HostAppointmentListScreen(onNavigateBack = {})
    }
}
