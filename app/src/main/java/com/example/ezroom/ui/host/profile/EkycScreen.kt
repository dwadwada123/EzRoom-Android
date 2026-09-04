package com.example.ezroom.ui.host.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ezroom.data.repository.UserRepositoryImpl
import com.example.ezroom.domain.usecase.GetCurrentUserUseCase
import com.example.ezroom.domain.usecase.UpdateProfileUseCase
import com.example.ezroom.domain.usecase.VerifyEkycUseCase
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.profile.ProfileViewModel
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.EzRoomTheme
import java.io.File

private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

enum class EkycStep {
    INSTRUCTIONS,
    UPLOAD_ID,
    SELFIE,
    SUCCESS
}

@Composable
fun EkycScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            val repo = UserRepositoryImpl()
            ProfileViewModel(
                GetCurrentUserUseCase(repo),
                UpdateProfileUseCase(repo),
                VerifyEkycUseCase(repo)
            )
        }
    )
) {
    // State definitions
    var currentStep by remember { mutableStateOf(EkycStep.INSTRUCTIONS) }
    var idCardNumber by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    var frontIdUri by remember { mutableStateOf<Uri?>(null) }
    var backIdUri by remember { mutableStateOf<Uri?>(null) }
    var selfieUri by remember { mutableStateOf<Uri?>(null) }

    var tempFrontUri by remember { mutableStateOf<Uri?>(null) }
    var tempBackUri by remember { mutableStateOf<Uri?>(null) }
    var tempSelfieUri by remember { mutableStateOf<Uri?>(null) }

    val frontCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) frontIdUri = tempFrontUri
    }
    val backCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) backIdUri = tempBackUri
    }
    val selfieCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) selfieUri = tempSelfieUri
    }

    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle success transition
    LaunchedEffect(uiState.isEkycSuccess) {
        if (uiState.isEkycSuccess) {
            currentStep = EkycStep.SUCCESS
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    // Main layout container
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CommonTopAppBar(
                title = "Xác thực danh tính",
                onBackClick = {
                    when (currentStep) {
                        EkycStep.INSTRUCTIONS, EkycStep.SUCCESS -> onNavigateBack()
                        else -> {
                            val previousStep = EkycStep.entries.getOrNull(currentStep.ordinal - 1)
                            if (previousStep != null) currentStep = previousStep
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading && currentStep != EkycStep.SUCCESS) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content scroll area
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
                            idCardNumber = idCardNumber,
                            onIdCardNumberChange = { idCardNumber = it },
                            onCaptureFront = { 
                                tempFrontUri = createImageUri(context)
                                tempFrontUri?.let { frontCameraLauncher.launch(it) }
                            },
                            onCaptureBack = { 
                                tempBackUri = createImageUri(context)
                                tempBackUri?.let { backCameraLauncher.launch(it) }
                            },
                            onNextClick = { currentStep = EkycStep.SELFIE }
                        )
                    }
                    EkycStep.SELFIE -> {
                        SelfieSection(
                            selfieUri = selfieUri,
                            onCapture = { 
                                tempSelfieUri = createImageUri(context)
                                tempSelfieUri?.let { selfieCameraLauncher.launch(it) }
                            },
                            onCompleteClick = {
                                if (frontIdUri != null && backIdUri != null && selfieUri != null) {
                                    viewModel.onVerifyEkyc(idCardNumber, frontIdUri!!, backIdUri!!, selfieUri!!, context)
                                }
                            }
                        )
                    }
                    EkycStep.SUCCESS -> {
                        PendingSection(
                            onFinishClick = onNavigateBack
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InstructionSection(onStartClick: () -> Unit) {
    Icon(
        imageVector = Icons.Default.VerifiedUser,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Xác thực danh tính",
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

    // Input fields group
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
            desc = "Vui lòng tháo kính râm và khẩu trang."
        )
    }

    Spacer(modifier = Modifier.height(48.dp))

    // Action buttons row
    Button(
        onClick = onStartClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Bắt đầu xác thực", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun InstructionItem(icon: ImageVector, title: String, desc: String) {
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
private fun UploadIdSection(
    frontUri: Uri?,
    backUri: Uri?,
    idCardNumber: String,
    onIdCardNumberChange: (String) -> Unit,
    onCaptureFront: () -> Unit,
    onCaptureBack: () -> Unit,
    onNextClick: () -> Unit
) {
    Text(
        text = "Chụp ảnh CMND/CCCD", 
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Đảm bảo ảnh rõ nét và đủ ánh sáng", 
        style = MaterialTheme.typography.bodyMedium, 
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(28.dp))

    val isIdValid = idCardNumber.matches(Regex("^[0-9]{9}$|^[0-9]{12}$"))

    OutlinedTextField(
        value = idCardNumber,
        onValueChange = onIdCardNumberChange,
        label = { Text("Số CMND/CCCD") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = idCardNumber.isNotEmpty() && !isIdValid,
        supportingText = {
            if (idCardNumber.isNotEmpty() && !isIdValid) {
                Text("Số CMND/CCCD phải có 9 hoặc 12 chữ số")
            }
        },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(20.dp))

    IdCardCaptureBox(
        title = "Mặt trước",
        imageUri = frontUri,
        onClick = onCaptureFront
    )

    Spacer(modifier = Modifier.height(20.dp))

    IdCardCaptureBox(
        title = "Mặt sau",
        imageUri = backUri,
        onClick = onCaptureBack
    )

    Spacer(modifier = Modifier.height(48.dp))

    // Action buttons row
    Button(
        onClick = onNextClick,
        enabled = frontUri != null && backUri != null && isIdValid,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Tiếp tục", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun IdCardCaptureBox(title: String, imageUri: Uri?, onClick: () -> Unit) {
    val isCaptured = imageUri != null
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
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
private fun SelfieSection(selfieUri: Uri?, onCapture: () -> Unit, onCompleteClick: () -> Unit) {
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
            AsyncImage(
                model = selfieUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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

    // Action buttons row
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
private fun PendingSection(onFinishClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.HourglassTop,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFFF57C00) // Amber/Orange
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Đã gửi hồ sơ thành công!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hệ thống đang xét duyệt hồ sơ của bạn. Thông thường mất 1–3 ngày làm việc. Chúng tôi sẽ thông báo kết quả qua ứng dụng.",
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
            Text("Về trang chủ", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
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

