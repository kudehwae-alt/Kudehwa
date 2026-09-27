package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductDao
import com.example.data.local.ProductEntity
import com.example.domain.model.Category
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao? = null
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts().map { entities ->
        entities.map { it.toDomain() }
    }

    val allCategories: Flow<List<Category>>? = categoryDao?.getAllCategories()?.map { entities ->
        entities.map { Category(it.id, it.name, it.slug) }
    }

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)?.toDomain()

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(
            ProductEntity(
                id = product.id,
                name = product.name,
                sku = product.sku,
                brand = product.brand,
                price = product.price,
                stockQuantity = product.stockQuantity,
                categoryId = product.categoryId,
                imageUrl = product.imageUrl
            )
        )
    }

    suspend fun deleteProduct(id: Int) {
        productDao.deleteProductById(id)
    }
}

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    sku = sku,
    brand = brand,
    price = price,
    stockQuantity = stockQuantity,
    categoryId = categoryId,
    imageUrl = imageUrl
)
