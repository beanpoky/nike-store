package com.example.nikestore.ui.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nikestore.R
import com.example.nikestore.data.EmptyState
import com.example.nikestore.data.Product
import com.example.nikestore.data.repo.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    val favoriteProducts: StateFlow<List<Product>> = productRepository.getFavoriteProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )

    var emptyState: EmptyState by mutableStateOf(
        EmptyState(
            mustShow = true,
            imageResId = R.drawable.ic_no_data,
            messageResId = R.string.you_havent_added_any_products_to_your_wishlist_yet
        )
    )
        private set

    fun deleteFromFavorites(product: Product) {
        viewModelScope.launch {
            productRepository.deleteFromFavorites(product)
        }
    }
}