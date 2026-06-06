package com.example.ezroom.ui.renter.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.model.Appointment
import com.example.ezroom.data.model.AppointmentStatus
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RenterAppointmentListScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onEditAppointment: (Appointment) -> Unit = {},
    onCancelAppointment: (Appointment) -> Unit = {}
) {
    // State definitions
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    
    val tabs = listOf("Chờ duyệt", "Đã xác nhận", "Đã hủy")
    val statusFilters = listOf(AppointmentStatus.PENDING, AppointmentStatus.APPROVED, AppointmentStatus.CANCELED)

    val appointmentsState = remember { 
        mutableStateListOf<Appointment>().apply { addAll(MockData.appointments) } 
    }

    val filteredList = remember(selectedTabIndex, appointmentsState.size) {
        appointmentsState.filter { it.status == statusFilters[selectedTabIndex] }
    }

    fun refreshData() {
        scope.launch {
            isLoading = true
            isError = false
            delay(1000) // Simulate data fetching
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab bar section
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SurfaceLight,
                contentColor = OrangePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), 
                        color = OrangePrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                text = title, 
                                fontSize = 12.sp, 
                                maxLines = 1,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            if (isError) {
                EmptyState(
                    title = "Đã có lỗi xảy ra",
                    description = "Vui lòng thử lại sau giây lát.",
                    actionText = "Thử lại",
                    onAction = { refreshData() }
                )
            } else if (!isLoading && filteredList.isEmpty()) {
                EmptyState(
                    title = "Chưa có lịch hẹn",
                    description = "Bạn chưa có lịch hẹn nào ở trạng thái này.",
                    actionText = "Khám phá phòng ngay",
                    onAction = onNavigateBack
                )
            } else {
                // Content scroll area
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        RenterAppointmentCard(
                            appointment = item,
                            onEditClick = { onEditAppointment(item) },
                            onCancelClick = { 
                                appointmentsState.remove(item)
                                onCancelAppointment(item) 
                            }
                        )
                    }
                }
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Composable
fun RenterAppointmentCard(
    appointment: Appointment,
    onEditClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = appointment.roomName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Thời gian: ${appointment.time} - ${appointment.date}", 
                fontSize = 13.sp, 
                color = OnBackgroundLight.copy(alpha = 0.6f)
            )
            if (appointment.note.isNotEmpty()) {
                Text(
                    text = "Lời nhắn: ${appointment.note}", 
                    fontSize = 13.sp, 
                    color = OnBackgroundLight.copy(alpha = 0.6f)
                )
            }

            if (appointment.status == AppointmentStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Hủy lịch", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Sửa lịch", fontSize = 12.sp, color = Color.White)
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
