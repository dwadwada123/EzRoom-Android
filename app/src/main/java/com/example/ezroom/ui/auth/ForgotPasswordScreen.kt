package com.example.ezroom.ui.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.R
import com.example.ezroom.ui.theme.EzRoomTheme

// Màu sắc được trích xuất đồng bộ từ logo ezroom_logo.jpg
val LogoNavy = Color(0xFF0A3366)     // Xanh đậm chủ đạo
val LogoCyan = Color(0xFF00AEEF)     // Xanh sáng biểu tượng
val SurfaceCard = Color(0xFFF0F5FA)  // Màu nền lót thẻ nhẹ nhàng
val peaceSansFont = FontFamily.SansSerif

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
        containerColor = Color.White,
        topBar = {
            // Nút quay lại được đưa lên trên cùng (TopBar)
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
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Quay lại",
                            tint = LogoCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter // Thay đổi từ Center sang TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 0.dp), // Bỏ padding vertical ở đây
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Tạo khoảng cách nhỏ dưới TopBar (nút back)
                // Phần Logo: Căn giữa và phóng to hơn
                Image(
                    painter = painterResource(id = R.drawable.ezroom_logo),
                    contentDescription = "EzRoom Logo",
                    modifier = Modifier
                        .height(80.dp) // Phóng to logo lên 80.dp
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally) // Căn giữa tuyệt đối
                        .padding(bottom = 0.dp)
                )

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = LogoCyan,
                        trackColor = SurfaceCard
                    )
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
                                if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    isLoading = true
                                    errorText = null
                                    onRequestOtp(
                                        email,
                                        { _ ->
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
                            onBackClick = onBackClick
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
                                            { _ ->
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
                            onBackClick = { currentStep = 1 }
                        )
                    }
                }

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = peaceSansFont,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp)) // Đẩy nội dung lên trên một chút
            }
        }
    }
}


@Composable
fun StepOneEmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), // Giảm tiếp xuống 4dp để title sát logo hơn
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung
    ) {

        Text(
            text = "Đặt lại mật khẩu của bạn",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold, // Đậm hơn (Bold -> ExtraBold)
            fontFamily = peaceSansFont,
            color = LogoNavy,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center // Căn giữa chữ

        )
        Spacer(modifier = Modifier.height(150.dp))
        Text(
            text = "Chúng tôi sẽ gửi cho bạn một mã xác thực qua email để bạn có thể đặt lại mật khẩu cho tài khoản của mình.",
            fontSize = 15.sp,
            fontFamily = peaceSansFont,
            color = Color.Gray,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium, // Đậm hơn (Normal -> Medium)
            textAlign = androidx.compose.ui.text.style.TextAlign.Center // Căn giữa chữ
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Địa chỉ email",
                fontWeight = FontWeight.Bold,
                fontFamily = peaceSansFont,
                fontSize = 15.sp,
                color = LogoNavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = LogoCyan,
                    cursorColor = LogoCyan
                )
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onNextStep,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LogoCyan)
        ) {
            Text(
                text = "GỬI MÃ XÁC THỰC",
                fontWeight = FontWeight.Bold,
                fontFamily = peaceSansFont,
                fontSize = 15.sp,
                color = Color.White
            )
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
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), // Giảm tiếp xuống 4dp để title sát logo hơn
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung
    ) {
        Text(
            text = "Thiết lập lại mật khẩu",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold, // Đậm hơn
            fontFamily = peaceSansFont,
            color = LogoNavy,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = "Mã OTP đã được gửi đến Email của bạn. Vui lòng nhập mã và thiết lập mật khẩu mới.",
            fontSize = 15.sp,
            fontFamily = peaceSansFont,
            color = Color.Gray,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium, // Đậm hơn
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChange,
                label = { Text("Mã OTP", fontFamily = peaceSansFont, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = LogoNavy) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LogoCyan,
                    cursorColor = LogoCyan
                )
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = { Text("Mật khẩu mới", fontFamily = peaceSansFont, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LogoNavy) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LogoCyan,
                    cursorColor = LogoCyan
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Xác nhận mật khẩu mới", fontFamily = peaceSansFont, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LogoNavy) },
                trailingIcon = {
                    IconButton(onClick = onToggleConfirmPasswordVisibility) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LogoCyan,
                    cursorColor = LogoCyan
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onResetPassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LogoCyan)
        ) {
            Text(
                text = "XÁC NHẬN ĐẶT LẠI MẬT KHẨU",
                fontWeight = FontWeight.Bold,
                fontFamily = peaceSansFont,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun ForgotPasswordScreenPreview() {
    EzRoomTheme {
        ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
    }
}