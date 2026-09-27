package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) {
        withContext(Dispatchers.IO) {
            val productDao = db.productDao()
            val categoryDao = db.categoryDao()

            // Check if categories or products exist
            val existingProduct = productDao.getProductById(1)
            if (existingProduct != null) {
                return@withContext
            }

            // Seed Categories
            val categories = listOf(
                CategoryEntity(1, "All / Zote", "all"),
                CategoryEntity(2, "Apple iPhone", "iphone"),
                CategoryEntity(3, "Samsung Galaxy", "samsung"),
                CategoryEntity(4, "Budget Phones", "budget"),
                CategoryEntity(5, "Accessories & Chargers", "accessories"),
                CategoryEntity(6, "Smartwatches & Audio", "audio-watch")
            )
            categoryDao.insertCategories(categories)

            // Seed Products with real Tanzanian pricing (TZS)
            val products = listOf(
                ProductEntity(
                    id = 1,
                    name = "Apple iPhone 15 Pro Max 256GB",
                    sku = "IP15PM-256-NAT",
                    brand = "Apple",
                    price = 3350000.0,
                    stockQuantity = 14,
                    categoryId = 2,
                    imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 2,
                    name = "Apple iPhone 15 128GB Blue",
                    sku = "IP15-128-BLU",
                    brand = "Apple",
                    price = 2250000.0,
                    stockQuantity = 18,
                    categoryId = 2,
                    imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 3,
                    name = "Apple iPhone 14 Pro 128GB Deep Purple",
                    sku = "IP14P-128-PUR",
                    brand = "Apple",
                    price = 2300000.0,
                    stockQuantity = 9,
                    categoryId = 2,
                    imageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 4,
                    name = "Apple iPhone 13 128GB Midnight",
                    sku = "IP13-128-BLK",
                    brand = "Apple",
                    price = 1650000.0,
                    stockQuantity = 22,
                    categoryId = 2,
                    imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 5,
                    name = "Samsung Galaxy S24 Ultra 512GB",
                    sku = "SAM-S24U-512",
                    brand = "Samsung",
                    price = 3450000.0,
                    stockQuantity = 12,
                    categoryId = 3,
                    imageUrl = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 6,
                    name = "Samsung Galaxy S24+ 256GB Onyx",
                    sku = "SAM-S24P-256",
                    brand = "Samsung",
                    price = 2400000.0,
                    stockQuantity = 10,
                    categoryId = 3,
                    imageUrl = "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 7,
                    name = "Samsung Galaxy A55 5G 256GB",
                    sku = "SAM-A55-256",
                    brand = "Samsung",
                    price = 980000.0,
                    stockQuantity = 35,
                    categoryId = 3,
                    imageUrl = "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 8,
                    name = "Samsung Galaxy A15 128GB",
                    sku = "SAM-A15-128",
                    brand = "Samsung",
                    price = 385000.0,
                    stockQuantity = 50,
                    categoryId = 3,
                    imageUrl = "https://images.unsplash.com/photo-1546054454-aa26e2b734c7?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 9,
                    name = "Tecno Camon 30 Pro 5G 512GB",
                    sku = "TEC-C30P-512",
                    brand = "Tecno",
                    price = 850000.0,
                    stockQuantity = 25,
                    categoryId = 4,
                    imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 10,
                    name = "Infinix Note 40 Pro 256GB MagCharge",
                    sku = "INF-N40P-256",
                    brand = "Infinix",
                    price = 690000.0,
                    stockQuantity = 30,
                    categoryId = 4,
                    imageUrl = "https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 11,
                    name = "Xiaomi Redmi Note 13 Pro+ 5G 512GB",
                    sku = "RED-N13PP-512",
                    brand = "Xiaomi",
                    price = 930000.0,
                    stockQuantity = 20,
                    categoryId = 4,
                    imageUrl = "https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 12,
                    name = "Xiaomi Redmi 13C 128GB Clover Green",
                    sku = "RED-13C-128",
                    brand = "Xiaomi",
                    price = 330000.0,
                    stockQuantity = 60,
                    categoryId = 4,
                    imageUrl = "https://images.unsplash.com/photo-1512499617640-c74ae3a79d37?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 13,
                    name = "Apple AirPods Pro 2 (USB-C)",
                    sku = "APP-AIRPOD-P2",
                    brand = "Apple",
                    price = 650000.0,
                    stockQuantity = 28,
                    categoryId = 6,
                    imageUrl = "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 14,
                    name = "Samsung 45W Super Fast Charger 2.0",
                    sku = "ACC-SAM-45W",
                    brand = "Samsung",
                    price = 850000.0 / 10, // 85,000 TZS
                    stockQuantity = 70,
                    categoryId = 5,
                    imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 15,
                    name = "Apple 20W USB-C Power Adapter",
                    sku = "ACC-APP-20W",
                    brand = "Apple",
                    price = 65000.0,
                    stockQuantity = 80,
                    categoryId = 5,
                    imageUrl = "https://images.unsplash.com/photo-1622445262464-84b1456045b6?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 16,
                    name = "Oraimo 65W GaN Multi-Port Charger",
                    sku = "ACC-ORA-65W",
                    brand = "Oraimo",
                    price = 75000.0,
                    stockQuantity = 65,
                    categoryId = 5,
                    imageUrl = "https://images.unsplash.com/photo-1585338107529-13afc5f02586?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 17,
                    name = "Anker 20,000mAh PowerCore 22.5W",
                    sku = "ACC-ANK-20K",
                    brand = "Anker",
                    price = 125000.0,
                    stockQuantity = 40,
                    categoryId = 5,
                    imageUrl = "https://images.unsplash.com/photo-1609592426806-258d44c9b207?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 18,
                    name = "Apple Watch Series 9 45mm GPS",
                    sku = "WAT-APP-S9",
                    brand = "Apple",
                    price = 1180000.0,
                    stockQuantity = 15,
                    categoryId = 6,
                    imageUrl = "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 19,
                    name = "Samsung Galaxy Watch 6 44mm",
                    sku = "WAT-SAM-W6",
                    brand = "Samsung",
                    price = 750000.0,
                    stockQuantity = 16,
                    categoryId = 6,
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80"
                ),
                ProductEntity(
                    id = 20,
                    name = "Oraimo FreePods 4 ANC Wireless",
                    sku = "AUD-ORA-FP4",
                    brand = "Oraimo",
                    price = 95000.0,
                    stockQuantity = 55,
                    categoryId = 6,
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80"
                )
            )

            productDao.insertProducts(products)
        }
    }
}
