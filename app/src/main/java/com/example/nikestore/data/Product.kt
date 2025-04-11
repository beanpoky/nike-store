package com.example.nikestore.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class Product(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val price: Int,
    val discount: Int,
    val image: String,
    val status: Int,
    @SerialName(value = "previous_price")
    val previousPrice: Int
) {
    var isFavorite: Boolean = false
}

const val SORT_LATEST = 0
const val SORT_POPULAR = 1
const val SORT_PRICE_DESC = 2
const val SORT_PRICE_ASC = 3