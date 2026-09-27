package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PaymentEntity

class PaymentRepository(private val db: AppDatabase) {

    private val paymentDao = db.paymentDao()

    suspend fun recordPaymentAttempt(payment: PaymentEntity) {
        paymentDao.insertPayment(payment)
    }

    suspend fun getPaymentsForOrder(orderId: String): List<PaymentEntity> {
        return paymentDao.getPaymentsForOrder(orderId)
    }

    suspend fun updatePaymentStatus(paymentId: String, status: String) {
        paymentDao.updatePaymentStatus(paymentId, status)
    }
}
