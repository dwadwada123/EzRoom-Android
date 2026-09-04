package com.example.ezroom.ui.host.room

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.LocationDropdown
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.SmallTextField
import com.example.ezroom.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.navigation.LocalSnackbarProvider
import com.example.ezroom.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.data.remote.LocationApi
import com.example.ezroom.data.remote.LocationSuggestion
import com.example.ezroom.ui.components.AddressSuggestionField
import kotlinx.coroutines.delay

// Local UI wrapper for amenities
data class AmenityItem(
    val name: String, 
    val compensationAmount: String = "", 
    val isChecked: Boolean = false
)

// Local UI wrapper for images to handle labels in the form
data class RoomImageUI(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val url: String? = null,
    val resId: Int? = null,
    val category: String = "Khác"
)

val ImageCategories = listOf("Phòng ngủ", "Phòng khách", "Phòng bếp", "Nhà vệ sinh", "Ban công", "Mặt tiền", "Khác")

// Helper: Format raw numeric strings into thousand-separated Vietnamese VND prices
fun formatVndNumber(raw: String): String {
    val digitsOnly = raw.filter { it.isDigit() }
    if (digitsOnly.isEmpty()) return ""
    return try {
        val number = digitsOnly.toLong()
        java.text.NumberFormat.getInstance(java.util.Locale.forLanguageTag("vi-VN")).format(number)
    } catch (e: Exception) {
        digitsOnly
    }
}


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RoomFormScreen(
    isEditMode: Boolean = false, 
    propertyId: String? = null,
    cloneFromRoomId: String? = null,
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    locationViewModel: LocationViewModel = viewModel(),
    viewModel: RoomFormViewModel = viewModel(
        factory = viewModelFactory {
            val repository = RoomRepositoryImpl()
            RoomFormViewModel(
                GetPropertyByIdUseCase(repository),
                GetRoomByIdUseCase(repository),
                SaveRoomUseCase(repository),
                repository
            )
        }
    )
) {
    // State definitions
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val showSnackbar = LocalSnackbarProvider.current
    val context = androidx.compose.ui.platform.LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    // Location States
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLocationLoading by locationViewModel.isLoading.collectAsState()

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

    // Map State
    val danangCenter = remember { LatLng(16.0544, 108.2022) }
    val markerState = rememberMarkerState(position = danangCenter)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(danangCenter, 15f)
    }

    // Update map when editing an existing room
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

    var isStructureDropdownExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            uris.forEach { uri ->
                viewModel.uploadedImages.add(RoomImageUI(uri = uri))
            }
        }
    )

    // Load data from ViewModel
    LaunchedEffect(cloneFromRoomId, isEditMode, propertyId) {
        viewModel.loadData(propertyId, cloneFromRoomId, isEditMode)
    }

    // Trigger save success navigation
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSnackbar(if (isEditMode) "Cập nhật phòng thành công" else "Lưu thông tin phòng thành công")
            onSaveSuccess()
        }
    }

    val isFormValid = uiState.title.isNotEmpty() && 
                      (uiState.belongsToProperty != null || uiState.address.isNotBlank() || (selectedProvince != null && selectedWard != null)) &&
                      uiState.detailedAddress.isNotEmpty() && uiState.price.isNotEmpty() && uiState.totalArea.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = when {
                                isEditMode -> "Chỉnh sửa phòng"
                                cloneFromRoomId != null -> "Sao chép phòng"
                                else -> "Đăng phòng mới"
                            }, 
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                        ) 
                    },
                    navigationIcon = { 
                        IconButton(onClick = { showDiscardDialog = true }, enabled = !uiState.isLoading) { 
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng") 
                        } 
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (uiState.belongsToProperty != null) {
                    PropertyInfoBanner(uiState.belongsToProperty!!)
                }

                FormSectionTitle(title = "Thông tin cơ bản")
                
                CustomTextField(
                    value = uiState.title, 
                    onValueChange = { viewModel.onTitleChange(it) }, 
                    label = "Tên/Số phòng (VD: Phòng 101)", 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                // Structure Dropdown with full area tap detection

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isLoading) { isStructureDropdownExpanded = true }
                ) {
                    CustomTextField(
                        value = uiState.selectedStructure.displayName, 
                        onValueChange = {}, 
                        readOnly = true,
                        label = "Loại phòng",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStructureDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )
                    // Transparent overlay Box to ensure tap anywhere on the input opens dropdown
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = !uiState.isLoading) { isStructureDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isStructureDropdownExpanded, 
                        onDismissRequest = { isStructureDropdownExpanded = false }
                    ) {
                        RoomStructure.entries.forEach { structure ->
                            DropdownMenuItem(
                                text = { Text(structure.displayName) },
                                onClick = { 
                                    viewModel.onStructureChange(structure)
                                    isStructureDropdownExpanded = false 
                                }
                            )
                        }
                    }
                }

                // Price Input with Auto-Formatting thousand separators (.)
                val formattedPrice = remember(uiState.price) { formatVndNumber(uiState.price) }
                CustomTextField(
                    value = formattedPrice, 
                    onValueChange = { newValue ->
                        val digitsOnly = newValue.filter { it.isDigit() }
                        viewModel.onPriceChange(digitsOnly)
                    }, 
                    label = "Giá thuê / tháng (VND)", 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                FormSectionTitle(title = "Chi phí dịch vụ")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val formattedElec = remember(uiState.electricityPrice) { formatVndNumber(uiState.electricityPrice) }
                    CustomTextField(
                        value = formattedElec,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }
                            viewModel.onElectricityPriceChange(digitsOnly)
                        },
                        label = "Điện (đ/kWh)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !uiState.isLoading
                    )

                    val formattedWater = remember(uiState.waterPrice) { formatVndNumber(uiState.waterPrice) }
                    CustomTextField(
                        value = formattedWater,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }
                            viewModel.onWaterPriceChange(digitsOnly)
                        },
                        label = "Nước (đ/m³)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !uiState.isLoading
                    )
                }

                // Location section for standalone rooms
                if (uiState.belongsToProperty == null) {
                    FormSectionTitle(title = "Vị trí")
                    if (uiState.address.isNotBlank() && (cloneFromRoomId != null || isEditMode)) {
                        Text(
                            text = "Khu vực: ${uiState.address}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral500
                        )
                    }
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
                        enabled = !uiState.isLoading,
                        isLoading = isLocationLoading && provinces.isEmpty()
                    )

                    LocationDropdown(
                        label = "Phường/Xã/Khu vực",
                        items = wards,
                        selectedItemName = selectedWard?.name ?: "",
                        onItemSelected = { selectedWard = it },
                        getItemName = { it.name },
                        enabled = !uiState.isLoading && selectedProvince != null
                    )

                    AddressSuggestionField(
                        value = uiState.detailedAddress,
                        onValueChange = { viewModel.onDetailedAddressChange(it) },
                        label = "Địa chỉ chi tiết (Số nhà, tên đường)",
                        suggestions = suggestions,
                        onSuggestionSelected = { suggestion ->
                            viewModel.onDetailedAddressChange(suggestion.displayName)
                            val newLatLng = LatLng(suggestion.lat, suggestion.lon)
                            markerState.position = newLatLng
                            scope.launch {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 17f)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )

                    MapSection(cameraPositionState, markerState)
                }

                
                CustomTextField(
                    value = uiState.description, 
                    onValueChange = { viewModel.onDescriptionChange(it) }, 
                    label = "Mô tả riêng cho phòng này", 
                    modifier = Modifier.fillMaxWidth().height(100.dp), 
                    singleLine = false,
                    enabled = !uiState.isLoading
                )

                HorizontalDivider(color = Neutral300.copy(alpha = 0.3f))

                // Image Upload Section
                FormSectionTitle(title = "Hình ảnh phòng")
                
                if (viewModel.uploadedImages.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable { if (!uiState.isLoading) photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        color = Neutral50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = Neutral500, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Bấm để tải lên hình ảnh", style = MaterialTheme.typography.bodyMedium, color = Neutral500)
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(viewModel.uploadedImages, key = { it.id }) { imgUI ->
                            ImageUploadItem(
                                item = imgUI,
                                onCategoryChange = { newCat ->
                                    val index = viewModel.uploadedImages.indexOfFirst { it.id == imgUI.id }
                                    if (index != -1) {
                                        viewModel.uploadedImages[index] = imgUI.copy(category = newCat)
                                    }
                                },
                                onDelete = {
                                    viewModel.uploadedImages.removeIf { it.id == imgUI.id }
                                },
                                enabled = !uiState.isLoading
                            )
                        }
                        item {
                            Surface(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 140.dp)
                                    .clickable { if (!uiState.isLoading) photoPickerLauncher.launch("image/*") },
                                shape = RoundedCornerShape(12.dp),
                                color = Neutral50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, null, tint = Neutral500)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Neutral300.copy(alpha = 0.3f))

                FormSectionTitle(title = "Diện tích & Tiện ích")
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomTextField(
                        value = uiState.totalArea, 
                        onValueChange = { viewModel.onTotalAreaChange(it) }, 
                        label = "Tổng diện tích toàn bộ (m²)", 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    )

                    CustomTextField(
                        value = uiState.capacity,
                        onValueChange = { viewModel.onCapacityChange(it.filter { char -> char.isDigit() }) },
                        label = "Số người ở tối đa",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    )
                }

                // Detailed Areas Section
                viewModel.detailedAreas.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = item.roomName, 
                            onValueChange = { viewModel.detailedAreas[index] = item.copy(roomName = it) }, 
                            label = "Tên (VD: Gác lửng)", 
                            modifier = Modifier.weight(1.3f),
                            enabled = !uiState.isLoading
                        )
                        CustomTextField(
                            value = if (item.areaValue == 0.0) "" else item.areaValue.toString(), 
                            onValueChange = { viewModel.detailedAreas[index] = item.copy(areaValue = it.toDoubleOrNull() ?: 0.0) }, 
                            label = "m²", 
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                            modifier = Modifier.weight(0.7f),
                            enabled = !uiState.isLoading
                        )
                        IconButton(
                            onClick = { if (!uiState.isLoading) viewModel.detailedAreas.removeAt(index) }, 
                            colors = IconButtonDefaults.iconButtonColors(contentColor = ErrorRose),
                            enabled = !uiState.isLoading
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { if (!uiState.isLoading) viewModel.detailedAreas.add(DetailedArea(id = UUID.randomUUID().toString(), roomName = "", areaValue = 0.0)) }, 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = MaterialTheme.shapes.small, 
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                    enabled = !uiState.isLoading
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm diện tích chi tiết (Gác, sân...)", style = MaterialTheme.typography.bodyMedium)
                }

                // Amenities
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.amenities.forEachIndexed { index, amenity ->
                        AmenityRow(
                            amenity = amenity,
                            onToggle = { viewModel.amenities[index] = amenity.copy(isChecked = !amenity.isChecked) },
                            onAmountChange = { newVal -> viewModel.amenities[index] = amenity.copy(compensationAmount = newVal) },
                            enabled = !uiState.isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(
                    text = if (isEditMode) "Cập nhật phòng" else "Lưu thông tin phòng", 
                    onClick = { 
                        val missingFields = mutableListOf<String>()
                        if (uiState.title.isBlank()) missingFields.add("Tên/Số phòng")
                        
                        val priceVal = uiState.price.replace(".", "").toLongOrNull() ?: -1L
                        if (uiState.price.isBlank() || priceVal < 0) missingFields.add("Giá thuê (phải lớn hơn 0)")
                        
                        val areaVal = uiState.totalArea.toDoubleOrNull() ?: -1.0
                        if (uiState.totalArea.isBlank() || areaVal < 0) missingFields.add("Diện tích (phải lớn hơn 0)")
                        
                        if (uiState.belongsToProperty == null) {
                            if (selectedProvince == null && uiState.address.isBlank()) missingFields.add("Tỉnh/Thành phố")
                            if (selectedWard == null && uiState.address.isBlank()) missingFields.add("Phường/Xã")
                            if (uiState.detailedAddress.isBlank()) missingFields.add("Địa chỉ chi tiết")
                        }
                        
                        if (missingFields.isNotEmpty()) {
                            showSnackbar("Vui lòng điền đầy đủ: ${missingFields.joinToString(", ")}")
                        } else {
                            viewModel.save(
                                propertyId = propertyId?.takeIf { it.isNotBlank() && it != "{propertyId}" },
                                province = selectedProvince?.name,
                                ward = selectedWard?.name,
                                lat = markerState.position.latitude,
                                lon = markerState.position.longitude,
                                context = context
                            )
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

            }
        }

        if (showDiscardDialog) {
            val dialogTitle = when {
                isEditMode -> "Hủy chỉnh sửa?"
                cloneFromRoomId != null -> "Hủy sao chép?"
                else -> "Hủy đăng phòng?"
            }
            val confirmText = when {
                isEditMode -> "Bỏ chỉnh sửa"
                cloneFromRoomId != null -> "Bỏ sao chép"
                else -> "Hủy đăng"
            }
            val dismissText = if (isEditMode) "Tiếp tục sửa" else "Tiếp tục đăng"

            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
                text = { Text("Mọi thông tin bạn vừa nhập sẽ bị mất. Bạn có chắc chắn muốn thoát?") },
                confirmButton = {
                    Button(
                        onClick = { 
                            showDiscardDialog = false
                            onNavigateBack() 
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

@Composable
fun PropertyInfoBanner(property: Property) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PrimarySurface,
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HomeWork, contentDescription = null, tint = PrimaryMain)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Đang thêm vào: ${property.name}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(text = property.address, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageUploadItem(
    item: RoomImageUI,
    onCategoryChange: (String) -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Neutral100)
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.uri ?: item.url ?: item.resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                enabled = enabled
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category Dropdown
        Box {
            Surface(
                onClick = { if (enabled) expanded = true },
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.category, 
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp), tint = Neutral500)
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                ImageCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onCategoryChange(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AmenityRow(
    amenity: AmenityItem,
    onToggle: () -> Unit,
    onAmountChange: (String) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGrey, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Checkbox(
                checked = amenity.isChecked, 
                onCheckedChange = { onToggle() }, 
                enabled = enabled
            )
            Text(text = amenity.name, style = MaterialTheme.typography.bodyMedium)
        }
        if (amenity.isChecked) {
            SmallTextField(
                value = amenity.compensationAmount,
                onValueChange = { if (it.all { it.isDigit() }) onAmountChange(it) },
                label = "Đền bù (đ)",
                modifier = Modifier.width(120.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
        }
    }
}

@Composable
fun MapSection(cameraPositionState: com.google.maps.android.compose.CameraPositionState, markerState: com.google.maps.android.compose.MarkerState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ghim vị trí trên bản đồ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Card(modifier = Modifier.fillMaxWidth().height(200.dp), shape = MaterialTheme.shapes.medium) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                onMapClick = { markerState.position = it }
            ) {
                Marker(state = markerState, title = "Vị trí trọ")
            }
        }
    }
}

@Composable
fun FormSectionTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true)
@Composable
fun RoomFormScreenPreview() {
    EzRoomTheme {
        RoomFormScreen()
    }
}
