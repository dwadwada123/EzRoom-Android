package com.example.ezroom.ui.host.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

enum class EkycStep {
    INSTRUCTIONS,
    UPLOAD_ID,
    SELFIE,
    SUCCESS
}

@Composable
fun EkycScreen(onNavigateBack: () -> Unit = {}) {
    // Quản lý trạng thái bước hiện tại
    var currentStep by remember { mutableStateOf(EkycStep.INSTRUCTIONS) }

    // Quản lý trạng thái hình ảnh (Sử dụng Uri? để sẵn sàng cho logic thực tế)
    var frontIdUri by remember { mutableStateOf<Uri?>(null) }
    var backIdUri by remember { mutableStateOf<Uri?>(null) }
    var selfieUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Xác thực danh tính",
                onBackClick = {
                    when (currentStep) {
                        EkycStep.INSTRUCTIONS, EkycStep.SUCCESS -> onNavigateBack()
                        else -> {
                            // Quay lại bước trước đó
                            val previousStep = EkycStep.entries.getOrNull(currentStep.ordinal - 1)
                            if (previousStep != null) currentStep = previousStep
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Render giao diện tương ứng với Step hiện tại
            when (currentStep) {
                EkycStep.INSTRUCTIONS -> {
                    InstructionSection(
                        onStartClick = { currentStep = EkycStep.UPLOAD_ID }
                    )
                }
                EkycStep.UPLOAD_ID -> {
                    UploadIdSection(
                        frontUri = frontIdUri,
                        backUri = backIdUri,
                        onCaptureFront = { /* Logic chụp ảnh thực tế sẽ gán Uri vào đây */ frontIdUri = Uri.EMPTY },
                        onCaptureBack = { backIdUri = Uri.EMPTY },
                        onNextClick = { currentStep = EkycStep.SELFIE }
                    )
                }
                EkycStep.SELFIE -> {
                    SelfieSection(
                        selfieUri = selfieUri,
                        onCapture = { selfieUri = Uri.EMPTY },
                        onCompleteClick = { currentStep = EkycStep.SUCCESS }
                    )
                }
                EkycStep.SUCCESS -> {
                    SuccessSection(
                        onFinishClick = onNavigateBack
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- CÁC PHÂN ĐOẠN GIAO DIỆN ---

@Composable
fun InstructionSection(onStartClick: () -> Unit) {
    Icon(
        imageVector = Icons.Default.VerifiedUser,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Chuẩn bị xác thực",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "Để bảo mật và tăng độ tin cậy, EzRoom cần xác minh danh tính của bạn. Vui lòng chuẩn bị:",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(32.dp))

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        InstructionItem(
            icon = Icons.Default.DocumentScanner, 
            title = "Bản gốc CMND/CCCD", 
            desc = "Giấy tờ hợp lệ, còn hạn sử dụng, không mờ nhòe."
        )
        InstructionItem(
            icon = Icons.Default.Lightbulb, 
            title = "Đảm bảo ánh sáng", 
            desc = "Thực hiện ở nơi đủ sáng, không bị chói hay bóng râm."
        )
        InstructionItem(
            icon = Icons.Default.Face, 
            title = "Khuôn mặt rõ ràng", 
            desc = "Vui lòng tháo kính râm và khẩu trang khi chụp ảnh."
        )
    }

    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onStartClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Bắt đầu xác thực", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun InstructionItem(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = desc, 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UploadIdSection(
    frontUri: Uri?,
    backUri: Uri?,
    onCaptureFront: () -> Unit,
    onCaptureBack: () -> Unit,
    onNextClick: () -> Unit
) {
    Text(
        text = "Chụp ảnh CCCD/CMND", 
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Đảm bảo ảnh rõ nét, không mất góc, không lóa sáng", 
        style = MaterialTheme.typography.bodyMedium, 
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(28.dp))

    IdCardCaptureBox(
        title = "Mặt trước",
        isCaptured = frontUri != null,
        onClick = onCaptureFront
    )

    Spacer(modifier = Modifier.height(20.dp))

    IdCardCaptureBox(
        title = "Mặt sau",
        isCaptured = backUri != null,
        onClick = onCaptureBack
    )

    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onNextClick,
        enabled = frontUri != null && backUri != null,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Tiếp tục", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun IdCardCaptureBox(title: String, isCaptured: Boolean, onClick: () -> Unit) {
    val borderColor = if (isCaptured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isCaptured) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = MaterialTheme.shapes.large)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isCaptured) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Đã chụp $title", 
                    style = MaterialTheme.typography.titleMedium, 
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chạm để chụp lại", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, 
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Chụp $title", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SelfieSection(selfieUri: Uri?, onCapture: () -> Unit, onCompleteClick: () -> Unit) {
    Text(
        text = "Xác thực khuôn mặt", 
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Giữ điện thoại ngang tầm mắt và nhìn thẳng vào camera", 
        style = MaterialTheme.typography.bodyMedium, 
        color = MaterialTheme.colorScheme.onSurfaceVariant, 
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(48.dp))

    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 3.dp, 
                color = if (selfieUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), 
                shape = CircleShape
            )
            .clickable { onCapture() },
        contentAlignment = Alignment.Center
    ) {
        if (selfieUri != null) {
            Icon(
                imageVector = Icons.Default.CheckCircle, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary, 
                modifier = Modifier.size(80.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Face, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), 
                modifier = Modifier.size(100.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(56.dp))

    Button(
        onClick = onCompleteClick,
        enabled = selfieUri != null,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Xác nhận khuôn mặt", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun SuccessSection(onFinishClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Gửi thông tin thành công!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hệ thống đang xét duyệt hồ sơ của bạn. Kết quả sẽ được thông báo trong vòng 24h tới.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(56.dp))
        Button(
            onClick = onFinishClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Quay lại trang cá nhân", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EkycScreenPreview() {
    EzRoomTheme {
        EkycScreen()
    }
}
