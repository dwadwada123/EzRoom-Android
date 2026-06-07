package com.example.ezroom.ui.host.room

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.SmallTextField
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.data.model.*
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
    val label: String = "Ảnh mới"
)

val ImageLabels = listOf("Ảnh phòng khách", "Ảnh phòng ngủ", "Ảnh WC", "Ảnh mặt tiền")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFormScreen(isEditMode: Boolean = false, onNavigateBack: () -> Unit = {}) {
    // State definitions
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedStructure by remember { mutableStateOf(RoomStructure.SINGLE) }
    var isStructureDropdownExpanded by remember { mutableStateOf(false) }

    var totalArea by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val detailedAreas = remember { mutableStateListOf<DetailedArea>() }

    val amenities = remember {
        mutableStateListOf(
            AmenityItem("Wifi"),
            AmenityItem("Điều hòa"),
            AmenityItem("Giường"),
            AmenityItem("Tủ quần áo")
        )
    }
    
    val uploadedImages = remember { mutableStateListOf<RoomImageUI>() }

    val isFormValid = title.isNotEmpty() && description.isNotEmpty() && 
                      price.isNotEmpty() && totalArea.isNotEmpty()

    // Main layout container
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // Top app bar
            topBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CenterAlignedTopAppBar(
                        title = { 
                            Text(
                                text = if (isEditMode) "Chỉnh sửa phòng trọ" else "Đăng tin mới", 
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            ) 
                        },
                        navigationIcon = { 
                            IconButton(onClick = onNavigateBack, enabled = !isLoading) { 
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng") 
                            } 
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        ) { innerPadding ->
            // Content scroll area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                FormSectionTitle(title = "Thông tin cơ bản")
                
                // Input fields group
                CustomTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = "Tiêu đề bài đăng", 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = selectedStructure.displayName, 
                        onValueChange = {}, 
                        readOnly = true,
                        label = "Cấu trúc cho thuê",
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStructureDropdownExpanded) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLoading) { isStructureDropdownExpanded = !isStructureDropdownExpanded },
                        enabled = !isLoading
                    )
                    DropdownMenu(
                        expanded = isStructureDropdownExpanded, 
                        onDismissRequest = { isStructureDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
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
                
                CustomTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = "Mô tả chi tiết phòng trọ", 
                    modifier = Modifier.fillMaxWidth().height(120.dp), 
                    singleLine = false,
                    enabled = !isLoading
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

                FormSectionTitle(title = "Diện tích căn hộ")
                
                CustomTextField(
                    value = totalArea, 
                    onValueChange = { totalArea = it }, 
                    label = "Diện tích tổng toàn căn (m²)", 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                detailedAreas.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = item.roomName, 
                            onValueChange = { detailedAreas[index] = item.copy(roomName = it) }, 
                            label = "Tên phòng (VD: Phòng ngủ 1)", 
                            modifier = Modifier.weight(1.3f),
                            enabled = !isLoading
                        )
                        CustomTextField(
                            value = item.areaValue.toString(), 
                            onValueChange = { detailedAreas[index] = item.copy(areaValue = it.toDoubleOrNull() ?: 0.0) }, 
                            label = "Diện tích (m²)", 
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                            modifier = Modifier.weight(0.9f),
                            enabled = !isLoading
                        )
                        IconButton(
                            onClick = { if (!isLoading) detailedAreas.removeAt(index) }, 
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            enabled = !isLoading
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa dòng")
                        }
                    }
                }

                // Action buttons row
                OutlinedButton(
                    onClick = { if (!isLoading) detailedAreas.add(DetailedArea(id = UUID.randomUUID().toString(), roomName = "", areaValue = 0.0)) }, 
                    modifier = Modifier.fillMaxWidth(), 
                    shape = MaterialTheme.shapes.small, 
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm diện tích chi tiết từng phòng (nếu có)", style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

                FormSectionTitle(title = "Tiện ích và Đền bù")
                
                // Input fields group: Amenities with compensation
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    amenities.forEachIndexed { index, amenity ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isLoading) { 
                                        amenities[index] = amenity.copy(isChecked = !amenity.isChecked) 
                                    }, 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = amenity.isChecked, 
                                    onCheckedChange = { checked -> 
                                        if (!isLoading) {
                                            amenities[index] = amenity.copy(isChecked = checked) 
                                        }
                                    }, 
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                                    enabled = !isLoading
                                )
                                Text(
                                    text = amenity.name, 
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            
                            if (amenity.isChecked) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 48.dp, top = 4.dp, bottom = 8.dp)
                                ) {
                                    SmallTextField(
                                        value = amenity.compensationAmount,
                                        onValueChange = { newVal ->
                                            if (newVal.all { it.isDigit() }) {
                                                amenities[index] = amenity.copy(compensationAmount = newVal)
                                            }
                                        },
                                        label = "Tiền đền bù (đ)",
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        enabled = !isLoading
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

                FormSectionTitle(title = "Hình ảnh thực tế")
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .clickable(enabled = !isLoading) { uploadedImages.add(RoomImageUI()) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload Image", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Bấm để tải ảnh lên từ thư viện máy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (uploadedImages.isNotEmpty()) {
                    uploadedImages.chunked(2).forEach { rowImages ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowImages.forEach { roomImage ->
                                val index = uploadedImages.indexOf(roomImage)
                                Box(modifier = Modifier.weight(1f)) {
                                    ImageCardWithLabel(
                                        roomImage = roomImage, 
                                        onDelete = { if (!isLoading) uploadedImages.removeAt(index) },
                                        onLabelChange = { newLabel ->
                                            if (!isLoading) {
                                                uploadedImages[index] = roomImage.copy(label = newLabel)
                                            }
                                        },
                                        enabled = !isLoading
                                    )
                                }
                            }
                            if (rowImages.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(
                    text = if (isEditMode) "Cập nhật bài đăng" else "Đăng bài ngay", 
                    onClick = { 
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                // Logic for data packaging
                                val finalAmenities = amenities
                                    .filter { it.isChecked }
                                    .map { Amenity(it.name, null) } // Real app would map compensation too

                                delay(1500)
                                isLoading = false
                                onNavigateBack()
                            }
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading
                )
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Composable
fun FormSectionTitle(title: String) {
    Text(
        text = title, 
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCardWithLabel(
    roomImage: RoomImageUI, 
    onDelete: () -> Unit,
    onLabelChange: (String) -> Unit,
    enabled: Boolean = true
) {
    var isLabelDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = MaterialTheme.shapes.medium, 
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), 
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(Color.LightGray)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ảnh minh họa", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                IconButton(
                    onClick = onDelete, 
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                    enabled = enabled
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa", modifier = Modifier.size(16.dp))
                }
            }

            Box(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, MaterialTheme.shapes.small)
                        .clickable(enabled = enabled) { isLabelDropdownExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = roomImage.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                
                DropdownMenu(
                    expanded = isLabelDropdownExpanded, 
                    onDismissRequest = { isLabelDropdownExpanded = false }
                ) {
                    ImageLabels.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(label, style = MaterialTheme.typography.bodySmall) },
                            onClick = { 
                                onLabelChange(label)
                                isLabelDropdownExpanded = false 
                            },
                            enabled = enabled
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomFormScreenPreview() {
    EzRoomTheme {
        RoomFormScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RoomFormScreenEditPreview() {
    EzRoomTheme {
        RoomFormScreen(isEditMode = true)
    }
}
