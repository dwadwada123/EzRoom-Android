package com.example.ezroom.ui.renter.discovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.SearchRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetSearchMetadataUseCase
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.*
import com.example.ezroom.viewmodel.LocationViewModel
import kotlin.math.roundToInt

// UI Component: Advanced Filter for Room Search
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFilterScreen(
    initialParams: FilterParams = FilterParams(),
    onFilterApply: (FilterParams) -> Unit = {},
    onDismiss: () -> Unit = {},
    locationViewModel: LocationViewModel = viewModel(),
    viewModel: FilterViewModel = viewModel(
        factory = viewModelFactory {
            FilterViewModel(GetSearchMetadataUseCase(SearchRepositoryImpl()))
        },
    ),
) {
    // State Management: UI State from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // State Management: Location data
    val provinces by locationViewModel.provinces.collectAsState()
    val wards by locationViewModel.wards.collectAsState()
    val isLoadingLocation by locationViewModel.isLoading.collectAsState()
    val loadError by locationViewModel.error.collectAsState()

    // State Management: Selection State
    var selectedProvince by remember(initialParams) { mutableStateOf<Province?>(if (initialParams.selectedDistrict.isNotBlank()) Province(initialParams.selectedDistrict, "") else null) }
    var selectedWard by remember(initialParams) { mutableStateOf<Ward?>(if (initialParams.selectedWard.isNotBlank()) Ward(initialParams.selectedWard, "") else null) }
    
    var selectedRoomType by remember(initialParams) { mutableStateOf(initialParams.selectedRoomType) }
    var priceRange by remember(initialParams) { mutableStateOf(if (initialParams.priceMin > 0f || initialParams.priceMax < 30f) initialParams.priceRange else 0f..30f) }
    var areaRange by remember(initialParams) { mutableStateOf(10f..100f) }
    var selectedAmenities by remember(initialParams) { mutableStateOf(initialParams.selectedAmenities.toSet()) }

    Scaffold(
        containerColor = Neutral50,
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
            ) {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Bộ lọc tìm kiếm", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.clip(CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                        }
                    },
                    actions = {
                    TextButton(
                        onClick = {
                            viewModel.resetFilters()
                            selectedProvince = null
                            selectedWard = null
                            selectedRoomType = ""
                            priceRange = 1f..15f
                            areaRange = 15f..50f
                            selectedAmenities = setOf()
                        },
                    ) {
                        Text("Xóa hết", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                PrimaryButton(
                    text = "ÁP DỤNG BỘ LỌC",
                    onClick = {
                        val params = FilterParams(
                            selectedDistrict = selectedProvince?.name ?: "",
                            selectedWard = selectedWard?.name ?: "",
                            priceMin = priceRange.start,
                            priceMax = priceRange.endInclusive,
                            selectedAreaRange = if (areaRange.start > 10f || areaRange.endInclusive < 100f) "${areaRange.start.roundToInt()} - ${areaRange.endInclusive.roundToInt()} m²" else "",
                            selectedRoomType = selectedRoomType,
                            selectedAmenities = selectedAmenities.toList()
                        )
                        viewModel.updateParams(params)
                        onFilterApply(params)
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                item {
                    SectionHeader("Vị trí")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterDropdown(
                            label = "Tỉnh/Thành phố",
                            value = selectedProvince?.name ?: "Chọn Tỉnh/Thành",
                            options = provinces.map { it.name },
                            onSelect = { name ->
                                val province = provinces.find { it.name == name }
                                if (province != null) {
                                    selectedProvince = province
                                    selectedWard = null
                                    locationViewModel.selectProvince(province.code)
                                }
                            }
                        )

                        FilterDropdown(
                            label = "Phường/Xã/Khu vực",
                            value = selectedWard?.name ?: "Tất cả",
                            options = wards.map { it.name },
                            onSelect = { name ->
                                selectedWard = wards.find { it.name == name }
                            },
                            enabled = selectedProvince != null
                        )
                    }
                    
                    if (isLoadingLocation) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }

                    loadError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        TextButton(onClick = { locationViewModel.fetchProvinces() }) {
                            Text("Thử lại", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Loại hình cho thuê")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.roomTypes.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { type ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FilterChip(
                                        text = type,
                                        selected = selectedRoomType == type,
                                        onSelect = { 
                                            selectedRoomType = if (selectedRoomType == type) "" else type 
                                        },
                                    )
                                    }
                                }
                                if (row.size < 2) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Khoảng giá (Triệu VNĐ/tháng)")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    RangeSlider(
                        value = priceRange,
                        onValueChange = { priceRange = it },
                        valueRange = 0f..30f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${priceRange.start.roundToInt()} Tr", fontWeight = FontWeight.Bold, color = PrimaryMain)
                        Text("${priceRange.endInclusive.roundToInt()} Tr", fontWeight = FontWeight.Bold, color = PrimaryMain)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Diện tích sử dụng (m²)")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    RangeSlider(
                        value = areaRange,
                        onValueChange = { areaRange = it },
                        valueRange = 10f..100f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${areaRange.start.roundToInt()} m²", fontWeight = FontWeight.Bold, color = PrimaryMain)
                        Text("${areaRange.endInclusive.roundToInt()} m²", fontWeight = FontWeight.Bold, color = PrimaryMain)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Tiện ích & Dịch vụ")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.amenities) { amenity ->
                            val isChecked = selectedAmenities.contains(amenity)
                            FilterChip(
                                text = amenity,
                                selected = isChecked,
                                onSelect = {
                                    selectedAmenities = if (isChecked) selectedAmenities - amenity else selectedAmenities + amenity
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onSelect: () -> Unit) {
    Surface(
        onClick = onSelect,
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else OnBackgroundLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(value = false) }
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        Box {
            Surface(
                onClick = { if (enabled) expanded = true },
                shape = MaterialTheme.shapes.small,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                color = if (enabled) Color.White else Neutral100
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = value, 
                        fontSize = 14.sp, 
                        maxLines = 1, 
                        modifier = Modifier.weight(1f), 
                        overflow = TextOverflow.Ellipsis,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown, 
                        contentDescription = null, 
                        tint = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.5f)
                    )
                }
            }
            if (enabled) {
                DropdownMenu(
                    expanded = expanded, 
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth(0.9f)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) }, 
                            onClick = {
                                onSelect(option)
                                expanded = false
                            }
                        )
                    }
                }
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
