package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName(value = "token_type")
    val tokenType: String,
    @SerialName(value = "expires_in")
    val expiresIn: Int,
    @SerialName(value = "access_token")
    val accessToken: String,
    @SerialName(value = "refresh_token")
    val refreshToken: String
)