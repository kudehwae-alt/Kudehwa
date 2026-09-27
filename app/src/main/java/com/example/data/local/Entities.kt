package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String
)

@Entity(tableName = "products", indices = [Index(value = ["categoryId"])])
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sku: String,
    val brand: String,
    val price: Double,
    val stockQuantity: Int,
    val categoryId: Int,
    val imageUrl: String?
)

@Entity(tableName = "inventory_transactions", indices = [Index(value = ["productId"])])
data class InventoryTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val quantityChange: Int,
    val type: String, // Stored as String
    val reason: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments", indices = [Index(value = ["orderId"])])
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val orderId: String,
    val amount: Double,
    val method: String, // MOBILE_MONEY, BANK, CARD, COD
    val status: String, // PENDING, PROCESSING, PAID, FAILED, REFUNDED
    val transactionReference: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "deliveries", indices = [Index(value = ["orderId"])])
data class DeliveryEntity(
    @PrimaryKey val deliveryId: String,
    val orderId: String,
    val deliveryStaffId: String?,
    val status: String, // ASSIGNED, PICKED_UP, OUT_FOR_DELIVERY, DELIVERED
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "delivery_locations")
data class DeliveryLocationEntity(
    @PrimaryKey val deliveryId: String,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actor: String,
    val action: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: String?
)
