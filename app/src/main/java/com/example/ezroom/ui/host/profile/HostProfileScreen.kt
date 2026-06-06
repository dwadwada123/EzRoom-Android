package com.example.ezroom.ui.host.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

enum class EkycStatus {
    UNVERIFIED, PENDING, VERIFIED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostProfileScreen(
    // Event callbacks
    onNavigateToEkyc: () -> Unit = {},
    onNavigateToDepositAccount: () -> Unit = {},
    onLogout: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    // State definitions
    val scrollState = rememberScrollState()
    val hostName by remember { mutableStateOf("Nguyễn Văn A") }
    val hostId by remember { mutableStateOf("EZ-HOST-88291") }
    var ekycStatus by remember { mutableStateOf(EkycStatus.UNVERIFIED) }

    // Main layout container
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Hồ sơ cá nhân",
                onBackClick = onBack
            )
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
            // Profile header section
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

            // Verification status section
            EkycStatusCard(
                status = ekycStatus,
                onClick = onNavigateToEkyc
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menu options list
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column {
                    MenuOptionItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Tài khoản nhận tiền",
                        subtitle = "Quản lý thông tin tài khoản nhận tiền đặt cọc",
                        onClick = onNavigateToDepositAccount
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

            // Auth actions row
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Đăng xuất")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ĐĂNG XUẤT",
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

@Composable
fun EkycStatusCard(status: EkycStatus, onClick: () -> Unit) {
    // Dynamic styling based on status
    val containerColor = when (status) {
        EkycStatus.VERIFIED -> Color(0xFFE0F2F1)
        EkycStatus.PENDING -> Color(0xFFFFF3E0)
        EkycStatus.UNVERIFIED -> MaterialTheme.colorScheme.errorContainer
    }
    
    val contentColor = when (status) {
        EkycStatus.VERIFIED -> Color(0xFF00796B)
        EkycStatus.PENDING -> Color(0xFFE65100)
        EkycStatus.UNVERIFIED -> MaterialTheme.colorScheme.onErrorContainer
    }

    val icon = when (status) {
        EkycStatus.VERIFIED -> Icons.Outlined.CheckCircle
        EkycStatus.PENDING -> Icons.Default.Settings
        EkycStatus.UNVERIFIED -> Icons.Default.Warning
    }
    
    val titleText = when (status) {
        EkycStatus.VERIFIED -> "Tài khoản đã xác minh"
        EkycStatus.PENDING -> "Đang chờ duyệt"
        EkycStatus.UNVERIFIED -> "Tài khoản chưa xác thực"
    }
    
    val subText = when (status) {
        EkycStatus.VERIFIED -> "Hồ sơ của bạn đã được duyệt và an toàn."
        EkycStatus.PENDING -> "Thông tin của bạn đang được hệ thống kiểm tra."
        EkycStatus.UNVERIFIED -> "Vui lòng xác thực CCCD để đăng tin và nhận cọc."
    }

    // Interactive card container
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = status == EkycStatus.UNVERIFIED) { onClick() },
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
            if (status == EkycStatus.UNVERIFIED) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Tiếp tục",
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
fun MenuOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    // Interactive list item row
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
            contentDescription = "Tiếp tục",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HostProfileScreenPreview() {
    EzRoomTheme {
        HostProfileScreen()
    }
}
