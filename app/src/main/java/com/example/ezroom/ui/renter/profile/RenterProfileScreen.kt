package com.example.ezroom.ui.renter.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezroom.data.repository.UserRepositoryImpl
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.usecase.GetCurrentUserUseCase
import com.example.ezroom.domain.usecase.UpdateProfileUseCase
import com.example.ezroom.domain.usecase.VerifyEkycUseCase
import com.example.ezroom.ui.components.RenterReviewItem
import com.example.ezroom.ui.profile.ProfileViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

/**
 * EzRoom 2026 "Pro Max" Renter Profile Screen
 * Features: High-impact header, Bento menu grid, and Reputation access.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenterProfileScreen(
    onBackClick: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToReputation: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            val repo = UserRepositoryImpl()
            ProfileViewModel(
                GetCurrentUserUseCase(repo),
                UpdateProfileUseCase(repo),
                VerifyEkycUseCase(repo),
            )
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(value = false) }
    
    val user = uiState.user ?: return
    
    val scrollState = rememberScrollState()
    val reviews = com.example.ezroom.data.model.MockData.renterReviews

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Section
            HeaderSection(user.avatarUrl ?: "", user.name, user.email, onBackClick)

            // Content Bento
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp)
            ) {
                if (!isEditing) {
                    // Credit Score Bento - Clickable to separate screen
                    ReputationScoreBento(reviews, onNavigateToReputation)

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Removed EkycVerifiedBadge for Renter as requested

                    Text(
                        text = "Tiện ích của tôi",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        BentoMenuItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Yêu thích",
                            subtitle = "12 phòng",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToFavorite
                        )
                        BentoMenuItem(
                            icon = Icons.Outlined.DateRange,
                            title = "Lịch hẹn",
                            subtitle = "3 lịch",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAppointments
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BentoMenuItem(
                        icon = Icons.Outlined.Receipt,
                        title = "Hóa đơn thanh toán",
                        subtitle = "Tháng 05/2026",
                        isFullWidth = true,
                        onClick = onNavigateToInvoices
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Cài đặt hệ thống",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Column {
                            MenuItemRow(icon = Icons.Outlined.Lock, title = "Đổi mật khẩu", onClick = onNavigateToChangePassword)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                            MenuItemRow(icon = Icons.AutoMirrored.Outlined.HelpOutline, title = "Trung tâm hỗ trợ", onClick = {})
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                            MenuItemRow(
                                icon = Icons.AutoMirrored.Filled.Logout, 
                                title = "Đăng xuất", 
                                isError = true, 
                                onClick = onLogout,
                            )
                        }
                    }
                } else {
                    // Edit Form
                    EditProfileForm(
                        name = user.name,
                        phone = user.phone,
                        onCancel = { isEditing = false },
                        onSave = { newName, newPhone ->
                            viewModel.onUpdateProfile(newName, newPhone)
                            isEditing = false
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Floating Edit Toggle
        if (!isEditing) {
            ExtendedFloatingActionButton(
                onClick = { isEditing = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chỉnh sửa", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HeaderSection(avatarUrl: String, name: String, email: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryMain, PrimaryMain.copy(alpha = 0.8f), Color.White)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(24.dp, CircleShape),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(4.dp, Color.White),
                ) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 8.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
    }
}

@Composable
fun ReputationScoreBento(reviews: List<RenterReview>, onClick: () -> Unit) {
    val averageRating = if (reviews.isEmpty()) 0.0 else reviews.asSequence().map { it.rating }.average()
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryMain.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Điểm uy tín của bạn", style = MaterialTheme.typography.labelMedium, color = Neutral500)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "%.1f".format(averageRating), 
                        style = MaterialTheme.typography.displayMedium, 
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryMain
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star, null, 
                                    tint = if (index < averageRating.toInt()) AccentAmber else Neutral300,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(text = "Xem ${reviews.size} đánh giá", style = MaterialTheme.typography.bodySmall, color = PrimaryMain, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = SuccessEmerald.copy(alpha = 0.1f),
                contentColor = SuccessEmerald
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun BentoMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    isFullWidth: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.then(if (isFullWidth) Modifier.fillMaxWidth() else Modifier),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    isError: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun EditProfileForm(
    name: String,
    phone: String,
    onCancel: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var editedName by remember { mutableStateOf(name) }
    var editedPhone by remember { mutableStateOf(phone) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Cập nhật thông tin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = CircleShape
                ) {
                    Text("Hủy bỏ", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onSave(editedName, editedPhone) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = CircleShape
                ) {
                    Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RenterProfileScreenPreview() {
    EzRoomTheme {
        RenterProfileScreen()
    }
}


