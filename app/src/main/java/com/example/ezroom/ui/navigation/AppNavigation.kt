package com.example.ezroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.auth.LoginScreen
import com.example.ezroom.ui.auth.RegisterScreen
import com.example.ezroom.ui.auth.UserRole
import com.example.ezroom.ui.splash.SplashScreen
import com.example.ezroom.ui.renter.discovery.RoomDetailScreen
import com.example.ezroom.ui.renter.appointment.BookingFormScreen
import com.example.ezroom.ui.renter.invoice.InvoiceDetailScreen
import com.example.ezroom.ui.renter.profile.RenterProfileScreen
import com.example.ezroom.ui.host.profile.HostProfileScreen
import com.example.ezroom.ui.renter.review_report.SubmitReportScreen
import com.example.ezroom.ui.renter.review_report.WriteReviewScreen
import com.example.ezroom.ui.notification.NotificationScreen
import com.example.ezroom.ui.chat.ChatRoomScreen
import com.example.ezroom.ui.host.room.RoomFormScreen
import com.example.ezroom.ui.host.profile.EkycScreen
import com.example.ezroom.ui.host.profile.AddDepositAccountScreen
import com.example.ezroom.ui.host.contract.CreateContractScreen
import com.example.ezroom.ui.host.contract.ContractScreen
import com.example.ezroom.data.model.Contract
import com.google.gson.Gson
import java.util.UUID

object Screen {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RENTER_MAIN = "renter_main"
    const val HOST_MAIN = "host_main"
    const val RENTER_PROFILE = "renter_profile"
    const val HOST_PROFILE = "host_profile"
    const val NOTIFICATION = "notification"
    const val ROOM_DETAIL = "room_detail/{roomId}"
    const val BOOKING_FORM = "booking_form/{roomId}?appointmentId={appointmentId}"
    const val INVOICE_DETAIL = "invoice_detail/{invoiceId}"
    const val CHAT_ROOM = "chat_room/{userName}"
    const val SUBMIT_REPORT = "submit_report/{roomId}"
    const val WRITE_REVIEW = "write_review/{roomId}"
    const val ROOM_FORM = "room_form/{isEditMode}"
    const val EKYC = "ekyc"
    const val DEPOSIT_ACCOUNT = "deposit_account"
    const val CREATE_CONTRACT = "create_contract"
    const val CONTRACT_TERMS = "contract_terms/{contractJson}"
}

@Composable
fun AppNavigation() {
    // State definitions
    val navController = rememberNavController()

    // Main layout container
    NavHost(
        navController = navController,
        startDestination = Screen.SPLASH,
    ) {
        // Splash Screen
        composable(Screen.SPLASH) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.LOGIN) {
                    popUpTo(Screen.SPLASH) { inclusive = true }
                }
            })
        }

        // Login Screen
        composable(Screen.LOGIN) {
            LoginScreen(
                onLoginClick = { email, _ ->
                    val target = if (email.contains("host", ignoreCase = true)) Screen.HOST_MAIN else Screen.RENTER_MAIN
                    navController.navigate(target) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        // Register Screen
        composable(Screen.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _, role ->
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

        // Renter Main
        composable(Screen.RENTER_MAIN) {
            RenterMainScreen(
                onRoomClick = { roomId -> navController.navigate("room_detail/$roomId") },
                onInvoiceClick = { invoiceId -> navController.navigate("invoice_detail/$invoiceId") },
                onProfileClick = { navController.navigate(Screen.RENTER_PROFILE) },
                onNotificationClick = { navController.navigate(Screen.NOTIFICATION) },
                onChatClick = { userName ->
                    val encodedName = android.net.Uri.encode(userName)
                    navController.navigate("chat_room/$encodedName")
                },
                onEditAppointment = { roomId, apptId ->
                    navController.navigate("booking_form/$roomId?appointmentId=$apptId")
                }
            )
        }

        // Renter Profile
        composable(Screen.RENTER_PROFILE) {
            RenterProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Notification Screen
        composable(Screen.NOTIFICATION) {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Host Main
        composable(Screen.HOST_MAIN) {
            HostMainScreen(
                onRoomClick = { roomId -> navController.navigate("room_detail/$roomId") },
                onInvoiceClick = { invoiceId -> navController.navigate("invoice_detail/$invoiceId") },
                onProfileClick = { navController.navigate(Screen.HOST_PROFILE) },
                onNotificationClick = { navController.navigate(Screen.NOTIFICATION) },
                onFabClick = { navController.navigate("room_form/false") },
                onEditRoomClick = { roomId -> navController.navigate("room_form/true") },
                onCreateContractClick = { navController.navigate(Screen.CREATE_CONTRACT) },
                onChatClick = { userName ->
                    val encodedName = android.net.Uri.encode(userName)
                    navController.navigate("chat_room/$encodedName")
                }
            )
        }

        // Room Form
        composable(
            route = Screen.ROOM_FORM,
            arguments = listOf(navArgument("isEditMode") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isEditMode = backStackEntry.arguments?.getBoolean("isEditMode") ?: false
            RoomFormScreen(
                isEditMode = isEditMode,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Host Profile
        composable(Screen.HOST_PROFILE) {
            HostProfileScreen(
                onNavigateToEkyc = { navController.navigate(Screen.EKYC) },
                onNavigateToDepositAccount = { navController.navigate(Screen.DEPOSIT_ACCOUNT) },
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Deposit Account Screen
        composable(Screen.DEPOSIT_ACCOUNT) {
            AddDepositAccountScreen(onNavigateBack = { navController.popBackStack() })
        }

        // EKYC Screen
        composable(Screen.EKYC) {
            EkycScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Create Contract Screen
        composable(Screen.CREATE_CONTRACT) {
            CreateContractScreen(
                onBackClick = { navController.popBackStack() },
                onProceedToTerms = { contract ->
                    val contractJson = android.net.Uri.encode(Gson().toJson(contract))
                    navController.navigate("contract_terms/$contractJson")
                }
            )
        }

        // Contract Terms Screen
        composable(
            route = Screen.CONTRACT_TERMS,
            arguments = listOf(navArgument("contractJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val contractJson = backStackEntry.arguments?.getString("contractJson")
            val contract = Gson().fromJson(contractJson, Contract::class.java)
            ContractScreen(
                contract = contract,
                onNavigateBack = { navController.popBackStack() },
                onSignContract = {
                    // Logic for signing contract
                    navController.navigate(Screen.HOST_MAIN) {
                        popUpTo(Screen.HOST_MAIN) { inclusive = true }
                    }
                }
            )
        }

        // Room Detail
        composable(
            route = Screen.ROOM_DETAIL,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val room = MockData.rooms.find { it.id == roomId }
            RoomDetailScreen(
                room = room,
                userRole = UserRole.HOST,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("room_form/true") },
                onBookAppointment = { id -> navController.navigate("booking_form/$id") },
                onNavigateToReport = { id -> navController.navigate("submit_report/$id") },
                onNavigateToWriteReview = { id -> navController.navigate("write_review/$id") },
                onNavigateToChat = { hostName ->
                    val encodedName = android.net.Uri.encode(hostName)
                    navController.navigate("chat_room/$encodedName")
                }
            )
        }

        // Submit Report
        composable(
            route = Screen.SUBMIT_REPORT,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            SubmitReportScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitReport = { navController.popBackStack() }
            )
        }

        // Write Review
        composable(
            route = Screen.WRITE_REVIEW,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            WriteReviewScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitReview = { _, _ -> navController.popBackStack() }
            )
        }

        // Booking Form
        composable(
            route = Screen.BOOKING_FORM,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("appointmentId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
            val room = MockData.rooms.find { it.id == roomId }
            val appointment = if (appointmentId != null) MockData.appointments.find { it.id == appointmentId } else null
            
            BookingFormScreen(
                roomName = room?.title ?: appointment?.roomName ?: "Room",
                appointment = appointment,
                onNavigateBack = { navController.popBackStack() },
                onSubmitBooking = { _, _, _ -> navController.popBackStack() }
            )
        }

        // Invoice Detail
        composable(
            route = Screen.INVOICE_DETAIL,
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId")
            val invoice = MockData.invoices.find { it.id == invoiceId }
            InvoiceDetailScreen(
                invoice = invoice,
                onBackClick = { navController.popBackStack() },
                onPaymentConfirm = { _, _ -> navController.popBackStack() }
            )
        }

        // Chat Room
        composable(
            route = Screen.CHAT_ROOM,
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            ChatRoomScreen(
                userName = userName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
