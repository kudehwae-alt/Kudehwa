package com.example.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.Product
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.data.LanguageManager
import com.example.ui.theme.KudehwaAccent
import com.example.ui.theme.KudehwaNavy
import com.example.ui.theme.KudehwaSuccess
import com.example.ui.viewmodel.StorefrontViewModel
import com.example.util.CurrencyUtils
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StorefrontViewModel,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrderClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val products by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val currentLang by LanguageManager.currentLanguage.collectAsStateWithLifecycle()
    val isDark by com.example.data.ThemeManager.isDarkTheme.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = { onAdminClick() }
                                    )
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0A2458),
                            tonalElevation = 4.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Logo",
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Kudehwa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                if (currentLang == "SW") "Simu & Vifaa" else "Phone & Gadgets",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Theme Selector Button & Dialog
                    var showThemeDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
                    val currentTheme by com.example.data.ThemeManager.currentTheme.collectAsStateWithLifecycle()

                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Chagua Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (showThemeDialog) {
                        AlertDialog(
                            onDismissRequest = { showThemeDialog = false },
                            title = { Text(if (currentLang == "SW") "Chagua Theme ya Duka" else "Choose Store Theme") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    com.example.data.AppThemeStyle.values().forEach { style ->
                                        OutlinedButton(
                                            onClick = {
                                                com.example.data.ThemeManager.setTheme(style)
                                                showThemeDialog = false
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (currentTheme == style) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                            )
                                        ) {
                                            Text(
                                                text = style.displayName,
                                                fontWeight = if (currentTheme == style) FontWeight.Bold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showThemeDialog = false }) {
                                    Text(if (currentLang == "SW") "Funga" else "Close")
                                }
                            }
                        )
                    }

                    // Language Switch Button (EN / SW)
                    TextButton(
                        onClick = { LanguageManager.toggleLanguage() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.testTag("lang_switch_button")
                    ) {
                        Text(
                            if (currentLang == "SW") "EN 🇬🇧" else "SW 🇹🇿",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    IconButton(onClick = onTrackOrderClick) {
                        Icon(Icons.Outlined.LocalShipping, contentDescription = "Fuatilia Oda")
                    }

                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = KudehwaAccent,
                                    contentColor = Color.Black
                                ) {
                                    Text("$cartCount", fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Mfuko wa Manunuzi")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            val context = LocalContext.current
            val whatsapp by com.example.data.ContactConfig.whatsapp.collectAsState()
            val whatsappMessage by com.example.data.ContactConfig.whatsappMessage.collectAsState()
            ExtendedFloatingActionButton(
                onClick = {
                    com.example.data.ContactConfig.openWhatsApp(context, whatsapp, whatsappMessage)
                },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = "WhatsApp")
                Spacer(modifier = Modifier.width(8.dp))
                Text("WhatsApp", fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already here */ },
                    icon = { Icon(Icons.Filled.Storefront, contentDescription = "Duka") },
                    label = { Text(if (currentLang == "SW") "Duka" else "Store") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onCartClick,
                    icon = {
                        BadgedBox(badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = KudehwaAccent, contentColor = Color.Black) {
                                    Text("$cartCount")
                                }
                            }
                        }) {
                            Icon(Icons.Outlined.ShoppingCart, contentDescription = "Mfuko")
                        }
                    },
                    label = { Text(if (currentLang == "SW") "Cart ($cartCount)" else "Cart ($cartCount)") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onTrackOrderClick,
                    icon = { Icon(Icons.Outlined.LocalShipping, contentDescription = "Fuatilia") },
                    label = { Text(if (currentLang == "SW") "Fuatilia" else "Track") }
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar item
            item(span = { GridItemSpan(2) }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = 2.dp
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input"),
                        placeholder = {
                            Text(
                                if (currentLang == "SW")
                                    "Tafuta iPhone, Samsung, Tecno, Chaja..."
                                else
                                    "Search iPhone, Samsung, Tecno, Chargers..."
                            )
                        },
                        leadingIcon = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Tafuta",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Futa", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                } else {
                                    Icon(
                                        Icons.Default.Tune,
                                        contentDescription = "Chuja",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            // Promotional Banner Item with Image
            item(span = { GridItemSpan(2) }) {
                val badgeText by com.example.data.PromotionalConfig.badgeText.collectAsState()
                val bannerTitle by com.example.data.PromotionalConfig.bannerTitle.collectAsState()
                val bannerSubtitle by com.example.data.PromotionalConfig.bannerSubtitle.collectAsState()
                val bannerImageUrl by com.example.data.PromotionalConfig.bannerImageUrl.collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = bannerImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            KudehwaNavy.copy(alpha = 0.92f),
                                            KudehwaNavy.copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.75f),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = KudehwaAccent,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        badgeText,
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    bannerTitle,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Text(
                                    bannerSubtitle,
                                    color = Color(0xFFD6E4FF),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // New Arrivals horizontal showcase
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.FiberNew, contentDescription = null, tint = KudehwaAccent)
                            Text(
                                if (currentLang == "SW") "Bidhaa Mpya Zenye Moto" else "Hot New Arrivals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            if (currentLang == "SW") "Mpya" else "New",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val newProducts = products.take(5)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(newProducts) { product ->
                            Card(
                                modifier = Modifier
                                    .width(150.dp)
                                    .clickable { onProductClick(product.id) },
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        if (!product.imageUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = product.imageUrl,
                                                contentDescription = product.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .align(Alignment.Center),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Surface(
                                            color = KudehwaAccent,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                if (currentLang == "SW") "MPYA" else "NEW",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        product.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        CurrencyUtils.formatTzs(product.price),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories horizontal list
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (currentLang == "SW") "Makundi ya Bidhaa" else "Product Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val categories = listOf(
                        0 to if (currentLang == "SW") "Zote" else "All",
                        2 to "Apple iPhone",
                        3 to "Samsung Galaxy",
                        4 to if (currentLang == "SW") "Simu za Kawaida" else "Budget Phones",
                        5 to if (currentLang == "SW") "Accessories & Chaja" else "Accessories & Chargers",
                        6 to if (currentLang == "SW") "Smartwatches & Audio" else "Smartwatches & Audio"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(categories) { (id, label) ->
                            FilterChip(
                                selected = selectedCategory == id,
                                onClick = { viewModel.onCategorySelected(id) },
                                label = { Text(label, fontWeight = if (selectedCategory == id) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(20.dp),
                                leadingIcon = {
                                    if (selectedCategory == id) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Sort Header
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = sortBy == "FEATURED",
                            onClick = { viewModel.onSortSelected("FEATURED") },
                            label = { Text(if (currentLang == "SW") "Zote" else "All", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = sortBy == "PRICE_LOW",
                            onClick = { viewModel.onSortSelected("PRICE_LOW") },
                            label = { Text(if (currentLang == "SW") "Bei ya Chini" else "Low Price", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = sortBy == "PRICE_HIGH",
                            onClick = { viewModel.onSortSelected("PRICE_HIGH") },
                            label = { Text(if (currentLang == "SW") "Bei ya Juu" else "High Price", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Empty State
            if (products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                if (currentLang == "SW") "Hakuna bidhaa iliyopatikana" else "No products found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (currentLang == "SW") "Jaribu kubadili neno uliloandika au chagua kundi lingine." else "Try searching another term or selecting a different category.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Product Cards Grid
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    currentLang = currentLang,
                    onProductClick = { onProductClick(product.id) },
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }

            // Social Media & WhatsApp Contact Footer Card
            item(span = { GridItemSpan(2) }) {
                val context = LocalContext.current
                val phone by com.example.data.ContactConfig.phone.collectAsState()
                val whatsapp by com.example.data.ContactConfig.whatsapp.collectAsState()
                val whatsappMessage by com.example.data.ContactConfig.whatsappMessage.collectAsState()
                val instagram by com.example.data.ContactConfig.instagram.collectAsState()
                val facebook by com.example.data.ContactConfig.facebook.collectAsState()
                val tiktok by com.example.data.ContactConfig.tiktok.collectAsState()
                val address by com.example.data.ContactConfig.address.collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            if (currentLang == "SW") "Wasiliana Nasi & Mitandao ya Kijamii" else "Contact Us & Social Media",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            if (currentLang == "SW")
                                "Eneo: $address\nPiga/WhatsApp: $phone\nTufuate kwenye mitandao ya kijamii au uwasiliane nasi moja kwa moja."
                            else
                                "Location: $address\nCall/WhatsApp: $phone\nFollow us on social media or contact us directly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    com.example.data.ContactConfig.openWhatsApp(context, whatsapp, whatsappMessage)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KudehwaSuccess),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (currentLang == "SW") "Piga Simu" else "Call Us", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagram))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Instagram", style = MaterialTheme.typography.labelMedium)
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebook))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Facebook", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tiktok))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("TikTok", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    currentLang: String,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            // Top Badge and Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        product.brand,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = KudehwaSuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (currentLang == "SW") "Zipo ${product.stockQuantity}" else "Stock: ${product.stockQuantity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = KudehwaSuccess,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = product.sku,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price in TZS
            Text(
                text = CurrencyUtils.formatTzs(product.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("add_to_cart_${product.id}"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (currentLang == "SW") "Weka Cart" else "Add to Cart",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
