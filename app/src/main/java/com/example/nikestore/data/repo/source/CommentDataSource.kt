package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Comment

interface CommentDataSource {

    suspend fun getComments(productId: Int): List<Comment>
}