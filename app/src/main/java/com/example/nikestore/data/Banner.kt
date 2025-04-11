package com.example.nikestore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Int,
    val image: String,
    @SerialName(value = "link_type")
    val linkType: Int,
    @SerialName(value = "link_value")
    val linkValue: String
)