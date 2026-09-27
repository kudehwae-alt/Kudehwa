package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalSales: Double = 0.0,
    val totalOrders: Int = 0,
    val totalDeliveries: Int = 0,
    val lowStockCount: Int = 0,
    val newOrdersCount: Int = 0
)

class AnalyticsViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _metrics = MutableStateFlow(DashboardMetrics())
    val metrics: StateFlow<DashboardMetrics> = _metrics

    init {
        loadMetrics()
    }

    private fun loadMetrics() {
        viewModelScope.launch {
            val analyticsDao = db.analyticsDao()
            val orders = db.orderDao().getAllOrders()
            val newCount = orders.count { it.status == "PLACED" || it.status == "NEW" || it.status.isBlank() }
            val salesFromDao = analyticsDao.getTotalSales() ?: 0.0
            val totalSales = if (salesFromDao == 0.0 && orders.isNotEmpty()) orders.sumOf { it.totalAmount } else salesFromDao
            
            val deliveriesFromDao = analyticsDao.getTotalDeliveries()
            val totalDeliveries = if (deliveriesFromDao == 0 && orders.isNotEmpty()) orders.count { it.status == "DELIVERED" } else deliveriesFromDao

            _metrics.value = DashboardMetrics(
                totalSales = totalSales,
                totalOrders = orders.size,
                totalDeliveries = totalDeliveries,
                lowStockCount = analyticsDao.getLowStockCount(),
                newOrdersCount = newCount
            )
        }
    }
}
