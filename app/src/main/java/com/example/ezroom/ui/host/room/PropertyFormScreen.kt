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
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.data.remote.LocationApi
import com.example.ezroom.data.remote.LocationSuggestion

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
    
    // State Management: Location states
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLocationLoading by locationViewModel.isLoading.collectAsState()

    // Initialization: Load property data if editing
    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    val isEditMode = propertyId != null && propertyId != "{propertyId}"

    // Navigation: Handle successful save
    LaunchedEffect(uiState.isSuccess, uiState.savedPropertyId) {
        val savedId = uiState.savedPropertyId
        if (uiState.isSuccess) {
            if (isEditMode) {
                onBack()
            } else if (!savedId.isNullOrBlank()) {
                onNavigateToCreateFirstRoom(savedId)
            }
        }
    }

    PropertyFormContent(
        uiState = uiState,
        provinces = provinces,
        wards = wards,
        isLocationLoading = isLocationLoading,
        propertyId = propertyId,
        onBack = onBack,
        onNameChange = viewModel::onNameChange,
        onDetailedAddressChange = viewModel::onDetailedAddressChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onToggleAmenity = viewModel::onToggleAmenity,
        onSave = viewModel::onSave,
        onSelectProvince = locationViewModel::selectProvince
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormContent(
    uiState: PropertyFormUiState,
    provinces: List<Province>,
    wards: List<Ward>,
    isLocationLoading: Boolean,
    propertyId: String? = null,
    onBack: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onDetailedAddressChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onToggleAmenity: (Int) -> Unit = {},
    onSave: (String?, String?, String?, Double, Double) -> Unit = { _, _, _, _, _ -> },
    onSelectProvince: (String) -> Unit = {}
) {
    var showDiscardDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // State Management: Location states
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

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
                    onValueChange = onNameChange,
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
                            onSelectProvince(it.code)
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

                // UI Component: Map Section
                val danangCenter = remember { LatLng(16.0544, 108.2022) }
                val markerState = rememberMarkerState(position = danangCenter)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(danangCenter, 15f)
                }

                // Update map when editing an existing property
                LaunchedEffect(uiState.latitude, uiState.longitude) {
                    if (uiState.latitude != null && uiState.longitude != null) {
                        val pos = LatLng(uiState.latitude!!, uiState.longitude!!)
                        markerState.position = pos
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(pos, 15f)
                    }
                }

                val locationApi = remember { NetworkClient.createService<LocationApi>() }
                var suggestions by remember { mutableStateOf<List<LocationSuggestion>>(emptyList()) }

                // Debounce address suggestions search
                LaunchedEffect(uiState.detailedAddress, selectedProvince, selectedWard) {
                    val query = uiState.detailedAddress
                    if (query.length < 3) {
                        suggestions = emptyList()
                        return@LaunchedEffect
                    }
                    delay(500) // Debounce delay
                    try {
                        val res = locationApi.suggest(
                            query = query,
                            province = selectedProvince?.name,
                            ward = selectedWard?.name
                        )
                        suggestions = res
                    } catch (e: Exception) {
                        suggestions = emptyList()
                    }
                }

                // Autopin map when Province/Ward changes
                LaunchedEffect(selectedProvince, selectedWard) {
                    val provName = selectedProvince?.name ?: ""
                    val wardName = selectedWard?.name ?: ""
                    if (provName.isEmpty()) return@LaunchedEffect
                    
                    val query = if (wardName.isNotEmpty()) "$wardName, $provName" else provName
                    try {
                        val coords = locationApi.geocode(query)
                        val newLatLng = LatLng(coords.lat, coords.lon)
                        markerState.position = newLatLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, if (wardName.isNotEmpty()) 15f else 12f)
                    } catch (e: Exception) {
                        // Fallback: stay where we are
                    }
                }

                // UI Component: Detailed Address
                AddressSuggestionField(
                    value = uiState.detailedAddress,
                    onValueChange = onDetailedAddressChange,
                    label = "Địa chỉ chi tiết",
                    suggestions = suggestions,
                    onSuggestionSelected = { suggestion ->
                        onDetailedAddressChange(suggestion.displayName)
                        val newLatLng = LatLng(suggestion.lat, suggestion.lon)
                        markerState.position = newLatLng
                        scope.launch {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 17f)
                        }
                    },

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
                                    onClick = { if (index != -1) onToggleAmenity(index) },
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
                    onValueChange = onDescriptionChange,
                    label = "Mô tả chung cho cả dãy trọ",
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Business Logic: Save Trigger
                PrimaryButton(
                    text = if (!isEditMode) "TIẾP THEO: THÊM PHÒNG ĐẦU TIÊN" else "HOÀN TẤT CHỈNH SỬA",
                    onClick = {
                        onSave(
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

@Preview(showBackground = true)
@Composable
fun PropertyFormScreenPreview() {
    EzRoomTheme {
        PropertyFormContent(
            uiState = PropertyFormUiState(
                name = "EzHome Quận 7",
                detailedAddress = "123 Nguyễn Văn Linh",
                commonAmenities = listOf(
                    CommonAmenityItem("Bảo vệ 24/7", true),
                    CommonAmenityItem("Camera an ninh", true),
                    CommonAmenityItem("Thang máy", false),
                    CommonAmenityItem("Nhà xe chung", true)
                )
            ),
            provinces = listOf(
                Province("Hồ Chí Minh", "79"),
                Province("Hà Nội", "01")
            ),
            wards = listOf(
                Ward("Tân Phong", "27493"),
                Ward("Tân Kiểng", "27499")
            ),
            isLocationLoading = false
        )
    }
}
