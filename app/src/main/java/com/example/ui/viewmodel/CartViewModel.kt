package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CartRepository
import com.example.domain.model.CartItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0
)

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = cartRepository.cartItems.map { items ->
        val subtotal = items.sumOf { it.price * it.quantity }
        // Free delivery above 500,000 TZS, otherwise 5,000 TZS standard inside Tanzania
        val deliveryFee = if (subtotal > 500000 || subtotal == 0.0) 0.0 else 5000.0
        val total = subtotal + deliveryFee
        CartUiState(
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            total = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState()
    )

    fun incrementQuantity(item: CartItem) {
        viewModelScope.launch {
            cartRepository.addToCart(item.copy(quantity = item.quantity + 1))
        }
    }

    fun decrementQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                cartRepository.addToCart(item.copy(quantity = item.quantity - 1))
            } else {
                cartRepository.removeFromCart(item.productId)
            }
        }
    }

    fun removeItem(productId: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
