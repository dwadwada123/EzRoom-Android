package com.example.ezroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.data.repository.LocationRepositoryImpl
import com.example.ezroom.domain.model.Province
import com.example.ezroom.domain.model.Ward
import com.example.ezroom.domain.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// State Management: Location Data
class LocationViewModel(
    private val repository: LocationRepository = LocationRepositoryImpl()
) : ViewModel() {

    private val _provinces = MutableStateFlow<List<Province>>(emptyList())
    val provinces: StateFlow<List<Province>> = _provinces

    private val _wards = MutableStateFlow<List<Ward>>(emptyList())
    val wards: StateFlow<List<Ward>> = _wards

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchProvinces()
    }

    // Business Logic: Load Provinces
    fun fetchProvinces() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _provinces.value = repository.getProvinces()
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu vị trí: ${e.message}"
                _provinces.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Business Logic: Select Province and update Wards
    fun selectProvince(provinceCode: String) {
        val province = _provinces.value.find { it.code == provinceCode }
        _wards.value = province?.wards ?: emptyList()
    }
}
