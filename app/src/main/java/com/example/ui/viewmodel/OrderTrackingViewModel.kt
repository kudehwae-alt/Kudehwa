package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.OrderEntity
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderTrackingViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    private val _order = MutableStateFlow<OrderEntity?>(null)
    val order: StateFlow<OrderEntity?> = _order

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _recentOrders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val recentOrders: StateFlow<List<OrderEntity>> = _recentOrders

    init {
        loadRecentOrders()
    }

    fun loadRecentOrders() {
        viewModelScope.launch {
            try {
                _recentOrders.value = repository.getAllOrders().take(5)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun trackOrder(orderId: String, phone: String) {
        viewModelScope.launch {
            try {
                if (orderId.isBlank()) {
                    _error.value = "Tafadhali weka Nambari ya Oda (Order ID)."
                    return@launch
                }
                val result = if (phone.isBlank()) {
                    repository.getOrderById(orderId)
                } else {
                    repository.getOrderByIdAndPhone(orderId, phone)
                }
                if (result != null) {
                    _order.value = result
                    _error.value = null
                } else {
                    _order.value = null
                    _error.value = "Oda '$orderId' haikupatikana. Hakikisha namba uliyoandika ni sahihi."
                }
            } catch (e: Exception) {
                _error.value = "Hitilafu imetokea: ${e.message}"
            }
        }
    }
}
