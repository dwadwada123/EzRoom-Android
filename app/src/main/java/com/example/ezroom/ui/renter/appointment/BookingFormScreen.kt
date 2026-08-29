package com.example.ezroom.ui.renter.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

// Biến fallback để tránh lỗi Unresolved reference 'peaceSansFont'
private val peaceSansFont = FontFamily.Default

@Composable
fun BookingFormScreen(
    roomName: String,
    appointment: Appointment? = null,
    onNavigateBack: () -> Unit,
    onSubmitBooking: (String, String, String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var date by remember { mutableStateOf(appointment?.date ?: "") }
    var time by remember { mutableStateOf(appointment?.time ?: "") }
    var note by remember { mutableStateOf(appointment?.note ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = date.isNotBlank() && time.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = White,
            topBar = {
                BookingTopBar(
                    title = if (appointment == null) "ĐẶT LỊCH HẸN" else "SỬA LỊCH HẸN",
                    onNavigateBack = onNavigateBack
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Thẻ thông tin phòng
                RoomInfoHeader(roomName = roomName)

                // Ô chọn ngày
                FormInputField(
                    label = "CHỌN NGÀY XEM PHÒNG",
                    value = date,
                    placeholder = "Chọn ngày xem phòng",
                    leadingIcon = Icons.Default.CalendarToday,
                    enabled = !isLoading,
                    onClick = { showDatePicker(context, date) { date = it } }
                )

                // Ô chọn giờ
                FormInputField(
                    label = "CHỌN GIỜ XEM PHÒNG",
                    value = time,
                    placeholder = "Chọn giờ xem phòng",
                    leadingIcon = Icons.Default.Schedule,
                    enabled = !isLoading,
                    onClick = { showTimePicker(context, time) { time = it } }
                )

                // Ô nhập lời nhắn
                FormNoteField(
                    value = note,
                    onValueChange = { note = it },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dòng thông báo bảo mật
                SecurityNotice()

                // Nút xác nhận đặt lịch
                Button(
                    onClick = {
                        if (isFormValid) {
                            scope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                                onSubmitBooking(date, time, note)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryMain,
                        disabledContainerColor = PrimaryMain.copy(alpha = 0.5f),
                        contentColor = White,
                        disabledContentColor = White.copy(alpha = 0.8f)
                    ),
                    enabled = isFormValid && !isLoading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (appointment == null) "XÁC NHẬN ĐẶT LỊCH" else "CẬP NHẬT LỊCH HẸN",
                        fontFamily = peaceSansFont,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (isLoading) {
            LoadingWidget()
        }
    }
}

// ================= UI COMPONENTS PHỤ TÁCH RIÊNG =================

@Composable
private fun BookingTopBar(
    title: String,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceCard)
                .clickable { onNavigateBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Neutral900,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontFamily = peaceSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Neutral900,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 40.dp)
        )
    }
}

@Composable
private fun RoomInfoHeader(roomName: String) {
    Surface(
        color = PrimarySurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HomeWork,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "ĐANG ĐẶT LỊCH CHO:",
                    fontFamily = peaceSansFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral500,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = roomName,
                    fontFamily = peaceSansFont,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryMain
                )
            }
        }
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    placeholder: String,
    leadingIcon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = peaceSansFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral500,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled, onClick = onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = PrimaryMain,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = value.ifBlank { placeholder },
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = if (value.isNotBlank()) Neutral900 else Neutral500.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Neutral500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FormNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LỜI NHẮN CHO CHỦ NHÀ",
            fontFamily = peaceSansFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral500,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Lời nhắn cho chủ nhà (Tùy chọn)",
                    fontFamily = peaceSansFont,
                    fontSize = 14.sp,
                    color = Neutral500.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                disabledContainerColor = SurfaceCard,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            textStyle = LocalTextStyle.current.copy(
                fontFamily = peaceSansFont,
                fontSize = 14.sp,
                color = Neutral900
            )
        )
    }
}

@Composable
private fun SecurityNotice() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = Neutral500,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Thông tin của bạn được bảo mật an toàn",
            fontFamily = peaceSansFont,
            fontSize = 13.sp,
            color = Neutral500
        )
    }
}

// ================= LOGIC GIỮ NGUYÊN 100% =================

private fun showDatePicker(context: android.content.Context, current: String, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    if (current.isNotEmpty()) {
        val parts = current.split("/")
        if (parts.size == 3) {
            try {
                calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                calendar.set(Calendar.YEAR, parts[2].toInt())
            } catch (ignored: Exception) { }
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private fun showTimePicker(context: android.content.Context, current: String, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    if (current.isNotEmpty()) {
        val parts = current.split(":")
        if (parts.size == 2) {
            try {
                calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                calendar.set(Calendar.MINUTE, parts[1].toInt())
            } catch (ignored: Exception) { }
        }
    }

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true,
    ).show()
}

@Preview(showBackground = true)
@Composable
fun BookingFormScreenPreview() {
    EzRoomTheme {
        BookingFormScreen(
            roomName = "Phòng 101 - EzHome Hải Châu",
            onNavigateBack = {},
            onSubmitBooking = { _, _, _ -> },
        )
    }
}