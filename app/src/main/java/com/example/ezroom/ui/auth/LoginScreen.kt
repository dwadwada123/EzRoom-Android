package com.example.ezroom.ui.auth

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.R
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.components.PasswordTextField
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.PrimaryMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Màu sắc cục bộ cho LoginScreen
private val LoginNavy = Color(0xFF0A3366)
private val LoginCyan = Color(0xFF00AEEF)
private val SoftBackground = Color(0xFFF8FAFC)

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

    Scaffold(
        containerColor = SoftBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Logo được căn giữa và làm nổi bật
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

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Chào mừng trở lại",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = peaceSansFont,
                    color = LoginNavy,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Email Input field style từ ảnh
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = peaceSansFont,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("yourname@gmail.com", color = Color.LightGray, fontFamily = peaceSansFont) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LoginCyan,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        isError = email.isNotEmpty() && !isEmailValid
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Input field style từ ảnh
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mật khẩu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = peaceSansFont,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    
                    // Sử dụng PasswordTextField có sẵn nhma bọc lại để giống style
                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Quên mật khẩu?",
                            color = LoginCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = peaceSansFont,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(top = 8.dp)
                                .clickable { onForgotPasswordClick() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ghi nhớ tôi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    @OptIn(ExperimentalMaterial3Api::class)
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = LoginCyan)
                        )
                    }
                    Text(
                        text = "Ghi nhớ tôi",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = peaceSansFont,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Nút Đăng nhập với Gradient màu Cam/Vàng dựa theo ảnh
                val buttonGradient = Brush.horizontalGradient(
                    colors = if (isFormValid && !isLoading) {
                        listOf(PrimaryMain, PrimaryMain.copy(alpha = 0.8f)) // Sử dụng PrimaryMain
                    } else {
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)) // Màu xám khi disabled
                    }
                )

                Surface(
                    onClick = {
                        if (isFormValid && !isLoading) {
                            scope.launch {
                                isLoading = true
                                delay(1000)
                                isLoading = false
                                onLoginClick(email, password, rememberMe)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 8.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = PrimaryMain,
                            spotColor = PrimaryMain
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
                            text = "ĐĂNG NHẬP",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = peaceSansFont,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Don't have an account
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Chưa có tài khoản? ",
                        color = Color.Gray,
                        fontFamily = peaceSansFont,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Đăng ký ngay",
                        color = LoginCyan, // Màu cam giống ảnh
                        fontWeight = FontWeight.Bold,
                        fontFamily = peaceSansFont,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Hoặc đăng nhập bằng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFA6A6A6))
                    Text(
                        text = "Đăng nhập với",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 13.sp,
                        fontFamily = peaceSansFont,
                        color = Color.Gray
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFA6A6A6))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chỉ nút Google Social Login
                @OptIn(ExperimentalMaterial3Api::class)
                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    modifier = Modifier
                        .size(60.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)))
                    )
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (isLoading) {
                LoadingWidget()
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
    EzRoomTheme {
        LoginScreen()
    }
}
