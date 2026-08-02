package com.example.ezroom.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PasswordTextField
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.components.SecondaryButton
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginClick: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }
    val scope = rememberCoroutineScope()

    val isInspectionMode = LocalInspectionMode.current
    val isEmailValid = if (isInspectionMode) {
        email.isEmpty() || email.contains("@")
    } else {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()
    }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty() && isEmailValid

    Box(modifier = Modifier.fillMaxSize()) {
        // Aesthetic Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo with container
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Logo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Chào mừng bạn",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = "Đăng nhập để tìm phòng ưng ý nhất",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            val showEmailError = email.isNotEmpty() && !isEmailValid
            val showPasswordError = password.isNotEmpty() && password.length < 6

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "yourname@gmail.com",
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading,
                isError = showEmailError,
            )

            if (showEmailError) {
                Text(
                    text = "Định dạng Email không hợp lệ (ví dụ: name@example.com)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = showPasswordError,
            )

            if (showPasswordError) {
                Text(
                    text = "Mật khẩu phải chứa ít nhất 6 ký tự",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        enabled = !isLoading
                    )
                    Text(
                        text = "Ghi nhớ tôi",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(onClick = onForgotPasswordClick, enabled = !isLoading) {
                    Text(
                        text = "Quên mật khẩu?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Đăng nhập",
                onClick = { 
                    if (isFormValid) {
                        scope.launch {
                            isLoading = true
                            delay(1000)
                            isLoading = false
                            onLoginClick(email, password, rememberMe)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                Text(
                    text = "Chưa có tài khoản?",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            SecondaryButton(
                text = "Đăng ký ngay",
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EzRoomTheme {
        LoginScreen()
    }
}

