package com.example.ezroom.ui.renter.discovery

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterHomeScreen(
    modifier: Modifier = Modifier,
    filterParams: FilterParams = FilterParams(),
    onRoomClick: (Room) -> Unit = {},
    onNavigateToFilter: () -> Unit = {},
    onClearFilter: () -> Unit = {},
    viewModel: RenterHomeViewModel = viewModel(
        factory = viewModelFactory {
            RenterHomeViewModel(GetDiscoveryItemsUseCase(RoomRepositoryImpl()))
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation(context) { lat, lon ->
                viewModel.onUserLocationChange(lat, lon)
            }
        }
    }

    LaunchedEffect(Unit) {
        val fineLocationGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation(context) { lat, lon ->
                viewModel.onUserLocationChange(lat, lon)
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(filterParams) {
        viewModel.onFilterParamsChange(filterParams)
    }

    val hasActiveFilter = uiState.filterParams.selectedDistrict.isNotEmpty() ||
            uiState.filterParams.selectedWard.isNotEmpty() ||
            (uiState.filterParams.priceMin > 0f || (uiState.filterParams.priceMax > 0f && uiState.filterParams.priceMax < 30f)) ||
            uiState.filterParams.selectedAreaRange.isNotEmpty() ||
            uiState.filterParams.selectedRoomType.isNotEmpty() ||
            uiState.filterParams.selectedAmenities.isNotEmpty()

    val isRefreshing = uiState.isLoading
    
    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = modifier.fillMaxSize().background(Neutral50)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                SearchBarSection(
                    query = uiState.query,
                    onQueryChange = { viewModel.onQueryChange(it) },
                    onFilterClick = onNavigateToFilter
                )
            }

            if (hasActiveFilter) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kết quả tìm kiếm",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = {
                                viewModel.onFilterParamsChange(com.example.ezroom.domain.model.FilterParams())
                                onClearFilter()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hủy lọc")
                        }
                    }
                }
            }

            if (uiState.error != null) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    com.example.ezroom.ui.components.EmptyState(
                        title = "Lỗi tải dữ liệu",
                        description = uiState.error ?: "",
                        actionText = "Thử lại",
                        onAction = { viewModel.refresh() }
                    )
                }
            }

            items(
                items = uiState.filteredDiscoveryItems, 
                key = { it.property.id },
                contentType = { "DiscoveryCard" },
            ) { item ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.9f, animationSpec = tween(600)),
                ) {
                    DiscoveryCard(
                        item = item,
                        onClick = {
                            item.rooms.firstOrNull()?.let { onRoomClick(it) }
                        },
                    )
                }
            }
        }

        if (uiState.isLoading && !isRefreshing) {
            LoadingWidget()
        }
    }
}

// Utility for simple manual DI
@Suppress("UNCHECKED_CAST")
fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): androidx.lifecycle.ViewModelProvider.Factory {
    return object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}

@Composable
fun DiscoveryCard(item: DiscoveryItem, onClick: () -> Unit) {
    val property = item.property
    val rooms = item.rooms
    
    Box(modifier = Modifier
        .padding(horizontal = 6.dp)
        .graphicsLayer {},
    ) {
        val firstImage = rooms.firstOrNull()?.images?.firstOrNull()
        RoomCard(
            title = if (property.type == PropertyType.COMPLEX) property.name else rooms.firstOrNull()?.title ?: property.name,
            price = property.priceRange,
            address = property.address,
            rating = property.rating,
            imageUrl = firstImage?.url?.takeIf { it.isNotBlank() } ?: firstImage?.resId ?: android.R.drawable.ic_menu_gallery,
            onClick = onClick,
            imageOverlay = {
                if (property.type == PropertyType.COMPLEX) {
                    Surface(
                        color = AccentTeal,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                    ) {
                        Text(
                            text = "Còn phòng",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", tint = Color.Gray)
            
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Tìm kiếm phòng trọ, khu vực...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )
            
            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 12.dp))
            
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimarySurface)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Lọc", tint = PrimaryMain, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun CategorySection(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    val categories = listOf("Tất cả", "Dãy trọ", "Chung cư mini", "Nhà riêng", "Ở ghép")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { cat ->
            val isSelected = selectedCategory == cat
            Surface(
                onClick = { onCategorySelect(cat) },
                shape = CircleShape,
                color = if (isSelected) PrimaryMain else Neutral50,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryMain else Neutral300),
                contentColor = if (isSelected) Color.White else Neutral700
            ) {
                Text(
                    text = cat,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, onLocationObtained: (Double, Double) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    
    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onLocationObtained(location.latitude, location.longitude)
            locationManager.removeUpdates(this)
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    
    try {
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                listener
            )
            val lastGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastGpsLocation != null) {
                onLocationObtained(lastGpsLocation.latitude, lastGpsLocation.longitude)
            }
        } else if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                listener
            )
            val lastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastNetworkLocation != null) {
                onLocationObtained(lastNetworkLocation.latitude, lastNetworkLocation.longitude)
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

@Preview(showBackground = true)
@Composable
fun RenterHomeScreenPreview() {
    EzRoomTheme {
        RenterHomeScreen()
    }
}
