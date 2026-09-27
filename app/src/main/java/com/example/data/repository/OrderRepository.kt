package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.DeliveryEntity
import com.example.data.local.CartDao
import com.example.data.local.AppDatabase
import androidx.room.withTransaction

class OrderRepository(
    private val db: AppDatabase
) {
    suspend fun createOrderAtomic(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ) {
        db.withTransaction {
            db.orderDao().createOrder(order, items)
            try {
                db.paymentDao().insertPayment(
                    PaymentEntity(
                        paymentId = "PAY-${order.orderId}",
                        orderId = order.orderId,
                        amount = order.totalAmount,
                        method = "MOBILE_MONEY",
                        status = "PAID",
                        transactionReference = "TXN-${System.currentTimeMillis()}",
                        timestamp = order.createdAt
                    )
                )
            } catch (e: Exception) {}

            try {
                db.deliveryDao().insertDelivery(
                    DeliveryEntity(
                        deliveryId = "DEL-${order.orderId}",
                        orderId = order.orderId,
                        deliveryStaffId = "1",
                        status = order.status,
                        updatedAt = order.createdAt
                    )
                )
            } catch (e: Exception) {}
        }
    }

    suspend fun getAllOrders(): List<OrderEntity> {
        return db.orderDao().getAllOrders()
    }

    suspend fun getOrderById(orderId: String): OrderEntity? {
        return db.orderDao().getOrderById(orderId)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        db.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun getOrderByIdAndPhone(orderId: String, phone: String): OrderEntity? {
        return db.orderDao().getOrderByIdAndPhone(orderId, phone)
    }
}
