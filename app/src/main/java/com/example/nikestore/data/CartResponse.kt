package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    @SerialName(value = "cart_items")
    val cartItems: List<CartItem>,
    @SerialName(value = "payable_price")
    val payablePrice: Int,
    @SerialName(value = "shipping_cost")
    val shippingCost: Int,
    @SerialName(value = "total_price")
    val totalPrice: Int
)

@Serializable
data class PurchaseDetail(
    var totalPrice: Int,
    var shippingCost: Int,
    var payablePrice: Int
)