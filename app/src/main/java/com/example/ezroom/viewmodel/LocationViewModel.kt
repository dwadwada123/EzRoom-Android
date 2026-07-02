package com.example.ezroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.data.remote.LocationApi
import com.example.ezroom.data.remote.Province
import com.example.ezroom.data.remote.Ward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing location data.
 * Simplified to 2 levels: Province -> Ward.
 */
class LocationViewModel : ViewModel() {
    private val api = LocationApi.create()

    private val _provinces = MutableStateFlow<List<Province>>(emptyList())
    val provinces: StateFlow<List<Province>> = _provinces

    private val _wards = MutableStateFlow<List<Ward>>(emptyList())
    val wards: StateFlow<List<Ward>> = _wards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchProvinces()
    }

    fun fetchProvinces() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _provinces.value = api.getProvinces()
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu vị trí: ${e.message}"
                _provinces.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Select a province and update the list of available wards.
     */
    fun selectProvince(provinceCode: String) {
        val province = _provinces.value.find { it.code == provinceCode }
        _wards.value = province?.wards ?: emptyList()
    }
}
