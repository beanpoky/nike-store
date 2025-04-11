package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Checkout(
    @SerialName(value = "purchase_success")
    val purchaseSuccess: Boolean,
    @SerialName(value = "payable_price")
    val payablePrice: Int,
    @SerialName(value = "payment_status")
    val paymentStatus: String
)