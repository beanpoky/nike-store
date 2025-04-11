package com.example.nikestore.data.repo.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nikestore.data.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductLocalDataSource : ProductDataSource {

    override suspend fun getProducts(sort: Int): List<Product> {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM products")
    override fun getFavoriteProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun addToFavorites(product: Product)

    @Delete
    override suspend fun deleteFromFavorites(product: Product)
}