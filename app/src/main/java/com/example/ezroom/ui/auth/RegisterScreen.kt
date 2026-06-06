package com.example.ezroom.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PasswordTextField
import com.example.ezroom.ui.components.PrimaryButton
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class UserRole {
    RENTER, HOST
}

@Composable
fun RegisterScreen(
    // Event callbacks
    onRegisterClick: (String, String, String, String, UserRole) -> Unit = { _, _, _, _, _ -> },
    onBackToLoginClick: () -> Unit = {}
) {
    // State definitions
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.RENTER) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()
    val isPhoneValid = (phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() }) || phoneNumber.isEmpty()
    
    val isFormValid = fullName.isNotEmpty() && 
                      phoneNumber.isNotEmpty() && isPhoneValid &&
                      email.isNotEmpty() && isEmailValid && 
                      password.length >= 6

    // Main layout container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tạo tài khoản mới",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tham gia cộng đồng EzRoom ngay hôm nay",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input fields group
            CustomTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Họ và tên",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Số điện thoại",
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = !isPhoneValid && phoneNumber.isNotEmpty(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isEmailValid && email.isNotEmpty(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = password.isNotEmpty() && password.length < 6
            )
            if (password.isNotEmpty() && password.length < 6) {
                Text(
                    text = "Mật khẩu phải có ít nhất 6 ký tự",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bạn là:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Role selection row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    text = "Người thuê",
                    isSelected = selectedRole == UserRole.RENTER,
                    onClick = { if (!isLoading) selectedRole = UserRole.RENTER },
                    modifier = Modifier.weight(1f)
                )
                RoleCard(
                    text = "Chủ nhà",
                    isSelected = selectedRole == UserRole.HOST,
                    onClick = { if (!isLoading) selectedRole = UserRole.HOST },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary action button
            PrimaryButton(
                text = "ĐĂNG KÝ",
                onClick = { 
                    if (isFormValid) {
                        scope.launch {
                            isLoading = true
                            delay(1500)
                            isLoading = false
                            onRegisterClick(fullName, phoneNumber, email, password, selectedRole)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                TextButton(onClick = onBackToLoginClick, enabled = !isLoading) {
                    Text(
                        text = "Đăng nhập ngay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Composable
fun RoleCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder(enabled = true),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EzRoomTheme {
        RegisterScreen()
    }
}
