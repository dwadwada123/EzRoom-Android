package com.example.ezroom.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ezroom.ui.theme.EzRoomTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    // Logic: Delay 2 seconds then trigger onTimeout
    LaunchedEffect(key1 = true) {
        delay(2000L)
        onTimeout()
    }

    // UI: Full screen with primary background (Orange)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Placeholder
            Surface(
                color = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = "EzRoom",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Indicator
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    EzRoomTheme {
        SplashScreen(onTimeout = {})
    }
}
