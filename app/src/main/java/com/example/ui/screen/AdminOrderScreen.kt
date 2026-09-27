package com.example.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KudehwaAccent
import com.example.ui.theme.KudehwaSuccess
import com.example.ui.viewmodel.AdminOrderViewModel
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(
    viewModel: AdminOrderViewModel,
    onBackClick: () -> Unit,
    onNavigateDashboard: () -> Unit,
    onNavigateReports: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Usimamizi wa Oda (Admin)" else "Order Management (Admin)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateDashboard) {
                        Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
                    }
                    IconButton(onClick = onNavigateReports) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reports")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (currentLang == "SW") "Hakuna Oda Yoyote Iliyowekwa Bado" else "No Orders Placed Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (currentLang == "SW") "Wateja wanaponunua bidhaa kwenye duka, oda zao zitaonekana hapa." else "Customer orders will appear here when placed.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders, key = { it.orderId }) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_item_${order.orderId}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    order.orderId,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = when (order.status) {
                                        "DELIVERED" -> KudehwaSuccess.copy(alpha = 0.15f)
                                        "OUT_FOR_DELIVERY" -> KudehwaAccent.copy(alpha = 0.2f)
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        when (order.status) {
                                            "PLACED" -> if (currentLang == "SW") "Imepokelewa" else "Placed"
                                            "CONFIRMED" -> if (currentLang == "SW") "Imethibitishwa" else "Confirmed"
                                            "PREPARING" -> if (currentLang == "SW") "Inafungashwa" else "Preparing"
                                            "OUT_FOR_DELIVERY" -> if (currentLang == "SW") "Iko Njiani" else "Out for Delivery"
                                            "DELIVERED" -> if (currentLang == "SW") "Imefikishwa" else "Delivered"
                                            else -> order.status
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            "DELIVERED" -> KudehwaSuccess
                                            else -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                if (currentLang == "SW") "Mteja: ${order.customerName} (${order.customerPhone})" else "Customer: ${order.customerName} (${order.customerPhone})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                if (currentLang == "SW") "Jumla: ${CurrencyUtils.formatTzs(order.totalAmount)}" else "Total: ${CurrencyUtils.formatTzs(order.totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                if (currentLang == "SW") "Badili Hali ya Oda:" else "Update Order Status:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Status Update Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, "CONFIRMED") },
                                    modifier = Modifier.weight(1f).testTag("confirm_button_${order.orderId}"),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(if (currentLang == "SW") "Thibitisha" else "Confirm", style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, "PREPARING") },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(if (currentLang == "SW") "Fungashia" else "Prepare", style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, "OUT_FOR_DELIVERY") },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(if (currentLang == "SW") "Iko Njiani" else "On Way", style = MaterialTheme.typography.labelSmall)
                                }

                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, "DELIVERED") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = KudehwaSuccess),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(if (currentLang == "SW") "Imefika" else "Delivered", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
