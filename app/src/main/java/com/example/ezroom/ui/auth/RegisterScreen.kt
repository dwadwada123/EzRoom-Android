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
import androidx.compose.ui.graphics.Brush
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
    onRegisterClick: (String, String, String, String, UserRole) -> Unit = { _, _, _, _, _ -> },
    onBackToLoginClick: () -> Unit = {}
) {
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

    Box(modifier = Modifier.fillMaxSize()) {
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
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tham gia cộng đồng EzRoom để trải nghiệm tốt nhất",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

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
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tôi muốn đăng ký làm:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernRoleCard(
                    text = "Người thuê",
                    icon = Icons.Default.PersonSearch,
                    isSelected = selectedRole == UserRole.RENTER,
                    onClick = { if (!isLoading) selectedRole = UserRole.RENTER },
                    modifier = Modifier.weight(1f)
                )
                ModernRoleCard(
                    text = "Chủ nhà",
                    icon = Icons.Default.HomeWork,
                    isSelected = selectedRole == UserRole.HOST,
                    onClick = { if (!isLoading) selectedRole = UserRole.HOST },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = "Đăng ký ngay",
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onBackToLoginClick, enabled = !isLoading) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

@Composable
fun ModernRoleCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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

