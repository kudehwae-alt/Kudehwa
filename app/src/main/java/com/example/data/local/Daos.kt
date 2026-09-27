package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantityChange WHERE id = :productId")
    suspend fun updateStock(productId: Int, quantityChange: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
}

@Dao
interface InventoryDao {
    @Insert
    suspend fun insertTransaction(transaction: InventoryTransactionEntity)

    @Query("SELECT * FROM inventory_transactions WHERE productId = :productId ORDER BY timestamp DESC")
    fun getTransactionsForProduct(productId: Int): Flow<List<InventoryTransactionEntity>>
}

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE orderId = :orderId")
    suspend fun getPaymentsForOrder(orderId: String): List<PaymentEntity>

    @Query("UPDATE payments SET status = :status WHERE paymentId = :paymentId")
    suspend fun updatePaymentStatus(paymentId: String, status: String)
}

@Dao
interface DeliveryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity)

    @Query("SELECT * FROM deliveries WHERE orderId = :orderId")
    suspend fun getDeliveryForOrder(orderId: String): DeliveryEntity?

    @Query("UPDATE deliveries SET status = :status, deliveryStaffId = :staffId WHERE deliveryId = :deliveryId")
    suspend fun updateDeliveryStatus(deliveryId: String, status: String, staffId: String?)
}

@Dao
interface DeliveryLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocation(location: DeliveryLocationEntity)

    @Query("SELECT * FROM delivery_locations WHERE deliveryId = :deliveryId")
    suspend fun getLocation(deliveryId: String): DeliveryLocationEntity?
}

@Dao
interface AnalyticsDao {
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'PAID'")
    suspend fun getTotalSales(): Double?

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getTotalOrders(): Int

    @Query("SELECT COUNT(*) FROM deliveries WHERE status = 'DELIVERED'")
    suspend fun getTotalDeliveries(): Int

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity < 10")
    suspend fun getLowStockCount(): Int

    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startTime AND :endTime")
    suspend fun getOrdersByDateRange(startTime: Long, endTime: Long): List<OrderEntity>

    @Query("SELECT * FROM payments WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun getPaymentsByDateRange(startTime: Long, endTime: Long): List<PaymentEntity>

    @Query("SELECT * FROM deliveries WHERE updatedAt BETWEEN :startTime AND :endTime")
    suspend fun getDeliveriesByDateRange(startTime: Long, endTime: Long): List<DeliveryEntity>
}

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insertLog(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    suspend fun getRecentLogs(): List<AuditLogEntity>
}
