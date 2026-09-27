package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.InventoryRepository
import com.example.domain.model.InventoryTransactionType
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    fun recordTransaction(
        productId: Int,
        quantityChange: Int,
        type: InventoryTransactionType,
        reason: String?
    ) {
        viewModelScope.launch {
            try {
                repository.recordTransaction(productId, quantityChange, type, reason)
            } catch (e: Exception) {
                // Handle error (e.g., show Toast/Snackbar)
            }
        }
    }
}
