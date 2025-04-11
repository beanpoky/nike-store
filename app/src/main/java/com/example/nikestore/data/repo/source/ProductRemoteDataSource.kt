package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Product
import com.example.nikestore.services.http.ApiService
import kotlinx.coroutines.flow.Flow

class ProductRemoteDataSource(private val apiService: ApiService) : ProductDataSource {

    override suspend fun getProducts(sort: Int): List<Product> = apiService.getProducts(sort)

    override fun getFavoriteProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavorites(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFromFavorites(product: Product) {
        TODO("Not yet implemented")
    }
}