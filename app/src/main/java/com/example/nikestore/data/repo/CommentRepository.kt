package com.example.nikestore.data.repo

import com.example.nikestore.data.Comment

interface CommentRepository {

    suspend fun getComments(productId: Int): List<Comment>
}