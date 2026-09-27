package com.example.data.repository

import com.example.data.local.ProductDao
import com.example.data.local.ProductEntity
import com.example.domain.model.Product

class AdminRepository(
    private val productDao: ProductDao
) {
    suspend fun addProduct(product: Product) {
        productDao.insertProducts(listOf(product.toEntity()))
    }

    suspend fun deleteProduct(id: Int) {
        productDao.deleteProductById(id)
    }

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        sku = sku,
        brand = brand,
        price = price,
        stockQuantity = stockQuantity,
        categoryId = categoryId,
        imageUrl = imageUrl
    )
}
