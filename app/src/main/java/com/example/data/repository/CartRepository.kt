package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.CartItemEntity
import com.example.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao
) {
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addToCart(item: CartItem) {
        cartDao.insertOrUpdateCartItem(item.toEntity())
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.removeCartItem(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}

fun CartItemEntity.toDomain() = CartItem(
    productId = productId,
    name = name,
    price = price,
    quantity = quantity
)

fun CartItem.toEntity() = CartItemEntity(
    productId = productId,
    name = name,
    price = price,
    quantity = quantity
)
