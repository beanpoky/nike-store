package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitOrderRequest(
    @SerialName(value = "first_name")
    val firstName: String,
    @SerialName(value = "last_name")
    val lastName: String,
    @SerialName(value = "postal_code")
    val postalCode: Int,
    @SerialName(value = "mobile")
    val phoneNumber: Int,
    val address: String,
    @SerialName(value = "payment_method")
    val paymentMethod: String
)