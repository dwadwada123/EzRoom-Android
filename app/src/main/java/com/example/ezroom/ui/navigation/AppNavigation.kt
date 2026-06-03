package com.example.ezroom.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezroom.ui.auth.LoginScreen
import com.example.ezroom.ui.auth.RegisterScreen
import com.example.ezroom.ui.auth.UserRole

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RENTER_MAIN = "renter_main"
    const val HOST_MAIN = "host_main"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LOGIN,
    ) {
        // Login Screen
        composable(Screen.LOGIN) {
            LoginScreen(
                onLoginClick = { email, _ ->
                    // Test: If email contain host => Host Screen else Renter Screen
                    if (email.contains("host", ignoreCase = true)) {
                        navController.navigate(Screen.HOST_MAIN) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.RENTER_MAIN) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.REGISTER)
                },
                onForgotPasswordClick = { /* Handle Forgot Password */ },
                onGoogleLoginClick = { /* Handle Google Login */ }
            )
        }

        // Register Screen
        composable(Screen.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _, role ->
                    // Navigate based on selected role after "registration"
                    val target = if (role == UserRole.HOST) Screen.HOST_MAIN else Screen.RENTER_MAIN
                    navController.navigate(target) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // Renter Main Workspace (Placeholder)
        composable(Screen.RENTER_MAIN) {
            RenterMainScreen()
        }

        // Host Main Workspace (Placeholder)
        composable(Screen.HOST_MAIN) {
            HostMainScreen()
        }
    }
}

