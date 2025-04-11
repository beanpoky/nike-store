package com.example.nikestore.data.repo

import com.example.nikestore.data.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun getProducts(sort: Int): List<Product>

    fun getFavoriteProducts(): Flow<List<Product>>

    suspend fun addToFavorites(product: Product)

    suspend fun deleteFromFavorites(product: Product)
}