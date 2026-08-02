package com.example.ezroom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.launch

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

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Đổi mật khẩu",
                onBackClick = onBackClick,
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Mật khẩu hiện tại",
                isVisible = currentPasswordVisible,
                onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "Mật khẩu mới",
                isVisible = newPasswordVisible,
                onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Xác nhận mật khẩu mới",
                isVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorText = "Vui lòng nhập đầy đủ các thông tin."
                        }
                        newPassword.length < 6 -> {
                            errorText = "Mật khẩu mới phải có ít nhất 6 ký tự."
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
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "CẬP NHẬT MẬT KHẨU",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        enabled = enabled,
        modifier = modifier,
    )
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
