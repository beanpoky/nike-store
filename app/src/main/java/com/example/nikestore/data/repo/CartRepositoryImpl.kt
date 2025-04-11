package com.example.nikestore.data.repo

import com.example.nikestore.data.AddToCartResponse
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.CartResponse
import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.repo.source.CartDataSource
import retrofit2.Response

class CartRepositoryImpl(private val cartRemoteDataSource: CartDataSource) : CartRepository {

    override suspend fun addToCart(productId: Int): Response<AddToCartResponse> =
        cartRemoteDataSource.addToCart(productId)

    override suspend fun get(): CartResponse = cartRemoteDataSource.get()

    override suspend fun remove(cartItemId: Int): Response<MessageResponse> =
        cartRemoteDataSource.remove(cartItemId)

    override suspend fun changeCount(cartItemId: Int, count: Int): Response<AddToCartResponse> =
        cartRemoteDataSource.changeCount(cartItemId, count)

    override suspend fun getCartItemsCart(): CartItemCount = cartRemoteDataSource.getCartItemsCart()
}