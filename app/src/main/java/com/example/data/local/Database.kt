package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class, CategoryEntity::class, InventoryTransactionEntity::class, CartItemEntity::class, OrderEntity::class, OrderItemEntity::class, PaymentEntity::class, DeliveryEntity::class, DeliveryLocationEntity::class, AuditLogEntity::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentDao(): PaymentDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun deliveryLocationDao(): DeliveryLocationDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun auditLogDao(): AuditLogDao
}
