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
import java.util.UUID

// --- DATA MODELS ---

enum class RoomStructure(val displayName: String) {
    SINGLE("Phòng đơn"), WHOLE("Nguyên căn"), APARTMENT("Căn hộ")
}

// Chuyển sang val để đảm bảo tính bất biến (Immutability), giúp Compose quản lý State ổn định hơn
data class DetailedArea(
    val id: String = UUID.randomUUID().toString(), 
    val roomName: String = "", 
    val areaValue: String = ""
)

data class AmenityItem(
    val name: String, 
    val defaultSpec: String, 
    val isChecked: Boolean = false
)

data class RoomImage(
    val id: String = UUID.randomUUID().toString(), 
    val uri: Uri?, 
    val label: String = "Ảnh phòng khách"
)

val ImageLabels = listOf("Ảnh phòng khách", "Ảnh phòng ngủ", "Ảnh WC", "Ảnh mặt tiền")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFormScreen(isEditMode: Boolean = false, onNavigateBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()

    // --- STATES QUẢN LÝ DỮ LIỆU ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedStructure by remember { mutableStateOf(RoomStructure.SINGLE) }
    var isStructureDropdownExpanded by remember { mutableStateOf(false) }

    var totalArea by remember { mutableStateOf("") }
    
    // Sử dụng mutableStateListOf để quản lý danh sách động (thêm/xóa dòng)
    val detailedAreas = remember { mutableStateListOf<DetailedArea>() }

    val amenities = remember {
        mutableStateListOf(
            AmenityItem("Wifi", "Băng thông cao"),
            AmenityItem("Điều hòa", "Inverter tiết kiệm điện"),
            AmenityItem("Giường", "1m8 x 2m"),
            AmenityItem("Tủ quần áo", "Gỗ 2 cánh")
        )
    }
    
    val uploadedImages = remember { mutableStateListOf<RoomImage>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = if (isEditMode) "Chỉnh sửa phòng trọ" else "Đăng tin mới", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = { 
                    IconButton(onClick = onNavigateBack) { 
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng") 
                    } 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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

            // --- 1. THÔNG TIN CƠ BẢN ---
            FormSectionTitle(title = "Thông tin cơ bản")
            
            OutlinedTextField(
                value = title, 
                onValueChange = { title = it }, 
                label = { Text("Tiêu đề bài đăng") }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = MaterialTheme.shapes.small
            )

            // Dropdown chọn cấu trúc cho thuê (Phòng đơn / Nguyên căn / Căn hộ)
            ExposedDropdownMenuBox(
                expanded = isStructureDropdownExpanded,
                onExpandedChange = { isStructureDropdownExpanded = !isStructureDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStructure.displayName, 
                    onValueChange = {}, 
                    readOnly = true, 
                    label = { Text("Cấu trúc cho thuê") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStructureDropdownExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), 
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
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

            OutlinedTextField(
                value = price, 
                onValueChange = { price = it }, 
                label = { Text("Giá thuê / tháng (VND)") }, 
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                modifier = Modifier.fillMaxWidth(), 
                shape = MaterialTheme.shapes.small
            )
            
            OutlinedTextField(
                value = description, 
                onValueChange = { description = it }, 
                label = { Text("Mô tả chi tiết phòng trọ") }, 
                modifier = Modifier.fillMaxWidth().height(120.dp), 
                shape = MaterialTheme.shapes.small, 
                maxLines = 5
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

            // --- 2. KHỐI DIỆN TÍCH (Tính năng sinh dòng tự động theo yêu cầu) ---
            FormSectionTitle(title = "Diện tích căn hộ")
            
            OutlinedTextField(
                value = totalArea, 
                onValueChange = { totalArea = it }, 
                label = { Text("Diện tích tổng toàn căn (m²)") }, 
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                modifier = Modifier.fillMaxWidth(), 
                shape = MaterialTheme.shapes.small
            )

            // Hiển thị danh sách các dòng chi tiết được sinh thêm
            detailedAreas.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ô bên trái: Nhập tên phòng
                    OutlinedTextField(
                        value = item.roomName, 
                        onValueChange = { detailedAreas[index] = item.copy(roomName = it) }, 
                        label = { Text("Tên phòng (VD: Phòng ngủ 1)") }, 
                        modifier = Modifier.weight(1.3f), 
                        shape = MaterialTheme.shapes.small
                    )
                    // Ô bên phải: Nhập diện tích phòng đó
                    OutlinedTextField(
                        value = item.areaValue, 
                        onValueChange = { detailedAreas[index] = item.copy(areaValue = it) }, 
                        label = { Text("Diện tích (m²)") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                        modifier = Modifier.weight(0.9f), 
                        shape = MaterialTheme.shapes.small
                    )
                    // Icon nút Thùng rác nhỏ màu đỏ để xóa dòng
                    IconButton(
                        onClick = { detailedAreas.removeAt(index) }, 
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa dòng")
                    }
                }
            }

            // Nút bấm có dấu (+) để thêm dòng mới
            OutlinedButton(
                onClick = { detailedAreas.add(DetailedArea()) }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = MaterialTheme.shapes.small, 
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm diện tích chi tiết từng phòng (nếu có)", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

            // --- 3. TIỆN ÍCH PHÒNG TRỌ (Checkbox kèm thông số mặc định) ---
            FormSectionTitle(title = "Tiện ích đi kèm")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                amenities.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { amenity ->
                            val index = amenities.indexOf(amenity)
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        // Cập nhật State bằng cách tạo bản sao mới (copy)
                                        amenities[index] = amenity.copy(isChecked = !amenity.isChecked) 
                                    }, 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = amenity.isChecked, 
                                    onCheckedChange = { checked -> 
                                        amenities[index] = amenity.copy(isChecked = checked) 
                                    }, 
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Column(modifier = Modifier.padding(start = 4.dp)) {
                                    Text(text = amenity.name, style = MaterialTheme.typography.bodyLarge)
                                    // Hiển thị thông số mặc định (VD: 1m8 x 2m) như yêu cầu
                                    Text(text = amenity.defaultSpec, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                        if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

            // --- 4. TẢI ẢNH VÀ GẮN NHÃN (Lưới ảnh kèm Dropdown) ---
            FormSectionTitle(title = "Hình ảnh thực tế")
            
            // Khung tải ảnh mô phỏng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .clickable { uploadedImages.add(RoomImage(uri = null)) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload Image", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bấm để tải ảnh lên từ thư viện máy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Lưới hiển thị ảnh và gắn nhãn
            if (uploadedImages.isNotEmpty()) {
                uploadedImages.chunked(2).forEach { rowImages ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowImages.forEach { roomImage ->
                            val index = uploadedImages.indexOf(roomImage)
                            Box(modifier = Modifier.weight(1f)) {
                                ImageCardWithLabel(
                                    roomImage = roomImage, 
                                    onDelete = { uploadedImages.removeAt(index) },
                                    onLabelChange = { newLabel ->
                                        uploadedImages[index] = roomImage.copy(label = newLabel)
                                    }
                                )
                            }
                        }
                        if (rowImages.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Nút hành động chính
            Button(
                onClick = { /* Xử lý gửi API tại đây */ }, 
                modifier = Modifier.fillMaxWidth().height(48.dp), 
                shape = MaterialTheme.shapes.medium, 
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isEditMode) "Cập nhật bài đăng" else "Đăng bài ngay", 
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Component hỗ trợ hiển thị tiêu đề các phân đoạn trong Form
 */
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

/**
 * Component hiển thị từng Card ảnh kèm Dropdown gắn nhãn phân loại
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCardWithLabel(
    roomImage: RoomImage, 
    onDelete: () -> Unit,
    onLabelChange: (String) -> Unit
) {
    var isLabelDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = MaterialTheme.shapes.medium, 
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), 
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Khu vực ảnh (Placeholder)
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(Color.LightGray)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ảnh minh họa", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                // Nút xóa nhanh ảnh
                IconButton(
                    onClick = onDelete, 
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa", modifier = Modifier.size(16.dp))
                }
            }

            // Dropdown gắn nhãn (Phòng khách, WC, Mặt tiền...)
            Box(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, MaterialTheme.shapes.small)
                        .clickable { isLabelDropdownExpanded = true }
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
                            }
                        )
                    }
                }
            }
        }
    }
}
