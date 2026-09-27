package com.example.domain.model

data class Category(
    val id: Int,
    val name: String,
    val slug: String
)

data class Product(
    val id: Int,
    val name: String,
    val sku: String,
    val brand: String,
    val price: Double,
    val stockQuantity: Int,
    val categoryId: Int,
    val imageUrl: String? = null
)
