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
import com.example.ezroom.data.model.MockData
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFormScreen(
    isEditMode: Boolean = false, 
    propertyId: String? = null,
    cloneFromRoomId: String? = null,
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    locationViewModel: LocationViewModel = viewModel()
) {
    // State definitions
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val showSnackbar = LocalSnackbarProvider.current

    var showDiscardDialog by remember { mutableStateOf(false) }

    // Location States
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLocationLoading by locationViewModel.isLoading.collectAsState()

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

    var title by remember { mutableStateOf("") }
    var detailedAddress by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var electricityPrice by remember { mutableStateOf("3500") }
    var waterPrice by remember { mutableStateOf("15000") }
    
    // Map State
    val danangCenter = remember { LatLng(16.0544, 108.2022) }
    val markerState = rememberMarkerState(position = danangCenter)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(danangCenter, 15f)
    }

    var selectedStructure by remember { mutableStateOf(RoomStructure.SINGLE) }
    var isStructureDropdownExpanded by remember { mutableStateOf(false) }

    var totalArea by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val detailedAreas = remember { mutableStateListOf<DetailedArea>() }
    val amenities = remember {
        mutableStateListOf(
            AmenityItem("Wifi"), AmenityItem("Điều hòa"), AmenityItem("Giường"), AmenityItem("Tủ quần áo")
        )
    }
    val uploadedImages = remember { mutableStateListOf<RoomImageUI>() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            uris.forEach { uri ->
                uploadedImages.add(RoomImageUI(uri = uri))
            }
        }
    )

    // Pre-fill logic for Edit or Clone
    LaunchedEffect(cloneFromRoomId, isEditMode) {
        val targetId = cloneFromRoomId ?: if (isEditMode) "some_id" else null // Simplified
        val sourceRoom = MockData.rooms.find { it.id == targetId }
        
        sourceRoom?.let { room ->
            title = if (cloneFromRoomId != null) "${room.title} (Bản sao)" else room.title
            price = room.price.toString()
            electricityPrice = room.electricityPrice.toString()
            waterPrice = room.waterPrice.toString()
            description = room.description
            totalArea = room.floorArea.toString()
            selectedStructure = room.structure
            
            // Fill detailed areas
            detailedAreas.clear()
            detailedAreas.addAll(room.detailedAreas)
            
            // Fill amenities
            amenities.forEachIndexed { index, item ->
                val match = room.amenities.find { it.name == item.name }
                if (match != null) {
                    amenities[index] = item.copy(isChecked = true, compensationAmount = match.compensationAmount.toString())
                }
            }

            // Fill images
            uploadedImages.clear()
            uploadedImages.addAll(room.images.map { 
                RoomImageUI(url = it.url, resId = it.resId, category = it.category ?: "Khác") 
            })
        }
    }

    val belongsToProperty = remember { MockData.properties.find { it.id == propertyId } }

    val isFormValid = title.isNotEmpty() && 
                      (belongsToProperty != null || (selectedProvince != null && selectedWard != null)) &&
                      detailedAddress.isNotEmpty() && price.isNotEmpty() && totalArea.isNotEmpty()

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
                        IconButton(onClick = { showDiscardDialog = true }, enabled = !isLoading) { 
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
                if (belongsToProperty != null) {
                    PropertyInfoBanner(belongsToProperty)
                }

                FormSectionTitle(title = "Thông tin cơ bản")
                
                CustomTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = "Tên/Số phòng (VD: Phòng 101)", 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Structure Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = selectedStructure.displayName, 
                        onValueChange = {}, 
                        readOnly = true,
                        label = "Loại phòng",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStructureDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().clickable { isStructureDropdownExpanded = true },
                        enabled = !isLoading
                    )
                    DropdownMenu(
                        expanded = isStructureDropdownExpanded, 
                        onDismissRequest = { isStructureDropdownExpanded = false }
                    ) {
                        RoomStructure.entries.forEach { structure ->
                            DropdownMenuItem(
                                text = { Text(structure.displayName) },
                                onClick = { 
                                    selectedStructure = structure
                                    isStructureDropdownExpanded = false 
                                }
                            )
                        }
                    }
                }

                CustomTextField(
                    value = price, 
                    onValueChange = { price = it }, 
                    label = "Giá thuê / tháng (VND)", 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                FormSectionTitle(title = "Chi phí dịch vụ")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomTextField(
                        value = electricityPrice,
                        onValueChange = { if (it.all { it.isDigit() }) electricityPrice = it },
                        label = "Điện (đ/kWh)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )
                    CustomTextField(
                        value = waterPrice,
                        onValueChange = { if (it.all { it.isDigit() }) waterPrice = it },
                        label = "Nước (đ/m³)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )
                }

                if (belongsToProperty == null) {
                    FormSectionTitle(title = "Vị trí")
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
                        enabled = !isLoading,
                        isLoading = isLocationLoading && provinces.isEmpty()
                    )

                    LocationDropdown(
                        label = "Phường/Xã/Khu vực",
                        items = wards,
                        selectedItemName = selectedWard?.name ?: "",
                        onItemSelected = { selectedWard = it },
                        getItemName = { it.name },
                        enabled = !isLoading && selectedProvince != null
                    )
                }

                CustomTextField(
                    value = detailedAddress,
                    onValueChange = { detailedAddress = it },
                    label = if (belongsToProperty != null) "Vị trí trong dãy (VD: Tầng 2)" else "Địa chỉ chi tiết (Số nhà, tên đường)",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                if (belongsToProperty == null) {
                    MapSection(cameraPositionState, markerState)
                }
                
                CustomTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = "Mô tả riêng cho phòng này", 
                    modifier = Modifier.fillMaxWidth().height(100.dp), 
                    singleLine = false,
                    enabled = !isLoading
                )

                HorizontalDivider(color = Neutral300.copy(alpha = 0.3f))

                // Image Upload Section
                FormSectionTitle(title = "Hình ảnh phòng")
                
                if (uploadedImages.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable { if (!isLoading) photoPickerLauncher.launch("image/*") },
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
                        items(uploadedImages, key = { it.id }) { imgUI ->
                            ImageUploadItem(
                                item = imgUI,
                                onCategoryChange = { newCat ->
                                    val index = uploadedImages.indexOfFirst { it.id == imgUI.id }
                                    if (index != -1) {
                                        uploadedImages[index] = imgUI.copy(category = newCat)
                                    }
                                },
                                onDelete = {
                                    uploadedImages.removeIf { it.id == imgUI.id }
                                },
                                enabled = !isLoading
                            )
                        }
                        item {
                            Surface(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 140.dp)
                                    .clickable { if (!isLoading) photoPickerLauncher.launch("image/*") },
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
                
                CustomTextField(
                    value = totalArea, 
                    onValueChange = { totalArea = it }, 
                    label = "Tổng diện tích toàn bộ (m²)", 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Detailed Areas Section
                detailedAreas.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = item.roomName, 
                            onValueChange = { detailedAreas[index] = item.copy(roomName = it) }, 
                            label = "Tên (VD: Gác lửng)", 
                            modifier = Modifier.weight(1.3f),
                            enabled = !isLoading
                        )
                        CustomTextField(
                            value = if (item.areaValue == 0.0) "" else item.areaValue.toString(), 
                            onValueChange = { detailedAreas[index] = item.copy(areaValue = it.toDoubleOrNull() ?: 0.0) }, 
                            label = "m²", 
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                            modifier = Modifier.weight(0.7f),
                            enabled = !isLoading
                        )
                        IconButton(
                            onClick = { if (!isLoading) detailedAreas.removeAt(index) }, 
                            colors = IconButtonDefaults.iconButtonColors(contentColor = ErrorRose),
                            enabled = !isLoading
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { if (!isLoading) detailedAreas.add(DetailedArea(id = UUID.randomUUID().toString(), roomName = "", areaValue = 0.0)) }, 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = MaterialTheme.shapes.small, 
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryMain),
                    enabled = !isLoading
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm diện tích chi tiết (Gác, sân...)", style = MaterialTheme.typography.bodyMedium)
                }

                // Amenities
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    amenities.forEachIndexed { index, amenity ->
                        AmenityRow(
                            amenity = amenity,
                            onToggle = { amenities[index] = amenity.copy(isChecked = !amenity.isChecked) },
                            onAmountChange = { newVal -> amenities[index] = amenity.copy(compensationAmount = newVal) },
                            enabled = !isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(
                    text = if (isEditMode) "Cập nhật phòng" else "Lưu thông tin phòng", 
                    onClick = { 
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                showSnackbar(if (isEditMode) "Cập nhật phòng thành công" else "Lưu thông tin phòng thành công")
                                onSaveSuccess()
                            }
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading
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

        if (isLoading) LoadingWidget()
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
