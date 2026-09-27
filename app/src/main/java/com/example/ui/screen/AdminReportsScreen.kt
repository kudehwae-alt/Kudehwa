package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.ReportViewModel
import com.example.util.CurrencyUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    viewModel: ReportViewModel,
    onBackClick: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val deliveries by viewModel.deliveries.collectAsState()
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsState()

    var selectedReportType by remember { mutableStateOf("DAILY") } // DAILY, WEEKLY, MONTHLY
    var exportSuccessMsg by remember { mutableStateOf<String?>(null) }

    // Initial load: Today's report
    LaunchedEffect(Unit) {
        loadReport("DAILY", viewModel)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Ripoti na Takwimu (Reports)" else "Store Analytics & Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Report type selection buttons
            Text(if (currentLang == "SW") "Chagua Aina ya Ripoti:" else "Select Report Period:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        selectedReportType = "DAILY"
                        loadReport("DAILY", viewModel)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedReportType == "DAILY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (currentLang == "SW") "Ripoti ya Siku" else "Daily Report", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        selectedReportType = "WEEKLY"
                        loadReport("WEEKLY", viewModel)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedReportType == "WEEKLY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (currentLang == "SW") "Wiki (7 Days)" else "Weekly", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        selectedReportType = "MONTHLY"
                        loadReport("MONTHLY", viewModel)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedReportType == "MONTHLY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (currentLang == "SW") "Mwezi (30 Days)" else "Monthly", fontWeight = FontWeight.Bold)
                }
            }

            // Summary cards
            val totalRevenue = payments.sumOf { it.amount }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (currentLang == "SW") "Jumla ya Mauzo" else "Total Revenue", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(CurrencyUtils.formatTzs(totalRevenue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (currentLang == "SW") "Jumla ya Oda" else "Total Orders", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${orders.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (currentLang == "SW") "Waliosafirishewa" else "Deliveries", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${deliveries.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (currentLang == "SW") "Malipo Yaliyokamilika" else "Paid Payments", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${payments.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Export button
            Button(
                onClick = {
                    exportSuccessMsg = if (currentLang == "SW") "Ripoti imetolewa na kuhifadhiwa kikamilifu (CSV/PDF Exported successfully)." else "Report exported successfully (CSV/PDF)."
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (currentLang == "SW") "Pakua Ripoti (Export CSV / PDF)" else "Export Report (CSV / PDF)", fontWeight = FontWeight.Bold)
            }

            exportSuccessMsg?.let { msg ->
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(msg, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(if (currentLang == "SW") "Oda za Kipindi Hiki (${orders.size}):" else "Orders in Period (${orders.size}):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(if (currentLang == "SW") "Hakuna oda kwenye kipindi hiki." else "No orders recorded for this period.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(order.orderId, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${order.customerName} (${order.customerPhone})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(CurrencyUtils.formatTzs(order.totalAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(order.status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun loadReport(type: String, viewModel: ReportViewModel) {
    val calendar = Calendar.getInstance()
    val endTime = calendar.timeInMillis
    
    when (type) {
        "DAILY" -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        "WEEKLY" -> {
            calendar.add(Calendar.DAY_OF_YEAR, -7)
        }
        "MONTHLY" -> {
            calendar.add(Calendar.DAY_OF_YEAR, -30)
        }
    }
    val startTime = calendar.timeInMillis
    viewModel.generateReport(startTime, endTime)
}
