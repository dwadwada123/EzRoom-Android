package com.example.ezroom.ui.host.room

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoomFormUiState(
    val title: String = "",
    val address: String = "",
    val detailedAddress: String = "",
    val description: String = "",
    val price: String = "",
    val electricityPrice: String = "3500",
    val waterPrice: String = "15000",
    val selectedStructure: RoomStructure = RoomStructure.SINGLE,
    val totalArea: String = "",
    val capacity: String = "",
    val belongsToProperty: Property? = null,
    val isEditMode: Boolean = false,
    val cloneFromRoomId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val savedRoomId: String? = null,
    val error: String? = null
)

class RoomFormViewModel(
    private val getPropertyById: GetPropertyByIdUseCase,
    private val getRoomById: GetRoomByIdUseCase,
    private val saveRoom: SaveRoomUseCase,
    private val repository: com.example.ezroom.domain.repository.RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomFormUiState())
    val uiState: StateFlow<RoomFormUiState> = _uiState.asStateFlow()

    val detailedAreas = mutableStateListOf<DetailedArea>()
    val amenities = mutableStateListOf<AmenityItem>()
    val uploadedImages = mutableStateListOf<RoomImageUI>()

    init {
        viewModelScope.launch {
            try {
                val api = com.example.ezroom.data.remote.NetworkClient.createService<com.example.ezroom.data.remote.AmenityApi>()
                val allAmenities = api.getAmenities()
                val roomAmenities = allAmenities.filter { it.type == "ROOM" || it.type.isNullOrBlank() }.map { it.name }
                val fetched = if (roomAmenities.isNotEmpty()) roomAmenities else allAmenities.map { it.name }
                if (fetched.isNotEmpty()) {
                    amenities.clear()
                    amenities.addAll(fetched.map { AmenityItem(it) })
                } else if (amenities.isEmpty()) {
                    amenities.addAll(listOf(AmenityItem("Wifi"), AmenityItem("Máy lạnh"), AmenityItem("Giường"), AmenityItem("Tủ quần áo")))
                }
            } catch (e: Exception) {
                if (amenities.isEmpty()) {
                    amenities.addAll(listOf(AmenityItem("Wifi"), AmenityItem("Máy lạnh"), AmenityItem("Giường"), AmenityItem("Tủ quần áo")))
                }
            }
        }
    }

    fun loadData(propertyId: String?, roomId: String?, isEditMode: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEditMode = isEditMode, cloneFromRoomId = roomId) }
            
            // 1. Load property banner if room is in a complex property
            val cleanPropertyId = propertyId?.takeIf { it.isNotBlank() && it != "{propertyId}" }
            if (cleanPropertyId != null) {
                val property = getPropertyById(cleanPropertyId)
                _uiState.update { it.copy(belongsToProperty = property) }
            }

            // 2. Load existing room details if editing or cloning
            if (roomId != null && roomId != "{cloneFromId}") {
                val room = getRoomById(roomId)
                room?.let { r ->
                    // FIX: If room belongs to a property but it wasn't passed in navigation, fetch and set it now
                    if (r.propertyId != null && _uiState.value.belongsToProperty == null) {
                        val prop = getPropertyById(r.propertyId)
                        if (prop != null) {
                            _uiState.update { it.copy(belongsToProperty = prop) }
                        }
                    }

                    val newTitle = if (isEditMode) {
                        r.title
                    } else {
                        val baseTitle = r.title.replace(Regex("""\s*\(Bản sao(\s*\d+)?\)"""), "").trim()
                        val existingTitles = com.example.ezroom.data.model.MockData.rooms.map { it.title }
                        var candidate = "$baseTitle (Bản sao)"
                        var count = 2
                        while (existingTitles.contains(candidate)) {
                            candidate = "$baseTitle (Bản sao $count)"
                            count++
                        }
                        candidate
                    }

                    _uiState.update { state ->
                        state.copy(
                            title = newTitle,
                            address = r.address,
                            detailedAddress = r.detailedAddress,
                            description = r.description,
                            price = r.price.toString(),
                            electricityPrice = r.electricityPrice.toString(),
                            waterPrice = r.waterPrice.toString(),
                            selectedStructure = r.structure,
                            totalArea = r.floorArea.toString(),
                            capacity = r.capacity.takeIf { it > 0 }?.toString() ?: "",
                            latitude = r.latitude,
                            longitude = r.longitude
                        )
                    }
                    
                    // Detailed areas
                    detailedAreas.clear()
                    detailedAreas.addAll(r.detailedAreas)
                    
                    // Amenities
                    val updatedAmenities = amenities.map { defAmenity ->
                        val matching = r.amenities.find { it.name == defAmenity.name }
                        if (matching != null) {
                            defAmenity.copy(
                                isChecked = true,
                                compensationAmount = matching.compensationAmount.toString()
                            )
                        } else {
                            defAmenity
                        }
                    }
                    amenities.clear()
                    amenities.addAll(updatedAmenities)

                    // Add other amenities in room that are not in defaults
                    val nonDefault = r.amenities.filter { ra -> ra.name !in listOf("Wifi", "Điều hòa", "Giường", "Tủ quần áo") }
                    amenities.addAll(nonDefault.map { ra -> 
                        AmenityItem(ra.name, ra.compensationAmount.toString(), isChecked = true) 
                    })

                    // Images
                    uploadedImages.clear()
                    uploadedImages.addAll(r.images.map { img ->
                        RoomImageUI(
                            url = img.url,
                            resId = img.resId,
                            category = img.category ?: "Khác"
                        )
                    })
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDetailedAddressChange(value: String) = _uiState.update { it.copy(detailedAddress = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onPriceChange(value: String) = _uiState.update { it.copy(price = value) }
    fun onElectricityPriceChange(value: String) = _uiState.update { it.copy(electricityPrice = value) }
    fun onWaterPriceChange(value: String) = _uiState.update { it.copy(waterPrice = value) }
    fun onStructureChange(value: RoomStructure) = _uiState.update { it.copy(selectedStructure = value) }
    fun onTotalAreaChange(area: String) {
        _uiState.update { it.copy(totalArea = area) }
    }

    fun onCapacityChange(capacity: String) {
        _uiState.update { it.copy(capacity = capacity) }
    }

    fun save(propertyId: String?, province: String?, ward: String?, lat: Double, lon: Double, context: android.content.Context) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Upload images first
            val finalImages = uploadedImages.map { img ->
                if (img.uri != null && img.url == null) {
                    try {
                        context.contentResolver.openInputStream(img.uri)?.use { inputStream ->
                            val bytes = inputStream.readBytes()
                            val mimeType = context.contentResolver.getType(img.uri) ?: "image/jpeg"
                            val uploadedUrl = repository.uploadRoomImage(bytes, "upload.jpg", mimeType)
                            if (uploadedUrl != null) {
                                return@map RoomImage(url = uploadedUrl, resId = img.resId, category = img.category)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                RoomImage(url = img.url, resId = img.resId, category = img.category)
            }

            val finalPrice = state.price.replace(".", "").toLongOrNull() ?: 0L
            val finalArea = state.totalArea.toDoubleOrNull() ?: 0.0
            val finalCapacity = state.capacity.toIntOrNull() ?: 0

            val addressFromSelection = if (province != null && ward != null) {
                "${ward}, ${province}".trim(',', ' ')
            } else ""
            val finalAddress = when {
                addressFromSelection.isNotBlank() -> addressFromSelection
                state.belongsToProperty?.address?.isNotBlank() == true -> state.belongsToProperty.address
                state.address.isNotBlank() -> state.address
                else -> "Đà Nẵng"
            }

            val room = Room(
                id = if (state.isEditMode) state.cloneFromRoomId!! else "",
                propertyId = propertyId?.takeIf { it.isNotBlank() && it != "{propertyId}" } ?: state.belongsToProperty?.id,
                title = state.title,
                price = finalPrice,
                priceFormatted = "",
                electricityPrice = state.electricityPrice.toLongOrNull() ?: 3500L,
                waterPrice = state.waterPrice.toLongOrNull() ?: 15000L,
                address = finalAddress,
                detailedAddress = if (state.belongsToProperty != null) state.belongsToProperty.detailedAddress else state.detailedAddress,
                description = state.description,
                structure = state.selectedStructure,
                floorArea = finalArea,
                mezzanineArea = detailedAreas.filter { it.roomName.equals("Gác", ignoreCase = true) }.sumOf { it.areaValue },
                capacity = finalCapacity,
                detailedAreas = detailedAreas.toList(),
                images = finalImages,
                amenities = amenities.filter { it.isChecked }.map { 
                    Amenity(it.name, it.compensationAmount.toLongOrNull() ?: 0L) 
                },
                status = if (state.isEditMode) {
                    // Maintain previous status if edit
                    com.example.ezroom.data.model.MockData.rooms.find { it.id == state.cloneFromRoomId }?.status ?: RoomStatus.PENDING
                } else RoomStatus.PENDING,
                latitude = if (state.belongsToProperty != null) state.belongsToProperty.latitude else lat,
                longitude = if (state.belongsToProperty != null) state.belongsToProperty.longitude else lon
            )

            val savedRoom = saveRoom(room)
            val resolvedRoomId = savedRoom.id.takeIf { it.isNotBlank() } ?: (if (state.isEditMode) state.cloneFromRoomId else null)

            _uiState.update { it.copy(isLoading = false, isSuccess = true, savedRoomId = resolvedRoomId) }
        }
    }
}
