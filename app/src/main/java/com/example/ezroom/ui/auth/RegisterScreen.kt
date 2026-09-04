package com.example.ezroom.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.components.CustomTextField
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PasswordTextField
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.PrimaryMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class UserRole {
    RENTER, HOST
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String, UserRole) -> Unit = { _, _, _, _, _ -> },
    onBackToLoginClick: () -> Unit = {},
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackToLoginClick() },
                    color = PrimaryMain.copy(alpha = 0.1f),
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = peaceSansFont,
                color = LogoNavy,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tham gia cộng đồng EzRoom để trải nghiệm tốt nhất",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = peaceSansFont,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            val showPhoneError = phoneNumber.isNotEmpty() && !isPhoneValid
            val showEmailError = email.isNotEmpty() && !isEmailValid
            val showPasswordError = password.isNotEmpty() && password.length < 6

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Họ và tên",
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = LogoNavy) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                )

                Column {
                    CustomTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "Số điện thoại",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = LogoNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading,
                        isError = showPhoneError,
                    )
                    if (showPhoneError) {
                        Text(
                            text = "Số điện thoại không hợp lệ (phải từ 10 chữ số)",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = peaceSansFont,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = LogoNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !isLoading,
                        isError = showEmailError,
                    )
                    if (showEmailError) {
                        Text(
                            text = "Định dạng Email không hợp lệ",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = peaceSansFont,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        isError = showPasswordError
                    )
                    if (showPasswordError) {
                        Text(
                            text = "Mật khẩu phải chứa ít nhất 6 ký tự",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = peaceSansFont,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tôi muốn đăng ký làm:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = peaceSansFont,
                color = LogoNavy,
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

            Spacer(modifier = Modifier.height(32.dp))

            val buttonGradient = Brush.horizontalGradient(
                colors = if (isFormValid && !isLoading) {
                    listOf(LogoCyan, LogoCyan.copy(alpha = 0.8f))
                } else {
                    listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                }
            )

            Surface(
                onClick = { 
                    if (isFormValid && !isLoading) {
                        scope.launch {
                            isLoading = true
                            delay(1000)
                            isLoading = false
                            onRegisterClick(fullName, phoneNumber, email, password, selectedRole)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = if (isFormValid) LogoCyan else Color.Transparent,
                        spotColor = if (isFormValid) LogoCyan else Color.Transparent
                    ),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent,
                enabled = isFormValid && !isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient),

                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ĐĂNG KÝ NGAY",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = peaceSansFont,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = peaceSansFont,
                    color = Color.Gray
                )
                Text(
                    text = "Đăng nhập",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = peaceSansFont,
                    color = PrimaryMain,
                    modifier = Modifier.clickable { onBackToLoginClick() }
                )
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
        shape = RoundedCornerShape(16.dp), // Bo góc 16dp cho đồng bộ
        color = if (isSelected) PrimaryMain.copy(alpha = 0.1f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryMain else Color(0xFFE2E8F0)
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
                tint = if (isSelected) PrimaryMain else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = peaceSansFont,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryMain else Color.Black
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
