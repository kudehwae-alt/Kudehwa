package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.OrderEntity
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminOrderViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders

    val newOrdersCount: StateFlow<Int> = _orders.map { list ->
        list.count { it.status == "PLACED" || it.status == "NEW" || it.status.isBlank() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            val all = repository.getAllOrders()
            // Filter out delivered orders from active order management list
            // while preserving them in the database for total deliveries, sales, and monthly reports.
            _orders.value = all.filter { it.status != "DELIVERED" }
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            loadOrders() // Refresh
        }
    }
}
