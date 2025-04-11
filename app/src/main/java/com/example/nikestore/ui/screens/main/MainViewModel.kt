package com.example.nikestore.ui.screens.main

import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.repo.CartRepository

class MainViewModel(
    private val cartRepository: CartRepository
) : BaseViewModel<CartItemCount, Unit>() {
    var hasIntent = true
        private set

    init {
        getCartItemCount()
    }

    fun getCartItemCount() {
        safeLaunchData {
            cartRepository.getCartItemsCart()
        }
    }

    fun setIntent() {
        hasIntent = false
    }
}