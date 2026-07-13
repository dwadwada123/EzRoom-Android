package com.example.ezroom.ui.host.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// State Management: UI State for Room Management
data class RoomManagementUiState(
    val properties: List<Property> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val error: String? = null
)

// State Management: Room Management Logic
class RoomManagementViewModel(
    private val getRooms: GetRoomsUseCase,
    private val getProperties: GetPropertiesUseCase,
    private val togglePropertyVisibility: TogglePropertyVisibilityUseCase,
    private val deleteProperty: DeletePropertyUseCase,
    private val toggleRoomVisibility: ToggleRoomVisibilityUseCase,
    private val deleteRoom: DeleteRoomUseCase,
    private val submitAppeal: SubmitAppealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomManagementUiState())
    val uiState: StateFlow<RoomManagementUiState> = _uiState.asStateFlow()

    init {
        // Initialization: Load data
        loadData()
    }

    // Business Logic: Fetch properties and rooms
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(getProperties(), getRooms()) { propsResult, roomsResult ->
                if (propsResult is Try.Success && roomsResult is Try.Success) {
                    _uiState.update { it.copy(
                        properties = propsResult.value,
                        rooms = roomsResult.value,
                        isLoading = false
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load data") }
                }
            }.collect()
        }
    }

    // Business Logic: Handle tab selection
    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    // Business Logic: Toggle property visibility
    fun onTogglePropertyVisibility(propertyId: String) {
        viewModelScope.launch {
            togglePropertyVisibility(propertyId)
            loadData()
        }
    }

    // Business Logic: Delete property
    fun onDeleteProperty(propertyId: String) {
        viewModelScope.launch {
            deleteProperty(propertyId)
            loadData()
        }
    }

    // Business Logic: Toggle room visibility
    fun onToggleRoomVisibility(roomId: String) {
        viewModelScope.launch {
            toggleRoomVisibility(roomId)
            loadData()
        }
    }

    // Business Logic: Delete standalone room or room in complex
    fun onDeleteRoom(roomId: String) {
        viewModelScope.launch {
            deleteRoom(roomId)
            loadData()
        }
    }

    // Business Logic: Submit appeal for a removed room
    fun onSubmitAppeal(roomId: String, text: String, images: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            submitAppeal(roomId, text, images)
            loadData()
        }
    }
}
