package com.example.ezroom.ui.renter.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.DiscoveryItem
import com.example.ezroom.domain.model.FilterParams
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RenterHomeUiState(
    val rawDiscoveryItems: List<DiscoveryItem> = emptyList(),
    val filteredDiscoveryItems: List<DiscoveryItem> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val filterParams: FilterParams = FilterParams(),
    val selectedCategory: String = "Tất cả",
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val error: String? = null
)

class RenterHomeViewModel(
    private val getDiscoveryItems: GetDiscoveryItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RenterHomeUiState())
    val uiState: StateFlow<RenterHomeUiState> = _uiState.asStateFlow()

    init {
        loadDiscoveryItems()
    }

    private fun loadDiscoveryItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(800) 
            
            getDiscoveryItems()
                .onEach { result ->
                    when (result) {
                        is Try.Success -> {
                            _uiState.update { state ->
                                state.copy(
                                    rawDiscoveryItems = result.value,
                                    filteredDiscoveryItems = applyFilters(
                                        result.value,
                                        state.query,
                                        state.filterParams,
                                        state.selectedCategory,
                                        state.userLatitude,
                                        state.userLongitude
                                    ),
                                    isLoading = false
                                )
                            }
                        }
                        is Try.Failure -> {
                            _uiState.update { it.copy(
                                error = result.error.toMessage(), 
                                isLoading = false 
                            ) }
                        }
                    }
                }
                .collect()
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { state ->
            state.copy(
                query = newQuery,
                filteredDiscoveryItems = applyFilters(
                    state.rawDiscoveryItems,
                    newQuery,
                    state.filterParams,
                    state.selectedCategory,
                    state.userLatitude,
                    state.userLongitude
                )
            )
        }
    }

    fun onFilterParamsChange(params: FilterParams) {
        _uiState.update { state ->
            state.copy(
                filterParams = params,
                filteredDiscoveryItems = applyFilters(
                    state.rawDiscoveryItems,
                    state.query,
                    params,
                    state.selectedCategory,
                    state.userLatitude,
                    state.userLongitude
                )
            )
        }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredDiscoveryItems = applyFilters(
                    state.rawDiscoveryItems,
                    state.query,
                    state.filterParams,
                    category,
                    state.userLatitude,
                    state.userLongitude
                )
            )
        }
    }

    fun onUserLocationChange(latitude: Double, longitude: Double) {
        _uiState.update { state ->
            state.copy(
                userLatitude = latitude,
                userLongitude = longitude,
                filteredDiscoveryItems = applyFilters(
                    state.rawDiscoveryItems,
                    state.query,
                    state.filterParams,
                    state.selectedCategory,
                    latitude,
                    longitude
                )
            )
        }
    }

    fun refresh() {
        loadDiscoveryItems()
    }

    private fun applyFilters(
        items: List<DiscoveryItem>,
        query: String,
        params: FilterParams,
        category: String,
        userLat: Double?,
        userLon: Double?
    ): List<DiscoveryItem> {
        val mapped = items.map { item ->
            val filteredRooms = item.rooms.filter { room ->
                // 1. Category Matching
                val categoryMatch = when (category) {
                    "Dãy trọ" -> item.property.type == com.example.ezroom.domain.model.PropertyType.COMPLEX
                    "Chung cư mini" -> room.structure == com.example.ezroom.domain.model.RoomStructure.APARTMENT
                    "Nhà riêng" -> room.structure == com.example.ezroom.domain.model.RoomStructure.WHOLE
                    "Ở ghép" -> room.structure == com.example.ezroom.domain.model.RoomStructure.SINGLE
                    else -> true
                }

                // 1.5. Advanced Filter Room Type Matching
                val roomTypeMatch = if (params.selectedRoomType.isBlank()) true else {
                    when (params.selectedRoomType) {
                        "Dãy / Tòa nhà", "COMPLEX" -> item.property.type == com.example.ezroom.domain.model.PropertyType.COMPLEX
                        "Phòng lẻ", "Phòng trọ lẻ", "SINGLE" -> item.property.type == com.example.ezroom.domain.model.PropertyType.SINGLE || room.propertyId == null || room.propertyId == "standalone" || room.propertyId.isBlank()
                        else -> room.structure.name.contains(params.selectedRoomType, ignoreCase = true) || item.property.type.name.contains(params.selectedRoomType, ignoreCase = true)
                    }
                }

                // 2. Query text matching
                val queryMatch = if (query.isBlank()) true else {
                    room.title.contains(query, ignoreCase = true) ||
                    room.address.contains(query, ignoreCase = true) ||
                    room.detailedAddress.contains(query, ignoreCase = true) ||
                    item.property.name.contains(query, ignoreCase = true)
                }

                // 3. Province/City matching (clean prefix for flexible matching)
                val cleanDistrict = params.selectedDistrict
                    .replace("Thành phố", "", ignoreCase = true)
                    .replace("Tỉnh", "", ignoreCase = true)
                    .replace("Quận", "", ignoreCase = true)
                    .replace("Huyện", "", ignoreCase = true)
                    .replace("TP.", "", ignoreCase = true)
                    .trim()
                val provinceMatch = if (cleanDistrict.isBlank()) true else {
                    room.address.contains(cleanDistrict, ignoreCase = true) ||
                    room.detailedAddress.contains(cleanDistrict, ignoreCase = true) ||
                    item.property.address.contains(cleanDistrict, ignoreCase = true) ||
                    item.property.detailedAddress.contains(cleanDistrict, ignoreCase = true) ||
                    cleanDistrict.contains(room.address, ignoreCase = true) ||
                    cleanDistrict.contains(item.property.address, ignoreCase = true)
                }

                // 4. Ward/Area matching (clean prefix for flexible matching)
                val cleanWard = params.selectedWard
                    .replace("Phường", "", ignoreCase = true)
                    .replace("Xã", "", ignoreCase = true)
                    .replace("Thị trấn", "", ignoreCase = true)
                    .replace("P.", "", ignoreCase = true)
                    .trim()
                val wardMatch = if (cleanWard.isBlank()) true else {
                    room.address.contains(cleanWard, ignoreCase = true) ||
                    room.detailedAddress.contains(cleanWard, ignoreCase = true) ||
                    item.property.address.contains(cleanWard, ignoreCase = true) ||
                    item.property.detailedAddress.contains(cleanWard, ignoreCase = true) ||
                    cleanWard.contains(room.address, ignoreCase = true) ||
                    cleanWard.contains(item.property.address, ignoreCase = true)
                }

                // 5. Price matching (Only filter if user actively modified price slider from default 0..30)
                val priceMatch = if (params.priceMin <= 0f && params.priceMax >= 30f) true else {
                    val priceMin = (params.priceMin * 1_000_000).toLong()
                    val priceMax = (params.priceMax * 1_000_000).toLong()
                    room.price in priceMin..priceMax
                }

                // 6. Area matching
                val areaMatch = if (params.selectedAreaRange.isBlank()) true else {
                    try {
                        val cleanArea = params.selectedAreaRange.replace(" m²", "")
                        val parts = cleanArea.split("-").map { it.trim().toDouble() }
                        if (parts.size == 2) {
                            room.floorArea >= parts[0] && room.floorArea <= parts[1]
                        } else true
                    } catch (e: Exception) {
                        true
                    }
                }

                // 7. Amenities matching (check room amenities + property common amenities)
                val amenitiesMatch = if (params.selectedAmenities.isEmpty()) true else {
                    val allAmenityNames = (room.amenities.map { it.name } + item.property.commonAmenities.map { it.name })
                    params.selectedAmenities.all { filterAmenity ->
                        allAmenityNames.any { it.contains(filterAmenity, ignoreCase = true) || filterAmenity.contains(it, ignoreCase = true) }
                    }
                }

                categoryMatch && roomTypeMatch && queryMatch && provinceMatch && wardMatch && priceMatch && areaMatch && amenitiesMatch
            }

            DiscoveryItem(
                property = item.property,
                rooms = filteredRooms
            )
        }.filter { it.rooms.isNotEmpty() }

        return if (userLat != null && userLon != null) {
            mapped.sortedBy { item ->
                calculateDistance(userLat, userLon, item.property.latitude, item.property.longitude)
            }
        } else {
            mapped
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}

fun AppError.toMessage(): String = when (this) {
    is AppError.Network -> "Không có kết nối internet. Vui lòng kiểm tra lại."
    is AppError.Database -> "Lỗi truy xuất dữ liệu từ thiết bị."
    is AppError.Unauthorized -> "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại."
    is AppError.Validation -> this.message
    is AppError.NotFound -> "Không tìm thấy nội dung yêu cầu."
    is AppError.Unknown -> "Đã có lỗi xảy ra. Vui lòng thử lại sau."
}
