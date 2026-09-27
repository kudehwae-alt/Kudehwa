package com.example.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StaffManager
import com.example.data.local.OrderEntity
import com.example.ui.theme.KudehwaAccent
import com.example.ui.theme.KudehwaSuccess
import com.example.ui.viewmodel.OrderTrackingViewModel
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: OrderTrackingViewModel,
    initialOrderId: String? = null,
    onBackClick: () -> Unit
) {
    var orderId by remember { mutableStateOf(initialOrderId ?: "") }
    var phone by remember { mutableStateOf("") }
    val order by viewModel.order.collectAsState()
    val error by viewModel.error.collectAsState()
    val recentOrders by viewModel.recentOrders.collectAsState()
    val staffList by StaffManager.staffList.collectAsState()
    val context = LocalContext.current
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsStateWithLifecycle()

    LaunchedEffect(initialOrderId) {
        if (!initialOrderId.isNullOrBlank()) {
            viewModel.trackOrder(initialOrderId, "")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Fuatilia Oda (Track Order)" else "Track Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Order ID Selector from Recent Orders
            if (recentOrders.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            if (currentLang == "SW") "Oda za Hivi Punde (Gusa kuchagua Order ID):" else "Recent Orders (Tap to select Order ID):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recentOrders) { rec ->
                                Surface(
                                    modifier = Modifier.clickable {
                                        orderId = rec.orderId
                                        viewModel.trackOrder(rec.orderId, "")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(rec.orderId, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        Text(CurrencyUtils.formatTzs(rec.totalAmount), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search Order Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        if (currentLang == "SW") "Weka Nambari ya Oda Yako:" else "Enter Your Order Number:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = orderId,
                        onValueChange = { orderId = it },
                        label = { Text(if (currentLang == "SW") "Order ID (Mfano: KUD-20260927-1042)" else "Order ID (e.g. KUD-20260927-1042)") },
                        modifier = Modifier.fillMaxWidth().testTag("track_order_id_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Receipt, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (currentLang == "SW") "Nambari ya Simu (Hiari)" else "Phone Number (Optional)") },
                        modifier = Modifier.fillMaxWidth().testTag("track_phone_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) }
                    )

                    Button(
                        onClick = { viewModel.trackOrder(orderId.trim(), phone.trim()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("track_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (currentLang == "SW") "Tafuta Hali ya Oda" else "Track Order Status", fontWeight = FontWeight.Bold)
                    }
                }
            }

            error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(it, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            order?.let { ord ->
                OrderDetailsCard(ord)
            }

            // Msaada wa Haraka & Namba za Wafanyakazi (Admin, Driver, Staff)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Msaada wa Haraka & Wafanyakazi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }
                    Text(
                        "Namba za simu za Admin, Dereva na Staff zinasasishwa kiotomatiki kutoka kwenye mfumo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Dynamic Staff List Display
                    staffList.forEach { staff ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(staff.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Surface(
                                            color = when (staff.role) {
                                                "ADMIN" -> MaterialTheme.colorScheme.primaryContainer
                                                "DRIVER" -> KudehwaAccent.copy(alpha = 0.2f)
                                                else -> KudehwaSuccess.copy(alpha = 0.15f)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                staff.role,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(staff.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.phone}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Piga", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${staff.phone.replace("+", "").replace(" ", "")}?text=Habari%20${staff.name},%20kuhusu%20oda%20yangu"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = KudehwaSuccess, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsCard(order: OrderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nambari ya Oda:", style = MaterialTheme.typography.labelSmall)
                    Text(order.orderId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }

                Surface(
                    color = when (order.status) {
                        "DELIVERED" -> KudehwaSuccess.copy(alpha = 0.15f)
                        "OUT_FOR_DELIVERY" -> KudehwaAccent.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        when (order.status) {
                            "PLACED" -> "Imepokelewa"
                            "CONFIRMED" -> "Imethibitishwa"
                            "PREPARING" -> "Inafungashwa"
                            "OUT_FOR_DELIVERY" -> "Iko Njiani"
                            "DELIVERED" -> "Imefikishwa"
                            else -> order.status
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "DELIVERED" -> KudehwaSuccess
                            "OUT_FOR_DELIVERY" -> Color(0xFFB45309)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider()

            // Timeline steps
            Text("Hatua za Usafirishaji:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

            val currentStep = when (order.status) {
                "PLACED" -> 1
                "CONFIRMED" -> 2
                "PREPARING" -> 3
                "OUT_FOR_DELIVERY" -> 4
                "DELIVERED" -> 5
                else -> 2
            }

            TimelineStepItem(
                stepNumber = 1,
                title = "Oda Imepokelewa",
                description = "Mfumo umehifadhi oda yako kikamilifu.",
                isCompleted = currentStep >= 1,
                isCurrent = currentStep == 1
            )
            TimelineStepItem(
                stepNumber = 2,
                title = "Imethibitishwa na Duka",
                description = "Meneja wa stoo amethibitisha upatikanaji wa simu na vifaa.",
                isCompleted = currentStep >= 2,
                isCurrent = currentStep == 2
            )
            TimelineStepItem(
                stepNumber = 3,
                title = "Inafungashwa (Packaging)",
                description = "Simu inakaguliwa na kufungwa kwenye kifurushi salama chenye warranty card.",
                isCompleted = currentStep >= 3,
                isCurrent = currentStep == 3
            )
            TimelineStepItem(
                stepNumber = 4,
                title = "Iko Njiani (Out for Delivery)",
                description = "Dereva bodaboda/gari wa Kudehwa yuko njiani kukuletea.",
                isCompleted = currentStep >= 4,
                isCurrent = currentStep == 4
            )
            TimelineStepItem(
                stepNumber = 5,
                title = "Imefikishwa (Delivered)",
                description = "Mteja amepokea na kukagua simu yake.",
                isCompleted = currentStep >= 5,
                isCurrent = currentStep == 5
            )

            HorizontalDivider()

            // Customer and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Mteja:", style = MaterialTheme.typography.labelSmall)
                    Text(order.customerName, fontWeight = FontWeight.SemiBold)
                    Text(order.customerPhone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Jumla ya Malipo:", style = MaterialTheme.typography.labelSmall)
                    Text(
                        CurrencyUtils.formatTzs(order.totalAmount),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineStepItem(
    stepNumber: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCompleted) KudehwaSuccess else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("$stepNumber", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (stepNumber < 5) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (isCompleted) KudehwaSuccess else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
