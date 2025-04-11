package com.example.nikestore.ui.screens.common

import com.example.nikestore.common.NikeException

sealed interface NetworkActionUiState<out S> {
    data object Idle : NetworkActionUiState<Nothing>
    data object Loading : NetworkActionUiState<Nothing>
    data class Success<S>(val data: S) : NetworkActionUiState<S>
    data class Error(val exception: NikeException) : NetworkActionUiState<Nothing>
}