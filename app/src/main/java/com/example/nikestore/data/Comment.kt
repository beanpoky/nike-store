package com.example.nikestore.data

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val author: Author
)