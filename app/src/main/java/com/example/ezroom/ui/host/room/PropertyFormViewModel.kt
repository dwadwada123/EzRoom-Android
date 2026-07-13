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
import java.util.UUID

// State Management: UI State for Property Form
data class PropertyFormUiState(
    val name: String = "",
    val detailedAddress: String = "",
    val description: String = "",
    val commonAmenities: List<CommonAmenityItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
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
        // Initialization: Set default amenities
        val defaultAmenities = listOf(
            CommonAmenityItem("Bảo vệ 24/7"),
            CommonAmenityItem("Camera an ninh"),
            CommonAmenityItem("Thang máy"),
            CommonAmenityItem("Nhà xe chung"),
            CommonAmenityItem("WiFi chung"),
            CommonAmenityItem("Dọn vệ sinh"),
            CommonAmenityItem("Cửa vân tay"),
            CommonAmenityItem("Hồ bơi"),
            CommonAmenityItem("Phòng Gym")
        )
        _uiState.update { it.copy(commonAmenities = defaultAmenities) }
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
                        detailedAddress = property.detailedAddress,
                        description = property.description,
                        commonAmenities = state.commonAmenities.map { item ->
                            item.copy(isChecked = property.commonAmenities.any { it.name == item.name })
                        },
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
            
            val property = Property(
                id = if (isEditMode) propertyId!! else UUID.randomUUID().toString(),
                name = currentState.name,
                type = PropertyType.COMPLEX,
                address = if (isEditMode) "" else "${ward}, ${province}", // Simplified for mock
                detailedAddress = currentState.detailedAddress,
                description = currentState.description,
                commonAmenities = currentState.commonAmenities.filter { it.isChecked }.map { Amenity(it.name) },
                latitude = lat,
                longitude = lon
            )
            
            saveProperty(property)
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}
