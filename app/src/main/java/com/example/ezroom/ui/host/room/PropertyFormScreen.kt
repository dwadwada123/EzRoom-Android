package com.example.ezroom.ui.host.room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.domain.model.*
import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.components.*
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*
import com.example.ezroom.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

// Local UI wrapper: Amenity Item for form state
data class CommonAmenityItem(
    val name: String,
    val isChecked: Boolean = false
)

// UI Component: Property Creation/Edit Form
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormScreen(
    propertyId: String? = null,
    onNavigateToCreateFirstRoom: (String) -> Unit = {},
    onBack: () -> Unit = {},
    locationViewModel: LocationViewModel = viewModel(),
    viewModel: PropertyFormViewModel = viewModel(
        factory = viewModelFactory {
            val repo = RoomRepositoryImpl()
            PropertyFormViewModel(SavePropertyUseCase(repo), GetPropertyByIdUseCase(repo))
        }
    )
) {
    // State Management: UI State from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    var showDiscardDialog by remember { mutableStateOf(false) }

    // State Management: Location states
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLocationLoading by locationViewModel.isLoading.collectAsState()

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

    // Initialization: Load property data if editing
    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    // Navigation: Handle successful save
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            val finalId = propertyId ?: "new_prop_id" // Ideally returned from VM
            onNavigateToCreateFirstRoom(finalId)
        }
    }

    val isEditMode = propertyId != null && propertyId != "{propertyId}"
    val isFormValid = uiState.name.isNotEmpty() && 
                      (isEditMode || (selectedProvince != null && selectedWard != null)) && 
                      uiState.detailedAddress.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (!isEditMode) "Tạo dãy trọ mới" else "Chỉnh sửa dãy trọ", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) { Icon(Icons.Default.Close, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Neutral50)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Thông tin dãy trọ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                
                // UI Component: Property Name
                CustomTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = "Tên dãy trọ (VD: EzHome Quận 7)",
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isEditMode) {
                    // UI Component: Location Selectors
                    LocationDropdown(
                        label = "Tỉnh/Thành phố",
                        items = provinces,
                        selectedItemName = selectedProvince?.name ?: "",
                        onItemSelected = { 
                            selectedProvince = it
                            selectedWard = null
                            locationViewModel.selectProvince(it.code)
                        },
                        getItemName = { it.name },
                        isLoading = isLocationLoading && provinces.isEmpty()
                    )

                    LocationDropdown(
                        label = "Phường/Xã/Khu vực",
                        items = wards,
                        selectedItemName = selectedWard?.name ?: "",
                        onItemSelected = { selectedWard = it },
                        getItemName = { it.name },
                        enabled = selectedProvince != null
                    )
                }

                // UI Component: Detailed Address
                CustomTextField(
                    value = uiState.detailedAddress,
                    onValueChange = { viewModel.onDetailedAddressChange(it) },
                    label = "Địa chỉ chi tiết",
                    modifier = Modifier.fillMaxWidth()
                )

                // UI Component: Common Amenities Grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Tiện ích chung (Của cả dãy/tòa nhà)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    
                    uiState.commonAmenities.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                val index = uiState.commonAmenities.indexOf(item)
                                FilterChip(
                                    selected = item.isChecked,
                                    onClick = { if (index != -1) viewModel.onToggleAmenity(index) },
                                    label = { Text(item.name, style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = if (item.isChecked) {
                                        { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryMain.copy(alpha = 0.1f),
                                        selectedLabelColor = PrimaryMain,
                                        selectedLeadingIconColor = PrimaryMain
                                    )
                                )
                            }
                            if (rowItems.size < 3) {
                                repeat(3 - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // UI Component: Map Section
                val danangCenter = remember { LatLng(16.0544, 108.2022) }
                val markerState = rememberMarkerState(position = danangCenter)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(danangCenter, 15f)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ghim vị trí trên bản đồ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Card(modifier = Modifier.fillMaxWidth().height(200.dp), shape = MaterialTheme.shapes.medium) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(zoomControlsEnabled = true),
                            onMapClick = { markerState.position = it }
                        ) {
                            Marker(state = markerState, title = "Vị trí dãy trọ")
                        }
                    }
                }

                // UI Component: Property Description
                CustomTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = "Mô tả chung cho cả dãy trọ",
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Business Logic: Save Trigger
                PrimaryButton(
                    text = if (!isEditMode) "TIẾP THEO: THÊM PHÒNG ĐẦU TIÊN" else "HOÀN TẤT CHỈNH SỬA",
                    onClick = {
                        viewModel.onSave(
                            propertyId, 
                            selectedProvince?.name, 
                            selectedWard?.name, 
                            markerState.position.latitude, 
                            markerState.position.longitude
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !uiState.isLoading
                )
            }
            
            // UI Component: Discard Changes Dialog
            if (showDiscardDialog) {
                val dialogTitle = if (isEditMode) "Hủy chỉnh sửa?" else "Hủy tạo dãy trọ?"
                val confirmText = if (isEditMode) "Bỏ chỉnh sửa" else "Hủy tạo"
                val dismissText = if (isEditMode) "Tiếp tục sửa" else "Tiếp tục tạo"

                AlertDialog(
                    onDismissRequest = { showDiscardDialog = false },
                    title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
                    text = { Text("Mọi thông tin về dãy trọ này sẽ không được lưu. Bạn có chắc chắn muốn thoát?") },
                    confirmButton = {
                        Button(
                            onClick = { 
                                showDiscardDialog = false
                                onBack() 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                        ) {
                            Text(confirmText)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDiscardDialog = false }) {
                            Text(dismissText)
                        }
                    },
                    containerColor = Color.White
                )
            }

            if (uiState.isLoading) LoadingWidget()
        }
    }
}
