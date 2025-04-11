package com.example.nikestore.data.repo

import com.example.nikestore.data.Product
import com.example.nikestore.data.repo.source.ProductDataSource
import com.example.nikestore.data.repo.source.ProductLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductRepositoryImpl(
    private val remoteDataSource: ProductDataSource,
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {

    override suspend fun getProducts(sort: Int): List<Product> {
        val favoriteProducts = localDataSource.getFavoriteProducts().first()
        val remoteProducts = remoteDataSource.getProducts(sort)

        val favoriteProductIds = favoriteProducts.map { it.id }
        return remoteProducts.map { product ->
            product.apply { isFavorite = favoriteProductIds.contains(product.id) }
        }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> = localDataSource.getFavoriteProducts()

    override suspend fun addToFavorites(product: Product) = localDataSource.addToFavorites(product)

    override suspend fun deleteFromFavorites(product: Product) =
        localDataSource.deleteFromFavorites(product)
}