package com.example.ezroom.ui.host.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.Amenity
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.PropertyType
import com.example.ezroom.domain.usecase.GetPropertyByIdUseCase
import com.example.ezroom.domain.usecase.SavePropertyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State Management: UI State for Property Form
data class PropertyFormUiState(
    val name: String = "",
    val address: String = "",
    val detailedAddress: String = "",
    val description: String = "",
    val commonAmenities: List<CommonAmenityItem> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val savedPropertyId: String? = null,
    val error: String? = null
)

// State Management: Property Form Logic
class PropertyFormViewModel(
    private val saveProperty: SavePropertyUseCase,
    private val getPropertyById: GetPropertyByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyFormUiState())
    val uiState: StateFlow<PropertyFormUiState> = _uiState.asStateFlow()

    init {
        // Initialization: Fetch amenities
        viewModelScope.launch {
            try {
                val api = com.example.ezroom.data.remote.NetworkClient.createService<com.example.ezroom.data.remote.AmenityApi>()
                val allAmenities = api.getAmenities()
                val propAmenities = allAmenities.filter { it.type == "PROPERTY" || it.type.isNullOrBlank() }.map { it.name }
                val fetched = if (propAmenities.isNotEmpty()) propAmenities else allAmenities.map { it.name }
                if (fetched.isNotEmpty()) {
                    _uiState.update { it.copy(commonAmenities = fetched.map { CommonAmenityItem(it) }) }
                } else {
                    _uiState.update { it.copy(commonAmenities = listOf(
                        CommonAmenityItem("Bảo vệ 24/7"), CommonAmenityItem("Camera an ninh"), 
                        CommonAmenityItem("Thang máy"), CommonAmenityItem("Nhà xe chung")
                    )) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(commonAmenities = listOf(
                    CommonAmenityItem("Bảo vệ 24/7"), CommonAmenityItem("Camera an ninh"), 
                    CommonAmenityItem("Thang máy"), CommonAmenityItem("Nhà xe chung")
                )) }
            }
        }
    }

    // Business Logic: Load property for editing
    fun loadProperty(propertyId: String?) {
        if (propertyId == null || propertyId == "{propertyId}") return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val property = getPropertyById(propertyId)
            if (property != null) {
                _uiState.update { state ->
                    state.copy(
                        name = property.name,
                        address = property.address,
                        detailedAddress = property.detailedAddress,
                        description = property.description,
                        commonAmenities = state.commonAmenities.map { item ->
                            item.copy(isChecked = property.commonAmenities.any { it.name == item.name })
                        },
                        latitude = property.latitude,
                        longitude = property.longitude,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Property not found") }
            }
        }
    }

    // State Management: Field Updates
    fun onNameChange(newName: String) = _uiState.update { it.copy(name = newName) }
    fun onDetailedAddressChange(newAddress: String) = _uiState.update { it.copy(detailedAddress = newAddress) }
    fun onDescriptionChange(newDesc: String) = _uiState.update { it.copy(description = newDesc) }
    
    fun onToggleAmenity(index: Int) {
        _uiState.update { state ->
            val updated = state.commonAmenities.toMutableList()
            updated[index] = updated[index].copy(isChecked = !updated[index].isChecked)
            state.copy(commonAmenities = updated)
        }
    }

    // Business Logic: Save property
    fun onSave(propertyId: String?, province: String?, ward: String?, lat: Double, lon: Double) {
        val currentState = _uiState.value
        val isEditMode = propertyId != null && propertyId != "{propertyId}"
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val newId = if (isEditMode) propertyId!! else ""
            val currentUser = com.example.ezroom.util.TokenManager.getUser()
            val addressFromSelection = if (province != null && ward != null) {
                "${ward}, ${province}".trim(',', ' ')
            } else ""
            val finalAddress = when {
                addressFromSelection.isNotBlank() -> addressFromSelection
                currentState.address.isNotBlank() -> currentState.address
                else -> "Đà Nẵng"
            }
            val property = Property(
                id = newId,
                name = currentState.name,
                type = PropertyType.COMPLEX,
                address = finalAddress,
                detailedAddress = currentState.detailedAddress,
                description = currentState.description,
                commonAmenities = currentState.commonAmenities.filter { it.isChecked }.map { Amenity(it.name) },
                latitude = lat,
                longitude = lon,
                hostId = currentUser?.id ?: "host_default"
            )
            
            val savedProperty = saveProperty(property)
            val resolvedPropertyId = savedProperty.id.takeIf { it.isNotBlank() } ?: newId
            _uiState.update { it.copy(isLoading = false, isSuccess = true, savedPropertyId = resolvedPropertyId) }
        }
    }
}
