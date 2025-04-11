package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    @SerialName(value = "product_id")
    val productId: Int
)