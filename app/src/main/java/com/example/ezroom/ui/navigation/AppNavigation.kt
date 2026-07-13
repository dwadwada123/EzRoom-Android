package com.example.ezroom.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ezroom.data.model.MockData
import com.example.ezroom.ui.auth.LoginScreen
import com.example.ezroom.ui.auth.RegisterScreen
import com.example.ezroom.ui.auth.ForgotPasswordScreen
import com.example.ezroom.ui.auth.UserRole
import com.example.ezroom.ui.splash.SplashScreen
import com.example.ezroom.ui.renter.discovery.RenterRoomDetailScreen
import com.example.ezroom.ui.host.room.HostRoomDetailScreen
import com.example.ezroom.ui.renter.appointment.BookingFormScreen
import com.example.ezroom.ui.renter.invoice.InvoiceDetailScreen
import com.example.ezroom.ui.renter.profile.RenterProfileScreen
import com.example.ezroom.ui.renter.profile.RenterReputationScreen
import com.example.ezroom.ui.host.profile.HostProfileScreen
import com.example.ezroom.ui.renter.review_report.SubmitReportScreen
import com.example.ezroom.ui.renter.review_report.WriteReviewScreen
import com.example.ezroom.ui.notification.NotificationScreen
import com.example.ezroom.ui.chat.ChatRoomScreen
import com.example.ezroom.ui.host.room.RoomFormScreen
import com.example.ezroom.ui.host.profile.EkycScreen
import com.example.ezroom.ui.host.profile.AddPaymentAccountScreen
import com.example.ezroom.ui.host.profile.PaymentAccountManagementScreen
import com.example.ezroom.ui.components.ChangePasswordScreen
import com.example.ezroom.ui.host.contract.CreateContractScreen
import com.example.ezroom.ui.host.contract.HostContractScreen
import com.example.ezroom.ui.renter.contract.ContractScreen as RenterContractScreen
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.model.DepositStatus
import com.example.ezroom.ui.host.invoice.CreateInvoiceScreen
import com.example.ezroom.ui.host.invoice.HostInvoiceDetailScreen
import com.example.ezroom.ui.renter.discovery.AdvancedFilterScreen
import com.example.ezroom.ui.host.room.PropertyFormScreen
import com.google.gson.Gson
import kotlinx.coroutines.launch

// State Management: Global Snackbar Provider
val LocalSnackbarProvider = staticCompositionLocalOf<(String) -> Unit> { { } }

// UI Constants: Route Definitions
object Screen {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RENTER_MAIN = "renter_main"
    const val HOST_MAIN = "host_main"
    const val RENTER_PROFILE = "renter_profile"
    const val HOST_PROFILE = "host_profile"
    const val NOTIFICATION = "notification"
    const val RENTER_ROOM_DETAIL = "renter_room_detail/{roomId}"
    const val HOST_ROOM_DETAIL = "host_room_detail/{roomId}"
    const val BOOKING_FORM = "booking_form/{roomId}?appointmentId={appointmentId}"
    const val INVOICE_DETAIL = "invoice_detail/{invoiceId}"
    const val CHAT_ROOM = "chat_room/{userName}"
    const val SUBMIT_REPORT = "submit_report/{roomId}"
    const val WRITE_REVIEW = "write_review/{roomId}"
    const val ROOM_FORM = "room_form/{isEditMode}?propertyId={propertyId}&cloneFromId={cloneFromId}"
    const val EKYC = "ekyc"
    const val PAYMENT_ACCOUNTS = "payment_accounts"
    const val ADD_PAYMENT_ACCOUNT = "add_payment_account"
    const val CHANGE_PASSWORD = "change_password"
    const val CREATE_CONTRACT = "create_contract"
    const val RENTER_CONTRACT = "renter_contract/{contractId}"
    const val PAYMENT_QR = "payment_qr/{contractId}"
    const val REFUND_FORM = "refund_form/{contractId}"
    const val HOST_CONTRACT = "host_contract/{contractId}"
    const val CREATE_INVOICE = "create_invoice"
    const val HOST_INVOICE_DETAIL = "host_invoice_detail/{invoiceId}"
    const val ADVANCED_FILTER = "advanced_filter"
    const val PROPERTY_FORM = "property_form?propertyId={propertyId}"
    const val RENTER_REPUTATION = "renter_reputation"
}

// UI Component: Navigation Root
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // UI Logic: Snackbar Trigger
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    CompositionLocalProvider(LocalSnackbarProvider provides showSnackbar) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Navigation: App Flow
            NavHost(
                navController = navController,
                startDestination = Screen.SPLASH,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { -300 }, animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(400)) },
                popExitTransition = { 
                    fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(400)) 
                },
            ) {
                // Splash Flow
                composable(Screen.SPLASH) {
                    SplashScreen {
                        navController.navigate(Screen.LOGIN) {
                            popUpTo(Screen.SPLASH) { inclusive = true }
                        }
                    }
                }

                // Auth Flow: Login
                composable(Screen.LOGIN) {
                    LoginScreen(
                        onLoginClick = { email, _ ->
                            val target = if (email.contains("host", ignoreCase = true)) Screen.HOST_MAIN else Screen.RENTER_MAIN
                            navController.navigate(target) {
                                popUpTo(Screen.LOGIN) { inclusive = true }
                            }
                        },
                        onRegisterClick = { navController.navigate(Screen.REGISTER) },
                        onForgotPasswordClick = { navController.navigate(Screen.FORGOT_PASSWORD) }
                    )
                }

                // Auth Flow: Forgot Password
                composable(Screen.FORGOT_PASSWORD) {
                    ForgotPasswordScreen(
                        onBackClick = { navController.popBackStack() },
                        onResetSuccess = {
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(Screen.FORGOT_PASSWORD) { inclusive = true }
                            }
                        }
                    )
                }

                // Auth Flow: Register
                composable(Screen.REGISTER) {
                    RegisterScreen(
                        onRegisterClick = { _, _, _, _, role ->
                            val target = if (role == UserRole.HOST) Screen.HOST_MAIN else Screen.RENTER_MAIN
                            navController.navigate(target) {
                                popUpTo(Screen.LOGIN) { inclusive = true }
                            }
                        },
                        onBackToLoginClick = { navController.popBackStack() }
                    )
                }

                // Renter Flow: Main Dashboard
                composable(Screen.RENTER_MAIN) {
                    RenterMainScreen(
                        onRoomClick = { roomId -> navController.navigate("renter_room_detail/$roomId") },
                        onInvoiceClick = { invoiceId -> navController.navigate("invoice_detail/$invoiceId") },
                        onProfileClick = { navController.navigate(Screen.RENTER_PROFILE) },
                        onNotificationClick = { navController.navigate(Screen.NOTIFICATION) },
                        onChatClick = { userName ->
                            val encodedName = android.net.Uri.encode(userName)
                            navController.navigate("chat_room/$encodedName")
                        },
                        onEditAppointment = { roomId, apptId ->
                            navController.navigate("booking_form/$roomId?appointmentId=$apptId")
                        },
                        onNavigateToFilter = { navController.navigate(Screen.ADVANCED_FILTER) }
                    )
                }

                // Renter Flow: Profile
                composable(Screen.RENTER_PROFILE) {
                    RenterProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToChangePassword = { navController.navigate(Screen.CHANGE_PASSWORD) },
                        onNavigateToReputation = { navController.navigate(Screen.RENTER_REPUTATION) },
                        onLogout = {
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Renter Flow: Reputation
                composable(Screen.RENTER_REPUTATION) {
                    RenterReputationScreen(onBack = { navController.popBackStack() })
                }

                // Shared Flow: Notifications
                composable(Screen.NOTIFICATION) {
                    NotificationScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSignContract = { contractId ->
                            navController.navigate("renter_contract/$contractId")
                        }
                    )
                }

                // Host Flow: Main Dashboard
                composable(Screen.HOST_MAIN) {
                    HostMainScreen(
                        onRoomClick = { roomId -> navController.navigate("host_room_detail/$roomId") },
                        onInvoiceClick = { invoiceId -> 
                            if (invoiceId == "create") {
                                navController.navigate(Screen.CREATE_INVOICE)
                            } else {
                                navController.navigate("host_invoice_detail/$invoiceId") 
                            }
                        },
                        onProfileClick = { navController.navigate(Screen.HOST_PROFILE) },
                        onNotificationClick = { navController.navigate(Screen.NOTIFICATION) },
                        onCloneRoomClick = { roomId -> navController.navigate("room_form/false?cloneFromId=$roomId") },
                        onAddRoomToProperty = { propertyId -> navController.navigate("room_form/false?propertyId=$propertyId") },
                        onAddPropertyClick = {
                    navController.navigate("property_form")
                },
                        onAddStandaloneRoomClick = { navController.navigate("room_form/false") },
                        onRenterReputationClick = { _ -> navController.navigate(Screen.RENTER_REPUTATION) },
                    onEditPropertyClick = { propertyId -> navController.navigate("property_form?propertyId=$propertyId") },
                        onCreateContractClick = { navController.navigate(Screen.CREATE_CONTRACT) },
                        onChatClick = { userName ->
                            val encodedName = android.net.Uri.encode(userName)
                            navController.navigate("chat_room/$encodedName")
                        }
                    )
                }

                composable(Screen.PAYMENT_ACCOUNTS) {
                    PaymentAccountManagementScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAdd = { navController.navigate(Screen.ADD_PAYMENT_ACCOUNT) }
                    )
                }

                composable(Screen.ADD_PAYMENT_ACCOUNT) {
                    AddPaymentAccountScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Host Flow: Property Creation
                composable(
                    route = Screen.PROPERTY_FORM,
                    arguments = listOf(navArgument("propertyId") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val propertyId = backStackEntry.arguments?.getString("propertyId")
                    PropertyFormScreen(
                        propertyId = propertyId,
                        onNavigateToCreateFirstRoom = { propId ->
                            showSnackbar(if (propertyId == null) "Tạo dãy trọ thành công" else "Cập nhật dãy trọ thành công")
                            navController.navigate("room_form/false?propertyId=$propId") {
                                popUpTo(Screen.PROPERTY_FORM) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Host Flow: Room Creation
                composable(
                route = Screen.ROOM_FORM,
                arguments = listOf(
                    navArgument("isEditMode") { type = NavType.BoolType },
                    navArgument("propertyId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("cloneFromId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val isEditMode = backStackEntry.arguments?.getBoolean("isEditMode") ?: false
                val propertyId = backStackEntry.arguments?.getString("propertyId")
                val cloneFromId = backStackEntry.arguments?.getString("cloneFromId")
                
                RoomFormScreen(
                    isEditMode = isEditMode,
                    propertyId = propertyId,
                    cloneFromRoomId = cloneFromId,
                    onSaveSuccess = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

                // Host Flow: Profile
                composable(Screen.HOST_PROFILE) {
                    HostProfileScreen(
                        onNavigateToEkyc = { navController.navigate(Screen.EKYC) },
                        onNavigateToDepositAccount = { navController.navigate(Screen.PAYMENT_ACCOUNTS) },
                        onNavigateToChangePassword = { navController.navigate(Screen.CHANGE_PASSWORD) },
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Shared Flow: Settings
                composable(Screen.CHANGE_PASSWORD) {
                    ChangePasswordScreen(
                        onBackClick = { navController.popBackStack() },
                        onPasswordChangeSuccess = {
                            showSnackbar("Đổi mật khẩu thành công")
                            navController.popBackStack()
                        }
                    )
                }

                // Host Flow: Settings
                composable(Screen.EKYC) {
                    EkycScreen(onNavigateBack = { navController.popBackStack() })
                }

                // Host Flow: Contracts
                composable(Screen.CREATE_CONTRACT) {
                    CreateContractScreen(
                        onBackClick = { navController.popBackStack() },
                        onProceedToTerms = { contract ->
                            val contractJson = android.net.Uri.encode(Gson().toJson(contract))
                            navController.navigate("host_contract/$contractJson")
                        }
                    )
                }

                // Renter Flow: Contracts
                composable(
                    route = Screen.RENTER_CONTRACT,
                    arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val contractId = backStackEntry.arguments?.getString("contractId")
                    val contract = MockData.contracts.find { it.id == contractId }
                    if (contract != null) {
                        RenterContractScreen(
                            contract = contract,
                            onNavigateBack = { navController.popBackStack() },
                            onSignSuccess = { showSnackbar("Ký hợp đồng thành công! Vui lòng thanh toán cọc.") },
                            onPayClick = { navController.navigate("payment_qr/$contractId") },
                            onRefundClick = { navController.navigate("refund_form/$contractId") },
                            onDisputeClick = { showSnackbar("Đã gửi yêu cầu tố cáo. Admin sẽ liên hệ bạn.") }
                        )
                    }
                }

                // Fintech: Payment QR
                composable(
                    route = Screen.PAYMENT_QR,
                    arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val contractId = backStackEntry.arguments?.getString("contractId")
                    val contract = MockData.contracts.find { it.id == contractId }
                    if (contract != null) {
                        com.example.ezroom.ui.renter.contract.PaymentQRScreen(
                            contract = contract,
                            onPaymentConfirmed = {
                                showSnackbar("Yêu cầu đã được gửi. Đang xác thực giao dịch...")
                                navController.popBackStack()
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                // Fintech: Refund Form
                composable(
                    route = Screen.REFUND_FORM,
                    arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val contractId = backStackEntry.arguments?.getString("contractId")
                    if (contractId != null) {
                        com.example.ezroom.ui.renter.contract.RefundFormScreen(
                            contractId = contractId,
                            onRefundRequested = {
                                showSnackbar("Đã gửi yêu cầu hoàn tiền. Admin sẽ duyệt trong 24h.")
                                navController.popBackStack()
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                // Host Flow: Contracts
                composable(
                    route = Screen.HOST_CONTRACT,
                    arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val contractId = backStackEntry.arguments?.getString("contractId")
                    val contract = MockData.contracts.find { it.id == contractId }
                    if (contract != null) {
                        HostContractScreen(
                            contract = contract,
                            onNavigateBack = { navController.popBackStack() },
                            onSignContract = { _ ->
                                showSnackbar("Đã gửi hợp đồng cho người thuê")
                                navController.navigate(Screen.HOST_MAIN) {
                                    popUpTo(Screen.HOST_MAIN) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                // Renter Flow: Room Details
                composable(
                    route = Screen.RENTER_ROOM_DETAIL,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId")
                    val room = MockData.rooms.find { it.id == roomId }
                    RenterRoomDetailScreen(
                        room = room,
                        onBackClick = { navController.popBackStack() },
                        onBookAppointment = { id -> navController.navigate("booking_form/$id") },
                        onNavigateToReport = { id -> navController.navigate("submit_report/$id") },
                        onNavigateToWriteReview = { id -> navController.navigate("write_review/$id") },
                        onNavigateToChat = { hostName ->
                            val encodedName = android.net.Uri.encode(hostName)
                            navController.navigate("chat_room/$encodedName")
                        }
                    )
                }

                // Host Flow: Room Details
                composable(
                    route = Screen.HOST_ROOM_DETAIL,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId")
                    val room = MockData.rooms.find { it.id == roomId }
                    HostRoomDetailScreen(
                        room = room,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = { id -> navController.navigate("room_form/true?cloneFromId=$id") },
                        onDeleteClick = { _ -> navController.popBackStack() }
                    )
                }

                // Renter Flow: Reports
                composable(
                    route = Screen.SUBMIT_REPORT,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) {
                    SubmitReportScreen(
                        onBackClick = { navController.popBackStack() },
                        onSubmitReport = { 
                            showSnackbar("Đã gửi báo cáo thành công")
                            navController.popBackStack() 
                        }
                    )
                }

                // Renter Flow: Reviews
                composable(
                    route = Screen.WRITE_REVIEW,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) {
                    WriteReviewScreen(
                        onBackClick = { navController.popBackStack() },
                        onSubmitReview = { _, _ -> 
                            showSnackbar("Cảm ơn bạn đã đánh giá!")
                            navController.popBackStack() 
                        }
                    )
                }

                // Renter Flow: Booking
                composable(
                    route = Screen.BOOKING_FORM,
                    arguments = listOf(
                        navArgument("roomId") { type = NavType.StringType },
                        navArgument("appointmentId") { type = NavType.StringType; nullable = true; defaultValue = null }
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
                        onSubmitBooking = { _, _, _ -> 
                            showSnackbar(if (appointment == null) "Đặt lịch thành công" else "Cập nhật lịch hẹn thành công")
                            navController.popBackStack() 
                        }
                    )
                }

                // Renter Flow: Invoices
                composable(
                    route = Screen.INVOICE_DETAIL,
                    arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val invoiceId = backStackEntry.arguments?.getString("invoiceId")
                    val invoice = MockData.invoices.find { it.id == invoiceId }
                    InvoiceDetailScreen(
                        invoice = invoice,
                        onBackClick = { navController.popBackStack() },
                        onPaymentConfirm = { _, _, _ -> navController.popBackStack() }
                    )
                }

                // Host Flow: Invoices
                composable(
                    route = Screen.HOST_INVOICE_DETAIL,
                    arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val invoiceId = backStackEntry.arguments?.getString("invoiceId")
                    val invoice = MockData.invoices.find { it.id == invoiceId }
                    HostInvoiceDetailScreen(
                        invoice = invoice,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Host Flow: Invoices
                composable(Screen.CREATE_INVOICE) {
                    CreateInvoiceScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onInvoiceCreated = { 
                            showSnackbar("Lập hóa đơn thành công")
                            navController.popBackStack() 
                        }
                    )
                }

                // Shared Flow: Chat
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

                // Renter Flow: Search
                composable(Screen.ADVANCED_FILTER) {
                    AdvancedFilterScreen(
                        onFilterApply = { navController.popBackStack() },
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }

            // UI Component: Snackbar Overlay
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .zIndex(99f),
                snackbar = { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            contentColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = data.visuals.message, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            )
        }
    }
}
