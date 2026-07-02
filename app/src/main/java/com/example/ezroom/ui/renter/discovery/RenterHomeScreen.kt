package com.example.ezroom.ui.renter.discovery

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.repository.RoomRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.RoomCard
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterHomeScreen(
    modifier: Modifier = Modifier,
    onRoomClick: (Room) -> Unit = {},
    onNavigateToFilter: () -> Unit = {},
    viewModel: RenterHomeViewModel = viewModel(
        factory = viewModelFactory {
            RenterHomeViewModel(GetDiscoveryItemsUseCase(RoomRepositoryImpl()))
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(Neutral50)) {
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

            item(span = StaggeredGridItemSpan.FullLine) {
                CategorySection()
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
                items = uiState.discoveryItems, 
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

        if (uiState.isLoading) {
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
    
    // Discovery item card
    Box(modifier = Modifier
        .padding(horizontal = 6.dp)
        .graphicsLayer {
            // Animation layer
        },
    ) {
        RoomCard(
            title = if (property.type == PropertyType.COMPLEX) property.name else rooms.firstOrNull()?.title ?: property.name,
            price = property.priceRange,
            address = property.address,
            rating = 4.5f,
            imageUrl = property.images.firstOrNull()?.resId ?: android.R.drawable.ic_menu_gallery,
            onClick = onClick,
            imageOverlay = {
                if (property.type == PropertyType.COMPLEX) {
                    // Availability Badge
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.BottomStart),
                        color = if (property.vacantRoomCount > 0) PrimaryMain.copy(alpha = 0.9f) else Neutral500.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp, 
                            Color.White.copy(alpha = 0.3f),
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (property.vacantRoomCount > 0) Icons.Default.HomeWork else Icons.Default.Block, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (property.vacantRoomCount > 0) "Còn ${property.vacantRoomCount} phòng" else "Hết phòng",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(12.dp, shape = CircleShape, ambientColor = PrimaryMain.copy(alpha = 0.2f)),
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryMain)
                Spacer(modifier = Modifier.width(12.dp))
                
                // Real Input for search
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { 
                        Text("Tìm khu vực, tòa nhà...", style = MaterialTheme.typography.bodyMedium, color = Neutral500) 
                    },
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
}

@Composable
fun CategorySection() {
    val categories = listOf("Tất cả", "Dãy trọ", "Chung cư mini", "Nhà riêng", "Ở ghép")
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { cat ->
            val isSelected = selectedCategory == cat
            Surface(
                onClick = { selectedCategory = cat },
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

@Preview(showBackground = true)
@Composable
fun RenterHomeScreenPreview() {
    EzRoomTheme {
        RenterHomeScreen()
    }
}

