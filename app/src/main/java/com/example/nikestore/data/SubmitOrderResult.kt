package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitOrderResult(
    @SerialName(value = "order_id")
    val orderId: Int,
    @SerialName(value = "bank_gateway_url")
    val bankGatewayUrl: String
)