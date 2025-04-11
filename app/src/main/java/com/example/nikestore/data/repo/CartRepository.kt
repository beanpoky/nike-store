package com.example.nikestore.data.repo

import com.example.nikestore.data.AddToCartResponse
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.CartResponse
import com.example.nikestore.data.MessageResponse
import retrofit2.Response

interface CartRepository {

    suspend fun addToCart(productId: Int): Response<AddToCartResponse>

    suspend fun get(): CartResponse

    suspend fun remove(cartItemId: Int): Response<MessageResponse>

    suspend fun changeCount(cartItemId: Int, count: Int): Response<AddToCartResponse>

    suspend fun getCartItemsCart(): CartItemCount
}