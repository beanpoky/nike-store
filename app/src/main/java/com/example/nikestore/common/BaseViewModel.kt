package com.example.nikestore.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseViewModel<T, S> : ViewModel() {
    var networkDataUiState: NetworkDataUiState<T> by mutableStateOf(NetworkDataUiState.Loading)
        private set
    var networkActionUiState: NetworkActionUiState<S> by mutableStateOf(NetworkActionUiState.Idle)
        private set

    protected fun safeLaunchData(block: suspend () -> T) {
        networkDataUiState = NetworkDataUiState.Loading
        viewModelScope.launch {
            networkDataUiState = try {
                NetworkDataUiState.Success(block())
            } catch (throwable: Throwable) {
                NetworkDataUiState.Error(NikeExceptionMapper.map(throwable))
            }
        }
    }

    protected fun safeLaunchAction(
        onSuccess: suspend () -> Unit = {},
        block: suspend () -> Response<S>
    ) {
        networkActionUiState = NetworkActionUiState.Loading
        viewModelScope.launch {
            networkActionUiState = try {
                val response = block()
                if (response.isSuccessful) {
                    onSuccess()
                    NetworkActionUiState.Success(response.body()!!)
                } else
                    throw HttpException(response)
            } catch (throwable: Throwable) {
                NetworkActionUiState.Error(NikeExceptionMapper.map(throwable))
            }
        }
    }

    protected fun updateDataState(newState: NetworkDataUiState<T>) {
        networkDataUiState = newState
    }

    fun resetNetworkState() {
        networkActionUiState = NetworkActionUiState.Idle
    }
}