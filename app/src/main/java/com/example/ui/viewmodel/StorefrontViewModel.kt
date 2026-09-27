package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CartRepository
import com.example.data.repository.ProductRepository
import com.example.domain.model.CartItem
import com.example.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StorefrontViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(0) // 0 means All
    val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

    private val _sortBy = MutableStateFlow("FEATURED") // FEATURED, PRICE_LOW, PRICE_HIGH
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val cartItemCount: StateFlow<Int> = cartRepository.cartItems
        .combine(MutableStateFlow(Unit)) { items, _ ->
            items.sumOf { it.quantity }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val uiState: StateFlow<List<Product>> = combine(
        productRepository.allProducts,
        _searchQuery,
        _selectedCategoryId,
        _sortBy
    ) { products, query, catId, sort ->
        var list = products

        // Filter by category
        if (catId > 0) {
            list = list.filter { it.categoryId == catId }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.sku.lowercase().contains(q)
            }
        }

        // Sorting
        when (sort) {
            "PRICE_LOW" -> list.sortedBy { it.price }
            "PRICE_HIGH" -> list.sortedByDescending { it.price }
            else -> list
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: Int) {
        _selectedCategoryId.value = categoryId
    }

    fun onSortSelected(sortOption: String) {
        _sortBy.value = sortOption
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = 1
                )
            )
            _message.value = "${product.name} imeongezwa kwenye Cart!"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
