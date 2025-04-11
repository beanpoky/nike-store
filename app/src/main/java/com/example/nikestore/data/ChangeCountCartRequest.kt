package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeCountCartRequest(
    @SerialName(value = "cart_item_id")
    val cartItemId: Int,
    val count: Int
)