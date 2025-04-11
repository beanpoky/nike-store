package com.example.nikestore.data.repo

import com.example.nikestore.data.Comment
import com.example.nikestore.data.repo.source.CommentDataSource

class CommentRepositoryImpl(
    private val commentRemoteDataSource: CommentDataSource
) : CommentRepository {

    override suspend fun getComments(productId: Int): List<Comment> =
        commentRemoteDataSource.getComments(productId)
}