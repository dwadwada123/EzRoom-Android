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
import com.example.ezroom.ui.notification.NotificationViewModel
import com.example.ezroom.data.repository.NotificationRepositoryImpl
import com.example.ezroom.domain.usecase.GetNotificationsUseCase
import com.example.ezroom.domain.usecase.MarkNotificationAsReadUseCase
import com.example.ezroom.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.ezroom.ui.theme.EzRoomTheme
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
    const val CHAT_ROOM = "chat_room/{conversationId}/{userName}?phoneNumber={phoneNumber}"
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
    const val HOST_CONTRACT_LIST = "host_contract_list"
}

// UI Component: Navigation Root
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val userRepository = remember { com.example.ezroom.data.repository.UserRepositoryImpl() }

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
                        val token = com.example.ezroom.util.TokenManager.getToken()
                        val currentUser = com.example.ezroom.util.TokenManager.getUser()
                        val isRemembered = com.example.ezroom.util.TokenManager.isRememberMe()
                        if (token != null && currentUser != null && isRemembered) {
                            val target = if (currentUser.role == "HOST") Screen.HOST_MAIN else Screen.RENTER_MAIN
                            navController.navigate(target) {
                                popUpTo(Screen.SPLASH) { inclusive = true }
                            }
                        } else {
                            com.example.ezroom.util.TokenManager.clear()
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(Screen.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }

                // Auth Flow: Login
                composable(Screen.LOGIN) {
                    LoginScreen(
                        onLoginClick = { email, password, rememberMe ->
                            scope.launch {
                                val (success, errorMsg) = (userRepository as com.example.ezroom.data.repository.UserRepositoryImpl).loginWithErrorMessage(email, password)
                                if (success) {
                                    com.example.ezroom.util.TokenManager.saveRememberMe(rememberMe)
                                    val currentUser = com.example.ezroom.util.TokenManager.getUser()
                                    val target = if (currentUser?.role == "HOST") Screen.HOST_MAIN else Screen.RENTER_MAIN
                                    navController.navigate(target) {
                                        popUpTo(Screen.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    showSnackbar(errorMsg ?: "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!")
                                }
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
                        onRequestOtp = { email, onSuccess, onError ->
                            scope.launch {
                                val result = userRepository.requestForgotPassword(email)
                                result.onSuccess { msg ->
                                    showSnackbar(msg)
                                    onSuccess(msg)
                                }.onFailure { err ->
                                    onError(err.message ?: "Không thể gửi OTP.")
                                }
                            }
                        },
                        onResetPassword = { email, otp, newPass, onSuccess, onError ->
                            scope.launch {
                                val result = userRepository.resetPassword(email, otp, newPass)
                                result.onSuccess { msg ->
                                    showSnackbar(msg)
                                    onSuccess(msg)
                                }.onFailure { err ->
                                    onError(err.message ?: "Đổi mật khẩu thất bại.")
                                }
                            }
                        },
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
                        onRegisterClick = { name, phone, email, password, role ->
                            scope.launch {
                                val roleStr = if (role == UserRole.HOST) "HOST" else "RENTER"
                                val success = userRepository.register(name, email, phone, password, roleStr)
                                if (success) {
                                    val target = if (role == UserRole.HOST) Screen.HOST_MAIN else Screen.RENTER_MAIN
                                    navController.navigate(target) {
                                        popUpTo(Screen.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    showSnackbar("Đăng ký tài khoản thất bại. Vui lòng thử lại!")
                                }
                            }
                        },
                        onBackToLoginClick = { navController.popBackStack() }
                    )
                }

                // Renter Flow: Main Dashboard
                composable("${Screen.RENTER_MAIN}?tab={tab}") { backStackEntry ->
                    val initialTab = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0
                    
                    val filterParamsState = backStackEntry.savedStateHandle.getStateFlow(
                        "filter_params",
                        com.example.ezroom.domain.model.FilterParams()
                    ).collectAsState()

                    val notificationViewModel: NotificationViewModel = viewModel(
                        factory = viewModelFactory {
                            val repo = NotificationRepositoryImpl()
                            NotificationViewModel(
                                GetNotificationsUseCase(repo),
                                MarkNotificationAsReadUseCase(repo),
                                MarkAllNotificationsAsReadUseCase(repo)
                            )
                        }
                    )
                    val notifUiState by notificationViewModel.uiState.collectAsState()
                    val unreadCount = notifUiState.notifications.count { !it.isRead }

                    LaunchedEffect(Unit) {
                        notificationViewModel.loadNotifications()
                    }

                    RenterMainScreen(
                        initialTab = initialTab,
                        filterParams = filterParamsState.value,
                        unreadNotificationCount = unreadCount,
                        onRoomClick = { roomId -> navController.navigate("renter_room_detail/$roomId") },
                        onInvoiceClick = { invoiceId -> navController.navigate("invoice_detail/$invoiceId") },
                        onProfileClick = { navController.navigate(Screen.RENTER_PROFILE) },
                        onNotificationClick = { navController.navigate(Screen.NOTIFICATION) },
                        onChatClick = { conversationId, userName, phoneNumber ->
                            val encodedName = android.net.Uri.encode(userName)
                            val encodedPhone = android.net.Uri.encode(phoneNumber)
                            navController.navigate("chat_room/$conversationId/$encodedName?phoneNumber=$encodedPhone")
                        },
                        onEditAppointment = { roomId, apptId ->
                            navController.navigate("booking_form/$roomId?appointmentId=$apptId")
                        },
                        onNavigateToFilter = { navController.navigate(Screen.ADVANCED_FILTER) },
                        onClearFilter = {
                            backStackEntry.savedStateHandle["filter_params"] = com.example.ezroom.domain.model.FilterParams()
                        }
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
                composable(
                    route = "${Screen.RENTER_REPUTATION}?renterId={renterId}",
                    arguments = listOf(navArgument("renterId") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val renterId = backStackEntry.arguments?.getString("renterId")
                    RenterReputationScreen(renterId = renterId, onBack = { navController.popBackStack() })
                }

                // Shared Flow: Notifications
                composable(Screen.NOTIFICATION) {
                    val currentUser = com.example.ezroom.util.TokenManager.getUser()
                    val isHost = currentUser?.role == "HOST"

                    NotificationScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToChat = { conversationId ->
                            navController.navigate("chat_room/$conversationId/Chat?phoneNumber=")
                        },
                        onNavigateToContract = { contractId ->
                            val route = if (isHost) "host_contract/$contractId" else "renter_contract/$contractId"
                            navController.navigate(route)
                        },
                        onNavigateToInvoice = { invoiceId ->
                            val route = if (isHost) "host_invoice_detail/$invoiceId" else "invoice_detail/$invoiceId"
                            navController.navigate(route)
                        },
                        onNavigateToAppointments = {
                            val route = if (isHost) "${Screen.HOST_MAIN}?tab=2" else "${Screen.RENTER_MAIN}?tab=2"
                            navController.navigate(route) {
                                // Prevent multiple copies of main screen
                                popUpTo(if (isHost) Screen.HOST_MAIN else Screen.RENTER_MAIN) { inclusive = true }
                            }
                        },
                        onNavigateToRoom = { roomId ->
                            val route = if (isHost) "host_room_detail/$roomId" else "renter_room_detail/$roomId"
                            navController.navigate(route)
                        }
                    )
                }

                // Host Flow: Main Dashboard
                composable("${Screen.HOST_MAIN}?tab={tab}") { backStackEntry ->
                    val initialTab = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0

                    val notificationViewModel: NotificationViewModel = viewModel(
                        factory = viewModelFactory {
                            val repo = NotificationRepositoryImpl()
                            NotificationViewModel(
                                GetNotificationsUseCase(repo),
                                MarkNotificationAsReadUseCase(repo),
                                MarkAllNotificationsAsReadUseCase(repo)
                            )
                        }
                    )
                    val notifUiState by notificationViewModel.uiState.collectAsState()
                    val unreadCount = notifUiState.notifications.count { !it.isRead }

                    LaunchedEffect(Unit) {
                        notificationViewModel.loadNotifications()
                    }

                    HostMainScreen(
                        initialTab = initialTab,
                        unreadNotificationCount = unreadCount,
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
                        onCloneRoomClick = { room ->
                            val ekycStatusClone = com.example.ezroom.util.TokenManager.getUser()?.ekycStatus
                            if (ekycStatusClone == "VERIFIED") {
                                val route = if (room.propertyId != null) "room_form/false?cloneFromId=${room.id}&propertyId=${room.propertyId}" else "room_form/false?cloneFromId=${room.id}"
                                navController.navigate(route)
                            } else {
                                showSnackbar("Bạn cần xác thực danh tính (eKYC) trước khi đăng phòng. Vui lòng vào Trang cá nhân để xác thực.")
                                navController.navigate(Screen.EKYC)
                            }
                        },
                        onAddRoomToProperty = { propertyId ->
                            val ekycStatusAddRoom = com.example.ezroom.util.TokenManager.getUser()?.ekycStatus
                            if (ekycStatusAddRoom == "VERIFIED") {
                                navController.navigate("room_form/false?propertyId=$propertyId")
                            } else {
                                showSnackbar("Bạn cần xác thực danh tính (eKYC) trước khi đăng phòng. Vui lòng vào Trang cá nhân để xác thực.")
                                navController.navigate(Screen.EKYC)
                            }
                        },
                        onAddPropertyClick = {
                            val ekycStatusProp = com.example.ezroom.util.TokenManager.getUser()?.ekycStatus
                            if (ekycStatusProp == "VERIFIED") {
                                navController.navigate("property_form")
                            } else {
                                showSnackbar("Bạn cần xác thực danh tính (eKYC) trước khi đăng phòng. Vui lòng vào Trang cá nhân để xác thực.")
                                navController.navigate(Screen.EKYC)
                            }
                        },
                        onAddStandaloneRoomClick = {
                            val ekycStatusStandalone = com.example.ezroom.util.TokenManager.getUser()?.ekycStatus
                            if (ekycStatusStandalone == "VERIFIED") {
                                navController.navigate("room_form/false")
                            } else {
                                showSnackbar("Bạn cần xác thực danh tính (eKYC) trước khi đăng phòng. Vui lòng vào Trang cá nhân để xác thực.")
                                navController.navigate(Screen.EKYC)
                            }
                        },
                        onRenterReputationClick = { renterId -> navController.navigate("${Screen.RENTER_REPUTATION}?renterId=$renterId") },
                        onCreateContractClick = { roomId, renterPhone, renterName ->
                            val rId = roomId ?: ""
                            val rPhone = renterPhone ?: ""
                            val rName = if (renterName != null) android.net.Uri.encode(renterName) else ""
                            navController.navigate("create_contract?roomId=$rId&renterPhone=$rPhone&renterName=$rName")
                        },
                        onChatClick = { conversationId, userName, phoneNumber ->
                            val encodedName = android.net.Uri.encode(userName)
                            val encodedPhone = android.net.Uri.encode(phoneNumber)
                            navController.navigate("chat_room/$conversationId/$encodedName?phoneNumber=$encodedPhone")
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
                            // Clear session so auto-login no longer works with the old token/password
                            com.example.ezroom.util.TokenManager.clear()
                            // Navigate to Login and clear entire back stack
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                            showSnackbar("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.")
                        },
                        onChangePassword = { current, new ->
                            userRepository.changePassword(current, new)
                        }
                    )
                }

                // Host Flow: Settings
                composable(Screen.EKYC) {
                    EkycScreen(onNavigateBack = { navController.popBackStack() })
                }

                // Host Flow: Profile
                composable(Screen.HOST_PROFILE) {
                    HostProfileScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToEkyc = { navController.navigate(Screen.EKYC) },
                        onNavigateToDepositAccount = { navController.navigate(Screen.PAYMENT_ACCOUNTS) },
                        onNavigateToContracts = { navController.navigate(Screen.HOST_CONTRACT_LIST) },
                        onNavigateToChangePassword = { navController.navigate(Screen.CHANGE_PASSWORD) },
                        onLogout = {
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Host Flow: Contract List
                composable(Screen.HOST_CONTRACT_LIST) {
                    com.example.ezroom.ui.host.contract.HostContractListScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onContractClick = { contractId -> navController.navigate("host_contract/$contractId") }
                    )
                }

                // Host Flow: Contracts
                composable(
                    route = "create_contract?roomId={roomId}&renterPhone={renterPhone}&renterName={renterName}",
                    arguments = listOf(
                        navArgument("roomId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("renterPhone") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("renterName") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId")
                    val renterPhone = backStackEntry.arguments?.getString("renterPhone")
                    val renterName = backStackEntry.arguments?.getString("renterName")
                    CreateContractScreen(
                        initialRoomId = roomId,
                        initialRenterPhone = renterPhone,
                        initialRenterName = renterName,
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
                    val contractArg = backStackEntry.arguments?.getString("contractId")
                    var contract by remember(contractArg) { mutableStateOf<Contract?>(null) }

                    LaunchedEffect(contractArg) {
                        if (contractArg != null) {
                            val fromJson = try {
                                val decoded = android.net.Uri.decode(contractArg)
                                if (decoded.startsWith("{") && decoded.endsWith("}")) {
                                    Gson().fromJson(decoded, Contract::class.java)
                                } else null
                            } catch (e: Exception) { null }

                            if (fromJson != null) {
                                contract = fromJson
                            } else {
                                try {
                                    val repo = com.example.ezroom.data.repository.ContractRepositoryImpl()
                                    repo.getContracts().collect { list ->
                                        contract = list.find { it.id == contractArg }
                                    }
                                } catch (e: Exception) { /* API fallback */ }
                            }
                        }
                    }

                    if (contract != null) {
                        RenterContractScreen(
                            contract = contract!!,
                            onNavigateBack = { navController.popBackStack() },
                            onSignSuccess = {
                                if (contract!!.depositAmount > 0L && contract!!.depositStatus == com.example.ezroom.domain.model.DepositStatus.UNPAID) {
                                    navController.navigate("payment_qr/${contract!!.id}") {
                                        popUpTo(Screen.RENTER_CONTRACT) { inclusive = true }
                                    }
                                } else {
                                    showSnackbar("Ký hợp đồng thành công! Hợp đồng đã có hiệu lực.")
                                    navController.popBackStack()
                                }
                            },
                            onPayClick = { navController.navigate("payment_qr/${contract!!.id}") },
                            onRefundClick = { navController.navigate("refund_form/${contract!!.id}") },
                            onDisputeClick = { showSnackbar("Đã gửi yêu cầu tố cáo. Admin sẽ liên hệ bạn.") }
                        )
                    } else {
                        com.example.ezroom.ui.components.LoadingWidget()
                    }
                }

                // Fintech: Payment QR
                composable(
                    route = Screen.PAYMENT_QR,
                    arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val contractArg = backStackEntry.arguments?.getString("contractId")
                    var contract by remember(contractArg) { mutableStateOf<Contract?>(null) }

                    LaunchedEffect(contractArg) {
                        if (contractArg != null) {
                            val fromJson = try {
                                Gson().fromJson(android.net.Uri.decode(contractArg), Contract::class.java)
                            } catch (e: Exception) { null }

                            if (fromJson != null && fromJson.id.isNotBlank()) {
                                contract = fromJson
                            } else {
                                try {
                                    val repo = com.example.ezroom.data.repository.ContractRepositoryImpl()
                                    repo.getContracts().collect { list ->
                                        contract = list.find { it.id == contractArg }
                                    }
                                } catch (e: Exception) { /* API fallback */ }
                            }
                        }
                    }

                    if (contract != null) {
                        com.example.ezroom.ui.renter.contract.PaymentQRScreen(
                            contract = contract!!,
                            onPaymentConfirmed = {
                                val repo = com.example.ezroom.data.repository.ContractRepositoryImpl()
                                val result = repo.confirmPaymentWithVerification(contract!!.id)
                                if (result.first) {
                                    showSnackbar("Thanh toán tiền cọc thành công! Hợp đồng đã có hiệu lực.")
                                    navController.popBackStack()
                                } else {
                                    showSnackbar(result.second ?: "PayOS chưa nhận được chuyển khoản. Vui lòng hoàn tất chuyển khoản và thử lại.")
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    } else {
                        com.example.ezroom.ui.components.LoadingWidget()
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
                    val contractArg = backStackEntry.arguments?.getString("contractId")
                    var contract by remember(contractArg) { mutableStateOf<Contract?>(null) }

                    LaunchedEffect(contractArg) {
                        if (contractArg != null) {
                            val fromJson = try {
                                val decoded = android.net.Uri.decode(contractArg)
                                if (decoded.startsWith("{") && decoded.endsWith("}")) {
                                    Gson().fromJson(decoded, Contract::class.java)
                                } else null
                            } catch (e: Exception) { null }

                            if (fromJson != null) {
                                contract = fromJson
                            } else {
                                try {
                                    val repo = com.example.ezroom.data.repository.ContractRepositoryImpl()
                                    repo.getContracts().collect { list ->
                                        contract = list.find { it.id == contractArg }
                                    }
                                } catch (e: Exception) { /* API fallback */ }
                            }
                        }
                    }

                    if (contract != null) {
                        HostContractScreen(
                            contract = contract!!,
                            onNavigateBack = {
                                val popped = navController.popBackStack(Screen.CREATE_CONTRACT, inclusive = true)
                                if (!popped) {
                                    navController.popBackStack()
                                }
                            },
                            onSignContract = { _ ->
                                showSnackbar("Đã gửi hợp đồng cho người thuê")
                                navController.navigate(Screen.HOST_MAIN) {
                                    popUpTo(Screen.HOST_MAIN) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        com.example.ezroom.ui.components.LoadingWidget()
                    }
                }

                // Renter Flow: Room Details
                composable(
                    route = Screen.RENTER_ROOM_DETAIL,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    var roomState by remember(roomId) { mutableStateOf<com.example.ezroom.domain.model.Room?>(MockData.rooms.find { it.id == roomId }) }

                    LaunchedEffect(roomId) {
                        if (roomId.isNotBlank()) {
                            try {
                                val fetched = com.example.ezroom.data.repository.RoomRepositoryImpl().getRoomById(roomId)
                                if (fetched != null) roomState = fetched
                            } catch (e: Exception) { /* fallback */ }
                        }
                    }

                    RenterRoomDetailScreen(
                        room = roomState,
                        onBackClick = { navController.popBackStack() },
                        onBookAppointment = { id -> navController.navigate("booking_form/$id") },
                        onNavigateToReport = { id -> navController.navigate("submit_report/$id") },
                        onNavigateToWriteReview = { id -> navController.navigate("write_review/$id") },
                        onNavigateToChat = { hostId, hostName, hostPhone ->
                            val currentUserId = com.example.ezroom.util.TokenManager.getUser()?.id ?: ""
                            val conversationId = "conv_${currentUserId}_${hostId}"
                            val encodedName = android.net.Uri.encode(hostName)
                            val encodedPhone = android.net.Uri.encode(hostPhone)
                            navController.navigate("chat_room/$conversationId/$encodedName?phoneNumber=$encodedPhone")
                        }
                    )
                }

                // Renter Flow: Reports
                composable(
                    route = Screen.SUBMIT_REPORT,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    var roomTitle by remember { mutableStateOf("Thông tin phòng trọ") }
                    var roomPrice by remember { mutableStateOf("") }
                    var roomImageUrl by remember { mutableStateOf("") }
                    
                    LaunchedEffect(roomId) {
                        if (roomId.isNotBlank()) {
                            try {
                                val fetched = com.example.ezroom.data.repository.RoomRepositoryImpl().getRoomById(roomId)
                                if (fetched != null) {
                                    roomTitle = fetched.title
                                    roomPrice = fetched.priceFormatted
                                    roomImageUrl = fetched.images.firstOrNull()?.url ?: ""
                                }
                            } catch (e: Exception) { /* fallback */ }
                        }
                    }

                    SubmitReportScreen(
                        roomTitle = roomTitle,
                        roomPrice = roomPrice,
                        roomImageUrl = roomImageUrl,
                        onBackClick = { navController.popBackStack() },
                        onSubmitReport = { reason ->
                            scope.launch {
                                try {
                                    val api = com.example.ezroom.data.remote.RoomApi.create()
                                    val user = com.example.ezroom.util.TokenManager.getUser()
                                    api.reportRoom(roomId, mapOf("reason" to reason, "reporterName" to (user?.name ?: "Người thuê")))
                                    showSnackbar("Đã gửi báo cáo vi phạm phòng trọ thành công!")
                                } catch (e: retrofit2.HttpException) {
                                    val errorJson = e.response()?.errorBody()?.string()
                                    val msg = try {
                                        org.json.JSONObject(errorJson ?: "").optString("error")
                                    } catch (_: Exception) { null }
                                    showSnackbar(if (!msg.isNullOrEmpty()) msg else "Bạn đã báo cáo phòng trọ này rồi. Báo cáo đang chờ Admin xử lý.")
                                } catch (e: Exception) {
                                    android.util.Log.e("AppNav", "Error reporting room", e)
                                    showSnackbar("Đã gửi báo cáo vi phạm phòng trọ!")
                                }
                                navController.popBackStack()
                            }
                        }
                    )
                }

                // Host Flow: Room Details
                composable(
                    route = Screen.HOST_ROOM_DETAIL,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    var roomState by remember(roomId) { mutableStateOf<com.example.ezroom.domain.model.Room?>(MockData.rooms.find { it.id == roomId }) }
                    var roomContracts by remember(roomId) { mutableStateOf<List<com.example.ezroom.domain.model.Contract>>(emptyList()) }

                    LaunchedEffect(roomId) {
                        if (roomId.isNotBlank()) {
                            try {
                                val fetched = com.example.ezroom.data.repository.RoomRepositoryImpl().getRoomById(roomId)
                                if (fetched != null) roomState = fetched
                            } catch (e: Exception) { /* fallback */ }

                            try {
                                com.example.ezroom.data.repository.ContractRepositoryImpl().getContracts().collect { list ->
                                    roomContracts = list.filter { it.roomId == roomId }
                                }
                            } catch (e: Exception) { /* fallback */ }
                        }
                    }

                    val finalDisplayRoom = remember(roomState, roomContracts) {
                        val baseRoom = roomState ?: return@remember null
                        val activeContract = roomContracts.find { it.status == com.example.ezroom.domain.model.ContractStatus.ACTIVE }
                        
                        val activePhone = activeContract?.renterPhone ?: baseRoom.currentRenter?.phone ?: ""

                        val hasOldContractsForActiveRenter = activeContract != null && roomContracts.any {
                            it.id != activeContract.id &&
                            (it.renterPhone.isNotBlank() && it.renterPhone == activeContract.renterPhone)
                        }

                        val currentRenterInfo = if (activeContract != null) {
                            com.example.ezroom.domain.model.RenterInfo(
                                id = activeContract.id,
                                name = activeContract.renterName,
                                phone = activeContract.renterPhone,
                                avatarUrl = null,
                                stayPeriod = if (hasOldContractsForActiveRenter) "${activeContract.startDate} - Hiện tại (Khách thuê lại)" else "${activeContract.startDate} - Hiện tại",
                                isCurrentlyStaying = true
                            )
                        } else baseRoom.currentRenter

                        // Past Renters: filter out any contract belonging to current active renter (by Phone), deduplicate by Phone
                        val pastContracts = roomContracts.filter { c ->
                            (c.status == com.example.ezroom.domain.model.ContractStatus.TERMINATED || c.status == com.example.ezroom.domain.model.ContractStatus.CANCELLED) &&
                            (activePhone.isBlank() || c.renterPhone != activePhone)
                        }

                        val pastRentersList = pastContracts
                            .groupBy { it.renterPhone }
                            .map { (_, list) ->
                                val latest = list.first()
                                com.example.ezroom.domain.model.RenterInfo(
                                    id = latest.id,
                                    name = latest.renterName,
                                    phone = latest.renterPhone,
                                    avatarUrl = null,
                                    stayPeriod = "${latest.startDate} - ${latest.endDate}",
                                    isCurrentlyStaying = false
                                )
                            }.ifEmpty { 
                                baseRoom.pastRenters.filter { 
                                    activePhone.isBlank() || it.phone != activePhone
                                } 
                            }

                        baseRoom.copy(
                            currentRenter = currentRenterInfo,
                            pastRenters = pastRentersList
                        )
                    }

                    HostRoomDetailScreen(
                        room = finalDisplayRoom,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = { room -> 
                            val route = if (room.propertyId != null) "room_form/true?cloneFromId=${room.id}&propertyId=${room.propertyId}" else "room_form/true?cloneFromId=${room.id}"
                            navController.navigate(route)
                        },
                        onDeleteClick = { roomId ->
                            scope.launch {
                                try {
                                    val api = com.example.ezroom.data.remote.RoomApi.create()
                                    api.deleteRoom(roomId)
                                    showSnackbar("Phòng đã được xóa thành công")
                                } catch (e: Exception) {
                                    android.util.Log.e("AppNav", "Error soft deleting room", e)
                                    showSnackbar("Phòng đã được xóa thành công")
                                }
                                navController.popBackStack()
                            }
                        }
                    )
                }



                // Renter Flow: Reviews
                composable(
                    route = Screen.WRITE_REVIEW,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    val room = MockData.rooms.find { it.id == roomId }
                    val scope = rememberCoroutineScope()
                    val repo = com.example.ezroom.data.repository.RoomRepositoryImpl()
                    
                    WriteReviewScreen(
                        roomTitle = room?.title ?: "Thông tin phòng trọ",
                        roomPrice = room?.priceFormatted ?: "",
                        roomImageUrl = room?.images?.firstOrNull()?.url ?: "",
                        onBackClick = { navController.popBackStack() },
                        onSubmitReview = { rating, comment -> 
                            scope.launch {
                                val success = repo.submitRoomReview(roomId, rating, comment)
                                if (success) {
                                    showSnackbar("Cảm ơn bạn đã đánh giá!")
                                } else {
                                    showSnackbar("Có lỗi xảy ra khi gửi đánh giá")
                                }
                                navController.popBackStack() 
                            }
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
                    val scope = rememberCoroutineScope()
                    val appointmentRepo = com.example.ezroom.data.repository.AppointmentRepositoryImpl()
                    val currentUser = com.example.ezroom.util.TokenManager.getUser()
                    
                    BookingFormScreen(
                        roomName = room?.title ?: appointment?.roomName ?: "Phòng trọ",
                        appointment = appointment,
                        onNavigateBack = { navController.popBackStack() },
                        onSubmitBooking = { date, time, note -> 
                            scope.launch {
                                if (appointment == null) {
                                    val newAppt = com.example.ezroom.domain.model.Appointment(
                                        id = "",
                                        roomId = roomId ?: "",
                                        roomName = room?.title ?: "Phòng trọ",
                                        renterName = currentUser?.name ?: "Người thuê",
                                        renterPhone = currentUser?.phone ?: "",
                                        hostName = room?.hostName ?: "Chủ nhà",
                                        date = date,
                                        time = time,
                                        note = note,
                                        status = com.example.ezroom.domain.model.AppointmentStatus.PENDING
                                    )
                                    appointmentRepo.createAppointment(newAppt)
                                    showSnackbar("Đặt lịch hẹn thành công")
                                } else {
                                    appointmentRepo.updateAppointmentStatus(
                                        appointmentId = appointment.id,
                                        status = appointment.status,
                                        date = date,
                                        time = time
                                    )
                                    showSnackbar("Cập nhật lịch hẹn thành công")
                                }
                                navController.popBackStack() 
                            }
                        }
                    )
                }

                // Renter Flow: Invoices
                composable(
                    route = Screen.INVOICE_DETAIL,
                    arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val invoiceId = backStackEntry.arguments?.getString("invoiceId")
                    var invoice by remember(invoiceId) { mutableStateOf<com.example.ezroom.domain.model.Invoice?>(null) }
                    
                    LaunchedEffect(invoiceId) {
                        if (invoiceId != null) {
                            val repo = com.example.ezroom.data.repository.InvoiceRepositoryImpl()
                            val result = repo.getInvoiceById(invoiceId)
                            if (result is com.example.ezroom.core.Try.Success) {
                                invoice = result.value
                            }
                        }
                    }
                    
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
                    var invoice by remember(invoiceId) { mutableStateOf<com.example.ezroom.domain.model.Invoice?>(null) }
                    
                    LaunchedEffect(invoiceId) {
                        if (invoiceId != null) {
                            val repo = com.example.ezroom.data.repository.InvoiceRepositoryImpl()
                            val result = repo.getInvoiceById(invoiceId)
                            if (result is com.example.ezroom.core.Try.Success) {
                                invoice = result.value
                            }
                        }
                    }
                    
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
                    arguments = listOf(
                        navArgument("conversationId") { type = NavType.StringType },
                        navArgument("userName") { type = NavType.StringType },
                        navArgument("phoneNumber") { type = NavType.StringType; nullable = true }
                    )
                ) { backStackEntry ->
                    val conversationId = backStackEntry.arguments?.getString("conversationId") ?: "conv_1"
                    val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                    val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
                    ChatRoomScreen(
                        conversationId = conversationId,
                        userName = userName,
                        phoneNumber = phoneNumber,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Renter Flow: Search
                composable(Screen.ADVANCED_FILTER) {
                    val currentParams = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.ezroom.domain.model.FilterParams>("filter_params") ?: com.example.ezroom.domain.model.FilterParams()
                    AdvancedFilterScreen(
                        initialParams = currentParams,
                        onFilterApply = { params ->
                            navController.previousBackStackEntry?.savedStateHandle?.set("filter_params", params)
                            navController.popBackStack()
                        },
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

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    EzRoomTheme {
        AppNavigation()
    }
}
