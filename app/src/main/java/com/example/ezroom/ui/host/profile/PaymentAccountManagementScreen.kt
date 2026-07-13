package com.example.ezroom.ui.host.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ezroom.data.repository.BankRepositoryImpl
import com.example.ezroom.data.repository.PaymentAccountRepositoryImpl
import com.example.ezroom.domain.model.PaymentAccount
import com.example.ezroom.domain.usecase.*
import com.example.ezroom.ui.components.EmptyState
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.renter.discovery.viewModelFactory
import com.example.ezroom.ui.theme.*

// UI Component: Payment Account Management Dashboard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAccountManagementScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    viewModel: PaymentAccountViewModel = viewModel(
        factory = viewModelFactory {
            val bankRepo = BankRepositoryImpl()
            val accRepo = PaymentAccountRepositoryImpl()
            PaymentAccountViewModel(
                GetBanksUseCase(bankRepo),
                GetPaymentAccountsUseCase(accRepo),
                SavePaymentAccountUseCase(accRepo),
                DeletePaymentAccountUseCase(accRepo),
                SetDefaultPaymentAccountUseCase(accRepo)
            )
        }
    )
) {
    // State Management: UI State from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<PaymentAccount?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {
        Scaffold(
            topBar = {
                // UI Component: Header
                CenterAlignedTopAppBar(
                    title = { Text("Tài khoản nhận tiền", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            floatingActionButton = {
                // UI Component: Add Button
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = PrimaryMain,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Thêm tài khoản")
                }
            },
            containerColor = Neutral50
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (uiState.isLoading && uiState.savedAccounts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryMain)
                    }
                } else if (uiState.savedAccounts.isEmpty()) {
                    // UI Component: Empty State
                    EmptyState(
                        title = "Chưa có tài khoản nào",
                        description = "Thêm tài khoản ngân hàng để nhận tiền cọc từ khách thuê.",
                        icon = Icons.Default.AccountBalance,
                        actionText = "Thêm tài khoản ngay",
                        onAction = onNavigateToAdd
                    )
                } else {
                    // UI Component: Account List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(uiState.savedAccounts, key = { _, it -> it.id }) { index, account ->
                            var isVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { isVisible = true }

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { 100 },
                                    animationSpec = tween(durationMillis = 400, delayMillis = index * 100)
                                ) + fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = index * 100))
                            ) {
                                PaymentAccountCard(
                                    account = account,
                                    onSetDefault = { viewModel.onSetDefault(account.id) },
                                    onDelete = {
                                        accountToDelete = account
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // UI Component: Deletion Confirmation
        if (showDeleteDialog && accountToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Xóa tài khoản?", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn xóa tài khoản ${accountToDelete?.bank?.code} - ${accountToDelete?.accountNumber}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDelete(accountToDelete!!.id)
                            showDeleteDialog = false
                            accountToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRose)
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

// UI Component: Bank Account Card
@Composable
fun PaymentAccountCard(
    account: PaymentAccount,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            if (account.isDefault) 2.dp else 1.dp,
            if (account.isDefault) PrimaryMain else Neutral300
        ),
        shadowElevation = if (account.isDefault) 4.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // UI Component: Bank Logo
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Neutral50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Neutral100)
                ) {
                    AsyncImage(
                        model = account.bank.logo,
                        contentDescription = account.bank.code,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = account.bank.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = account.accountOwner.uppercase(), style = MaterialTheme.typography.bodySmall, color = Neutral500)
                }
                
                if (account.isDefault) {
                    // UI Component: Default Badge
                    Surface(
                        color = PrimaryMain.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Mặc định",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryMain,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // UI Component: Account Number
            Text(
                text = formatAccountNumber(account.accountNumber),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (!account.isDefault) {
                    TextButton(onClick = onSetDefault) {
                        Text("Đặt làm mặc định", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Xóa", tint = ErrorRose)
                }
            }
        }
    }
}

// Data Mapping: Helper to mask account number
private fun formatAccountNumber(number: String): String {
    if (number.length < 6) return number
    return number.take(3) + " **** " + number.takeLast(3)
}

@Preview(showBackground = true)
@Composable
fun PaymentAccountManagementScreenPreview() {
    EzRoomTheme {
        PaymentAccountManagementScreen()
    }
}
