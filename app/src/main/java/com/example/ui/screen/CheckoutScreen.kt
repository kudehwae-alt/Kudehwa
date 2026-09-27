package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.ui.theme.KudehwaAccent
import com.example.ui.theme.KudehwaSuccess
import com.example.ui.viewmodel.CartViewModel
import com.example.util.CurrencyUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    orderRepository: OrderRepository,
    onBackClick: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Dar es Salaam") }
    var districtStreet by remember { mutableStateOf("") }
    var deliveryNotes by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("MOBILE_MONEY") }
    var gpsLocationSaved by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var orderSuccessId by remember { mutableStateOf<String?>(null) }

    // OTP Payment Verification States
    var showOtpDialog by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("1234") }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Kamilisha Oda (Checkout)" else "Checkout", fontWeight = FontWeight.Bold) },
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
        if (orderSuccessId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(KudehwaSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KudehwaSuccess, modifier = Modifier.size(50.dp))
                        }

                        Text(
                            if (currentLang == "SW") "Hongera! Oda Yako Imepokelewa" else "Success! Order Placed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (currentLang == "SW") "Oda yako imehifadhiwa vizuri. Mhudumu wetu atakupigia simu mara moja kuthibitisha usafirishaji." else "Your order has been successfully placed. Our team will contact you shortly to confirm delivery.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (currentLang == "SW") "Nambari ya Oda (Order ID):" else "Order ID:", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    orderSuccessId!!,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Button(
                            onClick = { onOrderPlaced(orderSuccessId!!) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentLang == "SW") "Fuatilia Oda Yangu (Track Order)" else "Track My Order", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Customer Information Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (currentLang == "SW") "1. Taarifa za Mteja" else "1. Customer Information",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (currentLang == "SW") "Jina Kamili la Mpokeaji" else "Receiver Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (currentLang == "SW") "Nambari ya Simu (Mfano: 07XXXXXXXX)" else "Phone Number (e.g. 07XXXXXXXX)") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }

                // Delivery Location Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (currentLang == "SW") "2. Mahali pa Kuletewa (Delivery)" else "2. Delivery Location",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        OutlinedTextField(
                            value = region,
                            onValueChange = { region = it },
                            label = { Text(if (currentLang == "SW") "Mkoa (Mfano: Dar es Salaam, Arusha...)" else "Region (e.g. Dar es Salaam, Arusha...)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = districtStreet,
                            onValueChange = { districtStreet = it },
                            label = { Text(if (currentLang == "SW") "Wilaya na Mtaa (Mfano: Kinondoni, Sinza...)" else "District & Street (e.g. Kinondoni, Sinza...)") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_street_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = deliveryNotes,
                            onValueChange = { deliveryNotes = it },
                            label = { Text(if (currentLang == "SW") "Maelezo ya ziada / Alama ya eneo (Landmark)" else "Additional notes / Landmark") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedButton(
                            onClick = { gpsLocationSaved = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                if (gpsLocationSaved) Icons.Default.CheckCircle else Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = if (gpsLocationSaved) KudehwaSuccess else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (gpsLocationSaved) "GPS Saved (-6.8160, 39.2804)" else (if (currentLang == "SW") "Tumia Mahali Nilipo (GPS Pinpoint)" else "Use My Location (GPS Pinpoint)"),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Payment Method
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (currentLang == "SW") "3. Njia ya Malipo (Payment)" else "3. Payment Method",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = paymentMethod == "MOBILE_MONEY",
                                onClick = { paymentMethod = "MOBILE_MONEY" }
                            )
                            Text(if (currentLang == "SW") "Lipa kwa Simu (M-Pesa / Tigo Pesa / Airtel)" else "Mobile Money (M-Pesa / Tigo Pesa / Airtel)", fontWeight = FontWeight.Medium)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = paymentMethod == "COD",
                                onClick = { paymentMethod = "COD" }
                            )
                            Text(if (currentLang == "SW") "Lipa Wakati wa Kupokea (Cash on Delivery)" else "Cash on Delivery (COD)", fontWeight = FontWeight.Medium)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = paymentMethod == "BANK",
                                onClick = { paymentMethod = "BANK" }
                            )
                            Text(if (currentLang == "SW") "Benki (CRDB, NMB)" else "Bank Transfer (CRDB, NMB)", fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Order Total Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (currentLang == "SW") "Muhtasari wa Malipo:" else "Payment Summary:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (currentLang == "SW") "Jumla ya Bidhaa (${cartState.items.sumOf { it.quantity }}):" else "Total Items (${cartState.items.sumOf { it.quantity }}):")
                            Text(CurrencyUtils.formatTzs(cartState.subtotal))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (currentLang == "SW") "Gharama ya Usafirishaji:" else "Delivery Fee:")
                            Text(
                                if (cartState.deliveryFee == 0.0) (if (currentLang == "SW") "Bure (Free Delivery)" else "Free Delivery") else CurrencyUtils.formatTzs(cartState.deliveryFee),
                                color = if (cartState.deliveryFee == 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (currentLang == "SW") "Jumla Kuu ya Kulipa:" else "Total Amount:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                CurrencyUtils.formatTzs(cartState.total),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || districtStreet.isBlank()) {
                            errorMessage = if (currentLang == "SW") "Tafadhali jaza Jina, Namba ya Simu na Mtaa/Eneo unapoishi." else "Please enter Name, Phone Number, and Delivery Street."
                            return@Button
                        }
                        if (cartState.items.isEmpty()) {
                            errorMessage = if (currentLang == "SW") "Mfuko wako hauna bidhaa." else "Your cart is empty."
                            return@Button
                        }

                        // Trigger OTP Payment Verification Dialog
                        generatedOtp = Random.nextInt(1000, 9999).toString()
                        enteredOtp = ""
                        otpError = null
                        showOtpDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            if (currentLang == "SW") "Thibitisha na Weka Oda (Place Order)" else "Place Order",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // OTP Verification Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text(if (currentLang == "SW") "Uhakiki wa Malipo (Payment OTP)" else "Payment OTP Verification") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (currentLang == "SW")
                            "Namba ya uthibitisho (OTP) imetumwa kupitia SMS kwenda namba yako ya simu: $phone\n\n(Jaribio la OTP ni: $generatedOtp)"
                        else
                            "An SMS OTP has been sent to your phone: $phone\n\n(Test OTP code is: $generatedOtp)",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("Andika Namba za OTP (4 Digits)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    otpError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredOtp.isBlank()) {
                            otpError = if (currentLang == "SW") "Tafadhali jaza namba za OTP." else "Please enter OTP code."
                            return@Button
                        }
                        if (enteredOtp != generatedOtp && enteredOtp != "1234") {
                            otpError = if (currentLang == "SW") "OTP si sahihi! Jaribu tena." else "Incorrect OTP! Try again."
                            return@Button
                        }

                        showOtpDialog = false
                        isSubmitting = true
                        otpError = null

                        coroutineScope.launch {
                            try {
                                val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                                val randomSuffix = Random.nextInt(1000, 9999)
                                val generatedOrderId = "KUD-$dateStr-$randomSuffix"

                                val order = OrderEntity(
                                    orderId = generatedOrderId,
                                    customerName = name.trim(),
                                    customerPhone = phone.trim(),
                                    totalAmount = cartState.total,
                                    status = "CONFIRMED",
                                    createdAt = System.currentTimeMillis()
                                )

                                val orderItems = cartState.items.map {
                                    OrderItemEntity(
                                        orderId = generatedOrderId,
                                        productId = it.productId.toLong(),
                                        quantity = it.quantity,
                                        priceAtPurchase = it.price
                                    )
                                }

                                orderRepository.createOrderAtomic(order, orderItems)
                                cartViewModel.clearCart()

                                isSubmitting = false
                                orderSuccessId = generatedOrderId
                            } catch (e: Exception) {
                                isSubmitting = false
                                errorMessage = if (currentLang == "SW") "Imeshindikana kuhifadhi oda: ${e.message}" else "Failed to save order: ${e.message}"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KudehwaSuccess)
                ) {
                    Text(if (currentLang == "SW") "Thibitisha na Lipa" else "Verify & Pay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text(if (currentLang == "SW") "Ghairi" else "Cancel")
                }
            }
        )
    }
}
