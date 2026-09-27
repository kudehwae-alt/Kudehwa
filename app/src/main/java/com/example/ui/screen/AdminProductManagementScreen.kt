package com.example.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.Product
import com.example.data.repository.ProductRepository
import com.example.ui.theme.KudehwaSuccess
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductManagementScreen(
    productRepository: ProductRepository,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val products by productRepository.allProducts.collectAsState(initial = emptyList())
    val currentLang by com.example.data.LanguageManager.currentLanguage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    var nameInput by remember { mutableStateOf("") }
    var brandInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var stockInput by remember { mutableStateOf("") }
    var skuInput by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf(2) }

    val isDialogOpen = showAddDialog || editingProduct != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == "SW") "Usimamizi wa Bidhaa (Products)" else "Product Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingProduct = null
                    nameInput = ""
                    brandInput = ""
                    priceInput = ""
                    stockInput = "10"
                    skuInput = "KUD-9272026-${Random.nextInt(10000, 99999)}"
                    imageUrlInput = ""
                    selectedCatId = 2
                    showAddDialog = true 
                },
                containerColor = KudehwaSuccess
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                if (currentLang == "SW") "Orodha ya Bidhaa na Accessories Zote (${products.size})" else "All Products & Accessories (${products.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (currentLang == "SW") "Hakuna bidhaa zilizopatikana." else "No products found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product Image Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!product.imageUrl.isNullOrBlank()) {
                                        coil.compose.AsyncImage(
                                            model = product.imageUrl,
                                            contentDescription = product.name,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(if (product.categoryId == 5) "🔌" else "📱", style = MaterialTheme.typography.titleMedium)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val catName = when(product.categoryId) {
                                        2 -> "iPhone"
                                        3 -> "Samsung"
                                        4 -> "Budget"
                                        5 -> "Accessories"
                                        6 -> "Smartwatch/Audio"
                                        else -> "General"
                                    }
                                    Text("Category: $catName | SKU: ${product.sku}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("TZS ${String.format("%,d", product.price.toLong())}", fontWeight = FontWeight.Bold, color = KudehwaSuccess)
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "Stock: ${product.stockQuantity} pcs",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Column {
                                    IconButton(onClick = {
                                        editingProduct = product
                                        nameInput = product.name
                                        brandInput = product.brand
                                        priceInput = product.price.toLong().toString()
                                        stockInput = product.stockQuantity.toString()
                                        skuInput = product.sku
                                        imageUrlInput = product.imageUrl ?: ""
                                        selectedCatId = product.categoryId
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        scope.launch {
                                            productRepository.deleteProduct(product.id)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { 
                    showAddDialog = false 
                    editingProduct = null
                },
                title = { Text(if (editingProduct != null) "Edit Product (${editingProduct?.name})" else "Add New Product or Accessory") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Product Name (e.g. iPhone 15 / Oraimo Charger)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = brandInput,
                            onValueChange = { brandInput = it },
                            label = { Text("Brand (e.g. Apple / Oraimo)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Price (TZS)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = stockInput,
                            onValueChange = { stockInput = it },
                            label = { Text("Stock Quantity (pcs)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = skuInput,
                            onValueChange = { skuInput = it },
                            label = { Text("SKU Code (Auto-generated KUD-9272026-xxxxx)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = imageUrlInput,
                            onValueChange = { imageUrlInput = it },
                            label = { Text("Image URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
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
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick Image from Gallery")
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select Category:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        val catList = listOf(
                            2 to "iPhone",
                            3 to "Samsung",
                            4 to "Budget",
                            5 to "Accessories",
                            6 to "Smartwatch/Audio"
                        )
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(catList) { (catId, label) ->
                                FilterChip(
                                    selected = selectedCatId == catId,
                                    onClick = { selectedCatId = catId },
                                    label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val priceVal = priceInput.toDoubleOrNull() ?: 0.0
                            val stockVal = stockInput.toIntOrNull() ?: 0
                            if (nameInput.isNotBlank() && priceVal > 0) {
                                scope.launch {
                                    val productId = editingProduct?.id ?: (System.currentTimeMillis() % 100000).toInt()
                                    val finalSku = if (skuInput.isNotBlank()) skuInput else "KUD-9272026-${Random.nextInt(10000, 99999)}"
                                    val updatedProduct = Product(
                                        id = productId,
                                        name = nameInput,
                                        sku = finalSku,
                                        brand = if (brandInput.isNotBlank()) brandInput else "Kudehwa",
                                        price = priceVal,
                                        stockQuantity = stockVal,
                                        categoryId = selectedCatId,
                                        imageUrl = if (imageUrlInput.isNotBlank()) imageUrlInput else null
                                    )
                                    productRepository.insertProduct(updatedProduct)
                                    showAddDialog = false
                                    editingProduct = null
                                    nameInput = ""
                                    brandInput = ""
                                    priceInput = ""
                                    stockInput = ""
                                    skuInput = ""
                                    imageUrlInput = ""
                                    selectedCatId = 2
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KudehwaSuccess)
                    ) {
                        Text(if (editingProduct != null) "Update" else "Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showAddDialog = false 
                        editingProduct = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
