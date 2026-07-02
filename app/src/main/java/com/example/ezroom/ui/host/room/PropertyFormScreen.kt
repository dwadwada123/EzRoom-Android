package com.example.ezroom.ui.host.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.PropertyType
import com.example.ezroom.data.remote.Province
import com.example.ezroom.data.remote.Ward
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.LocationDropdown
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.Neutral50
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormScreen(
    propertyId: String? = null,
    onNavigateToCreateFirstRoom: (String) -> Unit = {},
    onBack: () -> Unit = {},
    locationViewModel: LocationViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var detailedAddress by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLocationLoading by locationViewModel.isLoading.collectAsState()

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }
    
    var isLoading by remember { mutableStateOf(false) }

    // Pre-fill logic if editing
    LaunchedEffect(propertyId) {
        if (propertyId != null) {
            val property = com.example.ezroom.data.model.MockData.properties.find { it.id == propertyId }
            property?.let {
                name = it.name
                detailedAddress = it.detailedAddress
                description = it.description
                // Note: Province/Ward pre-filling logic would require more mapping here
            }
        }
    }

    val isFormValid = name.isNotEmpty() && (propertyId != null || (selectedProvince != null && selectedWard != null)) && detailedAddress.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (propertyId == null) "Tạo dãy trọ mới" else "Chỉnh sửa dãy trọ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = null) }
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
                
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tên dãy trọ (VD: EzHome Quận 7)",
                    modifier = Modifier.fillMaxWidth()
                )

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

                CustomTextField(
                    value = detailedAddress,
                    onValueChange = { detailedAddress = it },
                    label = "Địa chỉ chi tiết",
                    modifier = Modifier.fillMaxWidth()
                )

                // Map Section
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

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Mô tả chung cho cả dãy trọ",
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "TIẾP THEO: THÊM PHÒNG ĐẦU TIÊN",
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(1000)
                            isLoading = false
                            // In real app, save Property and get ID
                            val newId = UUID.randomUUID().toString()
                            onNavigateToCreateFirstRoom(newId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading
                )
            }
            
            if (isLoading) LoadingWidget()
        }
    }
}

