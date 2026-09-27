package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnalyticsDao
import com.example.data.local.DeliveryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.PaymentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(
    private val analyticsDao: AnalyticsDao
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders

    private val _payments = MutableStateFlow<List<PaymentEntity>>(emptyList())
    val payments: StateFlow<List<PaymentEntity>> = _payments

    private val _deliveries = MutableStateFlow<List<DeliveryEntity>>(emptyList())
    val deliveries: StateFlow<List<DeliveryEntity>> = _deliveries

    fun generateReport(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            _orders.value = analyticsDao.getOrdersByDateRange(startTime, endTime)
            _payments.value = analyticsDao.getPaymentsByDateRange(startTime, endTime)
            _deliveries.value = analyticsDao.getDeliveriesByDateRange(startTime, endTime)
        }
    }
}
