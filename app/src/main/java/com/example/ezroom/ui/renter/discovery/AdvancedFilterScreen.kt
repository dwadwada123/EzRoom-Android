package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.*
import kotlin.math.roundToInt

data class FilterParams(
    val selectedDistrict: String = "",
    val selectedWard: String = "",
    val priceRange: ClosedFloatingPointRange<Float> = 1f..10f,
    val selectedAreaRange: String = "",
    val selectedAmenities: List<String> = emptyList()
)

@Composable
fun AdvancedFilterScreen(
    // Event callbacks
    modifier: Modifier = Modifier,
    onFilterApply: (FilterParams) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    // State definitions
    var selectedDistrict by remember { mutableStateOf("") }
    var selectedWard by remember { mutableStateOf("") }
    var priceRange by remember { mutableStateOf(1f..10f) }
    var selectedAreaRange by remember { mutableStateOf("") }
    var selectedAmenities by remember { mutableStateOf(setOf<String>()) }

    val districts = listOf(
        "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7",
        "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12",
        "Quận Bình Thạnh", "Quận Gò Vấp", "Quận Phú Nhuận", "Quận Tân Bình"
    )

    val wards = listOf(
        "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
        "Phường Bến Nghé", "Phường Bến Thành", "Phường Cầu Kho"
    )

    val areaRanges = listOf("Dưới 20m²", "20m² - 30m²", "Trên 30m²")
    val amenities = listOf("WiFi", "Máy giặt", "Tủ lạnh", "Điều hòa", "Giờ giấc tự do", "Bảo vệ 24/7")

    // Main layout container
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top app bar section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bộ lọc nâng cao",
                style = Typography.titleMedium,
                color = OnBackgroundLight
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Đóng", tint = OrangePrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content scroll area
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section: Area selection
            item {
                Text(
                    text = "Khu vực",
                    style = Typography.bodyLarge,
                    color = OnBackgroundLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input fields group: District Dropdown
                var expandedDistrict by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        label = { Text("Chọn Quận/Huyện") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { 
                            IconButton(onClick = { expandedDistrict = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            cursorColor = OrangePrimary
                        )
                    )
                    DropdownMenu(
                        expanded = expandedDistrict,
                        onDismissRequest = { expandedDistrict = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        districts.forEach { district ->
                            DropdownMenuItem(
                                text = { Text(district) },
                                onClick = {
                                    selectedDistrict = district
                                    expandedDistrict = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input fields group: Ward Dropdown
                var expandedWard by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedWard,
                        onValueChange = {},
                        label = { Text("Chọn Phường/Xã") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { 
                            IconButton(onClick = { expandedWard = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            cursorColor = OrangePrimary
                        )
                    )
                    DropdownMenu(
                        expanded = expandedWard,
                        onDismissRequest = { expandedWard = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        wards.forEach { ward ->
                            DropdownMenuItem(
                                text = { Text(ward) },
                                onClick = {
                                    selectedWard = ward
                                    expandedWard = false
                                }
                            )
                        }
                    }
                }
            }

            // Section: Price range
            item {
                Text(
                    text = "Khoảng giá (Triệu đồng/tháng)",
                    style = Typography.bodyLarge,
                    color = OnBackgroundLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = priceRange.start.roundToInt().toString(),
                        onValueChange = { value ->
                            val minPrice = value.toFloatOrNull() ?: 1f
                            priceRange = minPrice..priceRange.endInclusive
                        },
                        label = { Text("Giá tối thiểu") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            cursorColor = OrangePrimary
                        )
                    )

                    OutlinedTextField(
                        value = priceRange.endInclusive.roundToInt().toString(),
                        onValueChange = { value ->
                            val maxPrice = value.toFloatOrNull() ?: 10f
                            priceRange = priceRange.start..maxPrice
                        },
                        label = { Text("Giá tối đa") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            cursorColor = OrangePrimary
                        )
                    )
                }
            }

            // Section: Area range
            item {
                Text(
                    text = "Diện tích",
                    style = Typography.bodyLarge,
                    color = OnBackgroundLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                areaRanges.forEach { area ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAreaRange == area,
                            onClick = { selectedAreaRange = area },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(area, style = Typography.bodyMedium, color = OnBackgroundLight)
                    }
                }
            }

            // Section: Amenities
            item {
                Text(
                    text = "Tiện ích",
                    style = Typography.bodyLarge,
                    color = OnBackgroundLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    amenities.chunked(2).forEach { rowAmenities ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowAmenities.forEach { amenity ->
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedAmenities.contains(amenity),
                                        onCheckedChange = {
                                            selectedAmenities = if (it) {
                                                selectedAmenities + amenity
                                            } else {
                                                selectedAmenities - amenity
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(amenity, style = Typography.bodyMedium, color = OnBackgroundLight)
                                }
                            }
                            if (rowAmenities.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selectedDistrict = ""
                    selectedWard = ""
                    priceRange = 1f..10f
                    selectedAreaRange = ""
                    selectedAmenities = setOf()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary)
            ) {
                Text("Xóa lọc")
            }

            Button(
                onClick = {
                    onFilterApply(
                        FilterParams(
                            selectedDistrict = selectedDistrict,
                            selectedWard = selectedWard,
                            priceRange = priceRange,
                            selectedAreaRange = selectedAreaRange,
                            selectedAmenities = selectedAmenities.toList()
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Áp dụng")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvancedFilterScreenPreview() {
    EzRoomTheme {
        AdvancedFilterScreen()
    }
}
