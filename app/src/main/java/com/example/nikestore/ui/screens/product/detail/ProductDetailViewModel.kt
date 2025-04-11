package com.example.nikestore.ui.screens.product.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.AddToCartResponse
import com.example.nikestore.data.Comment
import com.example.nikestore.data.Product
import com.example.nikestore.data.repo.CartRepository
import com.example.nikestore.data.repo.CommentRepository
import com.example.nikestore.data.repo.ProductRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : BaseViewModel<List<Comment>, AddToCartResponse>() {
    private val productJson: String =
        checkNotNull(savedStateHandle[ProductDetailDestination.PRODUCT_ARG])
    val product: Product = Json.decodeFromString(productJson)

    var isFavorite by mutableStateOf(product.isFavorite)
        private set

    init {
        getComments()
    }

    fun getComments() {
        safeLaunchData {
            commentRepository.getComments(product.id)
        }
    }

    fun addToCart() {
        safeLaunchAction {
            cartRepository.addToCart(product.id)
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            if (product.isFavorite)
                productRepository.deleteFromFavorites(product)
            else
                productRepository.addToFavorites(product)
            isFavorite = !product.isFavorite
        }
    }
}