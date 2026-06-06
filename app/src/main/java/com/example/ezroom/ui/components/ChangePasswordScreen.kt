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

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChangeSuccess: () -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Đổi Mật Khẩu",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Mật khẩu mới nên có ít nhất 6 ký tự để đảm bảo tính bảo mật.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Mật khẩu hiện tại
            PasswordField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorText = null
                },
                label = "Mật khẩu hiện tại",
                isVisible = currentPasswordVisible
            ) {
                currentPasswordVisible = !currentPasswordVisible
            }

            // Mật khẩu mới
            PasswordField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorText = null
                },
                label = "Mật khẩu mới",
                isVisible = newPasswordVisible
            ) {
                newPasswordVisible = !newPasswordVisible
            }

            // Xác nhận mật khẩu mới
            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorText = null
                },
                label = "Xác nhận mật khẩu mới",
                isVisible = confirmPasswordVisible
            ) {
                confirmPasswordVisible = !confirmPasswordVisible
            }

            if (errorText != null) {
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
                            onPasswordChangeSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "CẬP NHẬT MẬT KHẨU",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
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
    onVisibilityChange: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
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
