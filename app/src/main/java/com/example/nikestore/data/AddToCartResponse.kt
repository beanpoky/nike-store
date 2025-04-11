package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddToCartResponse(
    val id: Int,
    val count: Int,
    @SerialName(value = "product_id")
    val productId: Int
)