package com.example.ezroom.ui.host.contract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezroom.data.repository.ContractRepositoryImpl
import com.example.ezroom.domain.model.Contract
import com.example.ezroom.domain.model.ContractStatus
import com.example.ezroom.domain.model.DepositStatus
import com.example.ezroom.ui.components.CommonTopAppBar
import com.example.ezroom.ui.components.LoadingWidget
import com.example.ezroom.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostContractListScreen(
    onNavigateBack: () -> Unit,
    onContractClick: (String) -> Unit
) {
    val repository = remember { ContractRepositoryImpl() }
    var contracts by remember { mutableStateOf<List<Contract>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Tất cả", "Chờ ký", "Đang hoạt động")

    LaunchedEffect(Unit) {
        isLoading = true
        repository.getContracts().collect { list ->
            contracts = list
            isLoading = false
        }
    }

    val filteredContracts = when (selectedTab) {
        1 -> contracts.filter { it.status == ContractStatus.WAITING_SIGN || it.status == ContractStatus.DRAFT }
        2 -> contracts.filter { it.status == ContractStatus.ACTIVE || it.status == ContractStatus.WAITING_DEPOSIT }
        else -> contracts
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                CenterAlignedTopAppBar(
                    title = { Text("Quản lý Hợp đồng", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        },
        containerColor = Neutral50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryMain
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingWidget()
                }
            } else if (filteredContracts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Neutral500, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Chưa có hợp đồng nào", color = Neutral500, fontSize = 15.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredContracts) { contract ->
                        HostContractItemCard(
                            contract = contract,
                            onClick = { onContractClick(contract.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HostContractItemCard(
    contract: Contract,
    onClick: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,### đ") }
    val displayRoom = contract.roomName.takeIf { !it.isNullOrBlank() } ?: "Phòng trọ"
    val displayAddress = contract.address?.takeIf { it.isNotBlank() } ?: "Địa chỉ phòng trọ"

    val (statusText, statusColor) = when (contract.status) {
        ContractStatus.ACTIVE -> "ĐANG HOẠT ĐỘNG" to SuccessEmerald
        ContractStatus.WAITING_SIGN -> "CHỜ NGƯỜI THUÊ KÝ" to AccentAmber
        ContractStatus.WAITING_DEPOSIT -> "CHỜ THANH TOÁN CỌC" to PrimaryMain
        else -> "DỰ THẢO" to Neutral500
    }

    val depositStatusText = when (contract.depositStatus) {
        DepositStatus.FROZEN -> "Cọc: Đã đóng băng an toàn"
        DepositStatus.DISBURSED -> "Cọc: Đã giải ngân"
        DepositStatus.UNPAID -> "Cọc: Chưa thanh toán"
        else -> "Cọc: Đã hoàn trả"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayRoom,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Neutral900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "📍 $displayAddress",
                fontSize = 13.sp,
                color = Neutral500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = Neutral100, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Người thuê: ${contract.renterName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Neutral900)
                    Text(text = "SĐT: ${contract.renterPhone}", fontSize = 12.sp, color = Neutral500)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = formatter.format(contract.depositAmount), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryMain)
                    Text(text = depositStatusText, fontSize = 11.sp, color = Neutral500)
                }
            }
        }
    }
}
