package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Comment
import com.example.nikestore.services.http.ApiService

class CommentRemoteDataSource(private val apiService: ApiService) : CommentDataSource {

    override suspend fun getComments(productId: Int): List<Comment> =
        apiService.getComments(productId)
}