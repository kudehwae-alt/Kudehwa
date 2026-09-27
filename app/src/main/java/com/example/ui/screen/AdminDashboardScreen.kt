package com.example.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.ui.viewmodel.AnalyticsViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AnalyticsViewModel,
    onBackClick: () -> Unit = {},
    onNavigateProducts: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onNavigateStaff: () -> Unit = {},
    onNavigateReports: () -> Unit = {}
) {
    val metrics by viewModel.metrics.collectAsState()
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsState()

    var showPromoDialog by remember { mutableStateOf(false) }
    val currentBadge by com.example.data.PromotionalConfig.badgeText.collectAsState()
    val currentTitle by com.example.data.PromotionalConfig.bannerTitle.collectAsState()
    val currentSubtitle by com.example.data.PromotionalConfig.bannerSubtitle.collectAsState()
    val currentImageUrl by com.example.data.PromotionalConfig.bannerImageUrl.collectAsState()
    var badgeInput by remember { mutableStateOf("") }
    var titleInput by remember { mutableStateOf("") }
    var subtitleInput by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }

    var showContactDialog by remember { mutableStateOf(false) }
    val currentPhone by com.example.data.ContactConfig.phone.collectAsState()
    val currentWhatsapp by com.example.data.ContactConfig.whatsapp.collectAsState()
    val currentWhatsappMessage by com.example.data.ContactConfig.whatsappMessage.collectAsState()
    val currentInstagram by com.example.data.ContactConfig.instagram.collectAsState()
    val currentFacebook by com.example.data.ContactConfig.facebook.collectAsState()
    val currentTiktok by com.example.data.ContactConfig.tiktok.collectAsState()
    val currentEmail by com.example.data.ContactConfig.email.collectAsState()
    val currentAddress by com.example.data.ContactConfig.address.collectAsState()

    var phoneInput by remember { mutableStateOf("") }
    var whatsappInput by remember { mutableStateOf("") }
    var whatsappMessageInput by remember { mutableStateOf("") }
    var instagramInput by remember { mutableStateOf("") }
    var facebookInput by remember { mutableStateOf("") }
    var tiktokInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Dashibodi ya Usimamizi (Admin)" else "Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Rudi")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { com.example.data.LanguageManager.toggleLanguage() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.testTag("admin_lang_switch")
                    ) {
                        Text(
                            if (currentLang == "SW") "EN 🇬🇧" else "SW 🇹🇿",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(onClick = onNavigateProducts, modifier = Modifier.weight(1f)) {
                    Text(if (currentLang == "SW") "Bidhaa" else "Products")
                }
                Button(onClick = onNavigateOrders, modifier = Modifier.weight(1f)) {
                    Text(if (currentLang == "SW") "Oda" else "Orders")
                }
                Button(onClick = onNavigateStaff, modifier = Modifier.weight(1f)) {
                    Text(if (currentLang == "SW") "Timu" else "Staff")
                }
                Button(onClick = onNavigateReports, modifier = Modifier.weight(1f)) {
                    Text(if (currentLang == "SW") "Ripoti" else "Reports")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateOrders() },
                colors = CardDefaults.cardColors(
                    containerColor = if (metrics.newOrdersCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (metrics.newOrdersCount > 0) (if (currentLang == "SW") "Oda Mpya (${metrics.newOrdersCount}) Zinahitaji Uangalizi!" else "New Orders (${metrics.newOrdersCount}) Need Attention!") else (if (currentLang == "SW") "Hakuna Oda Mpya kwa Sasa" else "No New Orders Currently"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentLang == "SW") "Gusa hapa kuangalia na kusimamia oda zote" else "Tap here to view and manage all orders",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = if (currentLang == "SW") "Takwimu za Duka (Analytics)" else "Store Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AnalyticsCard(
                        title = if (currentLang == "SW") "Jumla ya Mauzo" else "Total Revenue",
                        value = com.example.util.CurrencyUtils.formatTzs(metrics.totalSales),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                item {
                    AnalyticsCard(
                        title = if (currentLang == "SW") "Jumla ya Oda" else "Total Orders",
                        value = "${metrics.totalOrders}",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                item {
                    AnalyticsCard(
                        title = if (currentLang == "SW") "Jumla ya Deliveries" else "Total Deliveries",
                        value = "${metrics.totalDeliveries}",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                item {
                    AnalyticsCard(
                        title = if (currentLang == "SW") "Bidhaa Zilizobaki Kidogo" else "Low Stock Items",
                        value = "${metrics.lowStockCount}",
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    badgeInput = currentBadge
                    titleInput = currentTitle
                    subtitleInput = currentSubtitle
                    imageUrlInput = currentImageUrl
                    showPromoDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (currentLang == "SW") "Hariri Matangazo & Bango la Nyumbani" else "Edit Promos & Home Banner")
            }

            Button(
                onClick = {
                    phoneInput = currentPhone
                    whatsappInput = currentWhatsapp
                    whatsappMessageInput = currentWhatsappMessage
                    instagramInput = currentInstagram
                    facebookInput = currentFacebook
                    tiktokInput = currentTiktok
                    emailInput = currentEmail
                    addressInput = currentAddress
                    showContactDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (currentLang == "SW") "Usimamizi wa Mawasiliano & Mitandao" else "Contact & Social Management")
            }
        }

        if (showPromoDialog) {
            AlertDialog(
                onDismissRequest = { showPromoDialog = false },
                title = { Text(if (currentLang == "SW") "Hariri Bango la Matangazo" else "Edit Promotional Banner") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = badgeInput,
                            onValueChange = { badgeInput = it },
                            label = { Text(if (currentLang == "SW") "Kichwa Kidogo / Badge (f.h. HOT DEAL)" else "Badge Text (e.g. HOT DEAL)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text(if (currentLang == "SW") "Kichwa Kikuu (f.h. Punguzo la 20%)" else "Main Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = subtitleInput,
                            onValueChange = { subtitleInput = it },
                            label = { Text(if (currentLang == "SW") "Ujumbe (f.h. Kwenye simu zote mpya)" else "Subtitle") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = imageUrlInput,
                            onValueChange = { imageUrlInput = it },
                            label = { Text(if (currentLang == "SW") "Picha URL (Kutoka mtandaoni)" else "Image URL") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.PickVisualMedia()
                        ) { uri: android.net.Uri? ->
                            uri?.let {
                                imageUrlInput = it.toString()
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (currentLang == "SW") "Chagua Picha kutoka Simu" else "Pick Image from Phone")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        com.example.data.PromotionalConfig.updateBanner(
                            badge = badgeInput,
                            title = titleInput,
                            subtitle = subtitleInput,
                            imageUrl = imageUrlInput
                        )
                        showPromoDialog = false
                    }) {
                        Text(if (currentLang == "SW") "Hifadhi" else "Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPromoDialog = false }) {
                        Text(if (currentLang == "SW") "Ghairi" else "Cancel")
                    }
                }
            )
        }

        if (showContactDialog) {
            AlertDialog(
                onDismissRequest = { showContactDialog = false },
                title = { Text(if (currentLang == "SW") "Usimamizi wa Mawasiliano & Mitandao" else "Contact & Social Management") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text(if (currentLang == "SW") "Namba ya Simu (f.h. 0715000000)" else "Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = whatsappInput,
                            onValueChange = { whatsappInput = it },
                            label = { Text(if (currentLang == "SW") "WhatsApp Namba (f.h. 255715000000)" else "WhatsApp Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = whatsappMessageInput,
                            onValueChange = { whatsappMessageInput = it },
                            label = { Text(if (currentLang == "SW") "Ujumbe wa WhatsApp (Default Message)" else "WhatsApp Default Message") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = instagramInput,
                            onValueChange = { instagramInput = it },
                            label = { Text("Instagram Link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = facebookInput,
                            onValueChange = { facebookInput = it },
                            label = { Text("Facebook Link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tiktokInput,
                            onValueChange = { tiktokInput = it },
                            label = { Text("TikTok Link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text(if (currentLang == "SW") "Anwani / Eneo" else "Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        com.example.data.ContactConfig.updateContacts(
                            newPhone = phoneInput,
                            newWhatsapp = whatsappInput,
                            newWhatsappMessage = whatsappMessageInput,
                            newInstagram = instagramInput,
                            newFacebook = facebookInput,
                            newTiktok = tiktokInput,
                            newEmail = emailInput,
                            newAddress = addressInput
                        )
                        showContactDialog = false
                    }) {
                        Text(if (currentLang == "SW") "Hifadhi" else "Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showContactDialog = false }) {
                        Text(if (currentLang == "SW") "Ghairi" else "Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
