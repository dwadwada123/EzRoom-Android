package com.example.ezroom.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.R
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.PrimaryMain
import kotlinx.coroutines.launch

private val LogoNavy = Color(0xFF0A3366)
private val LogoCyan = Color(0xFF00AEEF)
private val SoftBackground = Color(0xFFF8FAFC)
private val peaceSansFont = FontFamily.SansSerif

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChangeSuccess: () -> Unit,
    onChangePassword: (suspend (String, String) -> Boolean)? = null,
) {
    val scope = rememberCoroutineScope()
    
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(value = false) }
    var newPasswordVisible by remember { mutableStateOf(value = false) }
    var confirmPasswordVisible by remember { mutableStateOf(value = false) }

    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackClick() },
                    color = LogoCyan.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = LogoNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo được căn giữa
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ezroom_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Đổi mật khẩu",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = peaceSansFont,
                color = LogoNavy
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card - Modern Style
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                PasswordInputField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Mật khẩu hiện tại",
                    isVisible = currentPasswordVisible,
                    onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                    enabled = !isLoading,
                    placeholder = "••••••••••••"
                )

                PasswordInputField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Mật khẩu mới",
                    isVisible = newPasswordVisible,
                    onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                    enabled = !isLoading,
                    placeholder = "Nhập mật khẩu mới"
                )

                PasswordInputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Xác nhận mật khẩu mới",
                    isVisible = confirmPasswordVisible,
                    onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                    enabled = !isLoading,
                    placeholder = "Xác nhận mật khẩu mới"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Button - Dùng PrimaryMain
            val buttonGradient = Brush.horizontalGradient(
                colors = listOf(PrimaryMain, PrimaryMain.copy(alpha = 0.8f))
            )

            Surface(
                onClick = {
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorText = "Vui lòng nhập đầy đủ các thông tin."
                        }
                        newPassword.length < 8 -> {
                            errorText = "Mật khẩu mới phải có ít nhất 8 ký tự."
                        }
                        newPassword != confirmPassword -> {
                            errorText = "Mật khẩu mới và xác nhận mật khẩu không khớp."
                        }
                        else -> {
                            errorText = null
                            if (onChangePassword != null) {
                                scope.launch {
                                    isLoading = true
                                    val success = onChangePassword(currentPassword, newPassword)
                                    isLoading = false
                                    if (success) {
                                        onPasswordChangeSuccess()
                                    } else {
                                        errorText = "Mật khẩu hiện tại không đúng. Vui lòng thử lại."
                                    }
                                }
                            } else {
                                onPasswordChangeSuccess()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = PrimaryMain),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent,
                enabled = !isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = "CẬP NHẬT MẬT KHẨU",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = peaceSansFont,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (errorText != null) errorText!! else "Mật khẩu mới phải chứa ít nhất 8 ký tự",
                    color = if (errorText != null) MaterialTheme.colorScheme.error else Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = peaceSansFont,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(60.dp)) // Thêm khoảng cách ở cuối để không bị che
        }
    }
}

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    placeholder: String,
    enabled: Boolean = true,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = peaceSansFont,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = placeholder, 
                    color = Color.LightGray,
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock, 
                    contentDescription = null, 
                    tint = LogoNavy,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LogoCyan,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = LogoCyan,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    EzRoomTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onPasswordChangeSuccess = {}
        )
    }
}
