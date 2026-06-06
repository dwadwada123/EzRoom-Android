package com.example.ezroom.ui.renter.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

@Composable
fun RenterProfileScreen(
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSaveChanges: (name: String, phone: String) -> Unit = { _, _ -> }
) {
    var isEditing by remember { mutableStateOf(false) }
    
    // User data states (Mock data)
    var name by remember { mutableStateOf("Nguyễn Văn A") }
    var phone by remember { mutableStateOf("0987654321") }
    val email = "nguyenvana@gmail.com"
    val avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200"

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(title = "Hồ sơ của tôi")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. PROFILE HEADER
            ProfileHeader(
                isEditing = isEditing,
                name = name,
                phone = phone,
                email = email,
                avatarUrl = avatarUrl,
                onEditClick = { isEditing = true },
                onCancelClick = { isEditing = false },
                onSaveClick = { newName, newPhone ->
                    name = newName
                    phone = newPhone
                    onSaveChanges(newName, newPhone)
                    isEditing = false
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. SETTINGS MENU (Only visible when not editing)
            if (!isEditing) {
                SettingsMenu(
                    onFavoriteClick = onNavigateToFavorite,
                    onAppointmentsClick = onNavigateToAppointments,
                    onInvoicesClick = onNavigateToInvoices,
                    onChangePasswordClick = onNavigateToChangePassword,
                    onHelpClick = {},
                    onLogoutClick = onLogout
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(
    isEditing: Boolean,
    name: String,
    phone: String,
    email: String,
    avatarUrl: String,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: (String, String) -> Unit
) {
    var editedName by remember(name) { mutableStateOf(name) }
    var editedPhone by remember(phone) { mutableStateOf(phone) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Avatar with Badge
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { /* Handle change avatar */ },
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Avatar",
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isEditing) {
            // Viewing mode
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "$email | $phone",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onEditClick) {
                Text(text = "Chỉnh sửa hồ sơ", color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Editing mode
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Hủy")
                }
                Button(
                    onClick = { onSaveClick(editedName, editedPhone) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Lưu thay đổi")
                }
            }
        }
    }
}

@Composable
fun SettingsMenu(
    onFavoriteClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onInvoicesClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tiện ích & Cài đặt",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                MenuItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "Phòng trọ đã lưu",
                    onClick = onFavoriteClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                MenuItem(
                    icon = Icons.Outlined.DateRange,
                    title = "Lịch hẹn xem phòng",
                    onClick = onAppointmentsClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                MenuItem(
                    icon = Icons.Outlined.Receipt,
                    title = "Lịch sử hóa đơn",
                    onClick = onInvoicesClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                MenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Đổi mật khẩu",
                    onClick = onChangePasswordClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                MenuItem(
                    icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    title = "Điều khoản & Hỗ trợ",
                    onClick = onHelpClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                MenuItem(
                    icon = Icons.AutoMirrored.Outlined.ExitToApp,
                    title = "Đăng xuất",
                    isError = true,
                    onClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    isError: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RenterProfileScreenPreview() {
    EzRoomTheme {
        RenterProfileScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RenterProfileEditPreview() {
    EzRoomTheme {
        ProfileHeader(
            isEditing = true,
            name = "Nguyễn Văn A",
            phone = "0987654321",
            email = "nguyenvana@gmail.com",
            avatarUrl = "",
            onEditClick = {},
            onCancelClick = {},
            onSaveClick = { _, _ -> }
        )
    }
}
