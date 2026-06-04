package com.example.ezroom.ui.host.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostProfileScreen(
    onNavigateToEkyc: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val hostName by remember { mutableStateOf("Nguyễn Văn A") }
    val hostId by remember { mutableStateOf("EZ-HOST-88291") }
    val isEkycVerified by remember { mutableStateOf(false) }
    val hasBankAccount by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopAppBar(title = "Hồ sơ cá nhân")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // KHU VỰC THÔNG TIN CÁ NHÂN
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = hostName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Mã chủ trọ: $hostId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TRẠNG THÁI XÁC THỰC EKYC
            EkycStatusCard(
                isVerified = isEkycVerified,
                onClick = onNavigateToEkyc
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TIỆN ÍCH & CÀI ĐẶT
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column {
                    MenuOptionItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Tài khoản ngân hàng nhận cọc",
                        subtitle = if (hasBankAccount) "Đã liên kết Vietcombank" else "Chưa liên kết (Bấm để thêm)",
                        onClick = { /* Mở modal/màn hình thêm tài khoản ngân hàng */ }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MenuOptionItem(
                        icon = Icons.Default.Settings,
                        title = "Cài đặt tài khoản",
                        onClick = { }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MenuOptionItem(
                        icon = Icons.Default.SupportAgent,
                        title = "Trung tâm trợ giúp",
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // NÚT ĐĂNG XUẤT
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                )
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Đăng xuất")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng xuất",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Phiên bản 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// CARD TRẠNG THÁI EKYC
@Composable
fun EkycStatusCard(isVerified: Boolean, onClick: () -> Unit) {
    // Sử dụng màu sắc từ Theme để hỗ trợ Dark Mode
    val containerColor = if (isVerified) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    
    val contentColor = if (isVerified) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    val icon = if (isVerified) Icons.Outlined.CheckCircle else Icons.Default.Warning
    val titleText = if (isVerified) "Đã xác thực danh tính" else "Tài khoản chưa xác thực"
    val subText = if (isVerified) "Hồ sơ của bạn đã được duyệt và an toàn." else "Vui lòng xác thực CCCD để đăng tin và nhận cọc."

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isVerified) { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Status",
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            if (!isVerified) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Đi tiếp",
                    tint = contentColor
                )
            }
        }
    }
}

// ITEM MENU TÙY CHỌN
@Composable
fun MenuOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Đi tiếp",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
