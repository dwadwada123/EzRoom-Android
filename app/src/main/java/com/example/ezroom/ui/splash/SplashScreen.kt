package com.example.ezroom.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.ui.theme.EzRoomTheme
import com.example.ezroom.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    // State definitions
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(1200L)
        onSplashFinished()
    }

    // Main layout container
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value),
                tint = OrangePrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "EzRoom",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = OrangePrimary,
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}

class OvershootInterpolator(private val tension: Float = 2f) : android.view.animation.Interpolator {
    override fun getInterpolation(t: Float): Float {
        var tVal = t
        tVal -= 1.0f
        return tVal * tVal * ((tension + 1) * tVal + tension) + 1.0f
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    EzRoomTheme {
        SplashScreen(onSplashFinished = {})
    }
}
