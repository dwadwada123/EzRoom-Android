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
import com.example.ezroom.ui.theme.Neutral50

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onRequestOtp: (email: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit = { _, s, _ -> s("Mã OTP đã được gửi.") },
    onResetPassword: (email: String, otp: String, newPass: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, s, _ -> s("Đổi mật khẩu thành công.") },
    onResetSuccess: () -> Unit,
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(value = false) }
    var errorText by remember { mutableStateOf<String?>(value = null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Khôi Phục Mật Khẩu",
                onBackClick = {
                    if (currentStep == 2) {
                        currentStep = 1
                    } else {
                        onBackClick()
                    }
                },
            )
        },
        containerColor = Neutral50,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            AnimatedContent(
                targetState = currentStep,
                label = "StepTransition",
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
                                isLoading = true
                                errorText = null
                                onRequestOtp(
                                    email,
                                    { msg ->
                                        isLoading = false
                                        currentStep = 2
                                    },
                                    { err ->
                                        isLoading = false
                                        errorText = err
                                    }
                                )
                            } else {
                                errorText = "Email không hợp lệ. Vui lòng kiểm tra lại."
                            }
                        },
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
                                otpCode.length < 4 -> errorText = "Mã OTP phải có 4-6 ký tự."
                                newPassword.length < 6 -> errorText = "Mật khẩu quá ngắn (tối thiểu 6 ký tự)."
                                newPassword != confirmPassword -> errorText = "Mật khẩu xác nhận không khớp."
                                else -> {
                                    isLoading = true
                                    errorText = null
                                    onResetPassword(
                                        email, otpCode, newPassword,
                                        { msg ->
                                            isLoading = false
                                            onResetSuccess()
                                        },
                                        { err ->
                                            isLoading = false
                                            errorText = err
                                        }
                                    )
                                }
                            }
                        },
                    )
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText!!.uppercase(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}


@Composable
fun StepOneEmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    onNextStep: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Xác thực Email".uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        
        Text(
            text = "Vui lòng nhập Email đã đăng ký tài khoản. Chúng tôi sẽ gửi mã OTP để xác minh.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
        )

        Button(
            onClick = onNextStep,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
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
    onResetPassword: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Thiết lập lại".uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Mã OTP đã được gửi đến Email của bạn. Vui lòng nhập mã và thiết lập mật khẩu mới.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = otpCode,
            onValueChange = onOtpChange,
            label = { Text("Mã OTP") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
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
                        contentDescription = null,
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
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
                        contentDescription = null,
                    )
                }
            },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
        )

        Button(
            onClick = onResetPassword,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
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
