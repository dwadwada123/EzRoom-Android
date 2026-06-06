package com.example.ezroom.ui.host.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
fun HostAppointmentListScreen(
    // Event callbacks
    onNavigateBack: () -> Unit,
    onCreateContract: () -> Unit = {}
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
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
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
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
                    actionText = "Quay lại",
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
                        HostAppointmentCard(
                            appointment = item,
                            showActions = selectedTabIndex == 0,
                            onAction = { newStatus ->
                                val index = appointmentsState.indexOfFirst { it.id == item.id }
                                if (index != -1) {
                                    appointmentsState[index] = appointmentsState[index].copy(status = newStatus)
                                }
                            },
                            onCreateContract = onCreateContract
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

@Preview(showBackground = true)
@Composable
fun HostAppointmentListScreenPreview() {
    EzRoomTheme(darkTheme = false) {
        HostAppointmentListScreen(onNavigateBack = {})
    }
}
