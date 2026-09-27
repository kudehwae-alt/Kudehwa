package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.local.InventoryTransactionEntity
import com.example.domain.model.InventoryTransactionType
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val db: AppDatabase
) {
    private val productDao = db.productDao()
    private val inventoryDao = db.inventoryDao()

    suspend fun recordTransaction(
        productId: Int,
        quantityChange: Int,
        type: InventoryTransactionType,
        reason: String?
    ) {
        db.withTransaction {
            val product = productDao.getProductById(productId)
            val currentStock = product?.stockQuantity ?: 0
            
            if (currentStock + quantityChange < 0) {
                throw Exception("Insufficient stock")
            }

            productDao.updateStock(productId, quantityChange)
            inventoryDao.insertTransaction(
                InventoryTransactionEntity(
                    productId = productId,
                    quantityChange = quantityChange,
                    type = type.name,
                    reason = reason
                )
            )
        }
    }

    fun getTransactionsForProduct(productId: Int): Flow<List<InventoryTransactionEntity>> =
        inventoryDao.getTransactionsForProduct(productId)
}
