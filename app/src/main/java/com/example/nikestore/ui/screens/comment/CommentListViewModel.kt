package com.example.nikestore.ui.screens.comment

import androidx.lifecycle.SavedStateHandle
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.Comment
import com.example.nikestore.data.repo.CommentRepository

class CommentListViewModel(
    savedStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository
) : BaseViewModel<List<Comment>, Unit>() {
    private val productId: Int =
        checkNotNull(savedStateHandle[CommentListDestination.PRODUCT_ID_ARG])

    init {
        getComments()
    }

    fun getComments() {
        safeLaunchData {
            commentRepository.getComments(productId)
        }
    }
}