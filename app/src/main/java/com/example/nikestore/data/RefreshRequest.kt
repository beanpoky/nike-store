package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    @SerialName(value = "grant_type")
    val grantType: String,
    @SerialName(value = "refresh_token")
    val refreshToken: String,
    @SerialName(value = "client_id")
    val clientId: Int,
    @SerialName(value = "client_secret")
    val clientSecret: String
)