package com.example.nikestore.data.repo.source

import com.example.nikestore.data.AddToCartRequest
import com.example.nikestore.data.AddToCartResponse
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.CartResponse
import com.example.nikestore.data.ChangeCountCartRequest
import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.RemoveFromCartRequest
import com.example.nikestore.services.http.ApiService
import retrofit2.Response

class CartRemoteDataSource(private val apiService: ApiService) : CartDataSource {

    override suspend fun addToCart(productId: Int): Response<AddToCartResponse> =
        apiService.addToCart(AddToCartRequest(productId))

    override suspend fun get(): CartResponse = apiService.getCart()

    override suspend fun remove(cartItemId: Int): Response<MessageResponse> =
        apiService.removeItemFromCart(RemoveFromCartRequest(cartItemId))

    override suspend fun changeCount(cartItemId: Int, count: Int): Response<AddToCartResponse> =
        apiService.changeCount(ChangeCountCartRequest(cartItemId, count))

    override suspend fun getCartItemsCart(): CartItemCount = apiService.getCartItemsCount()
}