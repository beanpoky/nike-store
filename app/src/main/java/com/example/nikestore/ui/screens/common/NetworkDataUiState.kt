package com.example.nikestore.ui.screens.common

import com.example.nikestore.common.NikeException

sealed interface NetworkDataUiState<out T> {
    data object Loading : NetworkDataUiState<Nothing>
    data class Success<T>(val data: T) : NetworkDataUiState<T>
    data class Error(val exception: NikeException) : NetworkDataUiState<Nothing>
}