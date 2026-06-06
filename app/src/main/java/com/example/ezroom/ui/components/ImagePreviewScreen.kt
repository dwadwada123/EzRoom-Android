package com.example.ezroom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.ezroom.ui.theme.EzRoomTheme

/**
 * ImagePreviewScreen - Thành phần dùng chung để xem ảnh toàn màn hình với tính năng Zoom và Drag.
 * 
 * @param imageUrl Đường dẫn URL của hình ảnh cần hiển thị.
 * @param title Tiêu đề hiển thị trên thanh công cụ (mặc định là "Xem ảnh").
 * @param onClose Callback xử lý khi người dùng nhấn nút đóng.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    imageUrl: String = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1080", // Thêm ảnh mẫu mặc định để dễ dàng testing
    title: String = "Xem ảnh",
    onClose: () -> Unit,
) {
    // Quản lý tỷ lệ phóng to (scale)
    var scale by remember { mutableFloatStateOf(1f) }
    // Quản lý vị trí dịch chuyển (offset)
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Nền đen tuyền làm nổi bật ảnh
    ) {
        // Vùng hiển thị ảnh với xử lý cử chỉ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Tính toán scale mới: scale cũ * hệ số zoom từ cử chỉ
                        // Giới hạn scale tối thiểu là 1f và tối đa là 5f
                        val newScale = (scale * zoom).coerceIn(1f, 5f)
                        
                        // Nếu đang zoom (scale > 1), cho phép dịch chuyển ảnh (pan)
                        if (newScale > 1f) {
                            // Thuật toán tính toán offset để ảnh dịch chuyển theo ngón tay
                            // Offset mới = Offset cũ + khoảng dịch chuyển từ cử chỉ
                            offset += pan
                        } else {
                            // Nếu scale về 1f, reset offset về 0
                            offset = Offset.Zero
                        }
                        scale = newScale
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Image Preview",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit,
                placeholder = rememberVectorPainter(Icons.Default.Image), // Hiện icon khi đang tải
                error = rememberVectorPainter(Icons.Default.Image) // Hiện icon lỗi nếu không tải được
            )
        }

        // Top App Bar trong suốt nằm đè lên ảnh
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White
            )
        )
    }

    // Reset trạng thái khi Composable bị dispose hoặc khi logic yêu cầu (tùy chọn)
    LaunchedEffect(scale) {
        if (scale <= 1f) {
            offset = Offset.Zero
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagePreviewScreenPreview() {
    EzRoomTheme {
        ImagePreviewScreen(
            imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1080",
        ) {}
    }
}
