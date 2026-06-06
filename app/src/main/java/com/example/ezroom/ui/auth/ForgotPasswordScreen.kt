package com.example.ezroom.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.theme.EzRoomTheme

@Composable
fun ForgotPasswordScreen(
    // Event callbacks
    onBackClick: () -> Unit,
    onResetSuccess: () -> Unit,
) {
    // State definitions
    var currentStep by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(value = false) }
    var errorText by remember { mutableStateOf<String?>(value = null) }
    
    val scrollState = rememberScrollState()

    // Main layout container
    Scaffold(
        // Top app bar
        topBar = {
            CommonTopAppBar(
                title = "Khôi Phục Mật Khẩu"
            ) {
                if (currentStep == 2) {
                    currentStep = 1
                } else {
                    onBackClick()
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Content scroll area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedContent(
                targetState = currentStep,
                label = "StepTransition"
            ) { step ->
                when (step) {
                    1 -> StepOneEmailInput(
                        email = email,
                        onEmailChange = { 
                            email = it
                            errorText = null
                        },
                        onNextStep = {
                            if (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                currentStep = 2
                            } else {
                                errorText = "Vui lòng nhập email hợp lệ."
                            }
                        }
                    )
                    2 -> StepTwoResetPassword(
                        otpCode = otpCode,
                        onOtpChange = { otpCode = it },
                        newPassword = newPassword,
                        onNewPasswordChange = { newPassword = it },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        isConfirmPasswordVisible = isConfirmPasswordVisible,
                        onToggleConfirmPasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                        onResetPassword = {
                            when {
                                otpCode.length < 4 -> errorText = "Mã OTP không hợp lệ."
                                newPassword.length < 6 -> errorText = "Mật khẩu phải có ít nhất 6 ký tự."
                                newPassword != confirmPassword -> errorText = "Mật khẩu xác nhận không khớp."
                                else -> onResetSuccess()
                            }
                        }
                    )
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StepOneEmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    onNextStep: () -> Unit
) {
    // Input fields group
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Vui lòng nhập Email đã đăng ký tài khoản. Chúng tôi sẽ gửi mã OTP để xác minh.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        // Action buttons row
        Button(
            onClick = onNextStep,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("GỬI MÃ XÁC THỰC", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StepTwoResetPassword(
    otpCode: String,
    onOtpChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isConfirmPasswordVisible: Boolean,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onResetPassword: () -> Unit
) {
    // Input fields group
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Mã OTP đã được gửi đến Email của bạn. Vui lòng nhập mã và thiết lập mật khẩu mới.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = otpCode,
            onValueChange = onOtpChange,
            label = { Text("Mã OTP") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("Mật khẩu mới") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Xác nhận mật khẩu mới") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onToggleConfirmPasswordVisibility) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        // Action buttons row
        Button(
            onClick = onResetPassword,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("XÁC NHẬN ĐẶT LẠI MẬT KHẨU", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    EzRoomTheme {
        ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
    }
}
