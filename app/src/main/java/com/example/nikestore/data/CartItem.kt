package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    @SerialName(value = "cart_item_id")
    val cartItemId: Int,
    var count: Int,
    val product: Product
)