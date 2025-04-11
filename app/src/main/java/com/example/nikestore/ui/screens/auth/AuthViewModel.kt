package com.example.nikestore.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nikestore.common.NikeExceptionMapper
import com.example.nikestore.data.TokenResponse
import com.example.nikestore.data.repo.UserRepository
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AuthData(
    val email: String = "",
    val password: String = ""
)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    var networkActionUiState: NetworkActionUiState<TokenResponse> by mutableStateOf(
        NetworkActionUiState.Idle
    )
        private set
    private val _authData = MutableStateFlow(AuthData())
    val authData = _authData.asStateFlow()

    fun login(email: String, password: String) {
        networkActionUiState = NetworkActionUiState.Loading
        viewModelScope.launch {
            networkActionUiState = try {
                val response = userRepository.login(email, password)
                if (response.isSuccessful) {
                    userRepository.onSuccessfulLogin(email, response.body()!!)
                    NetworkActionUiState.Success(response.body()!!)
                } else
                    throw HttpException(response)
            } catch (throwable: Throwable) {
                NetworkActionUiState.Error(NikeExceptionMapper.map(throwable))
            }
        }
    }

    fun signUp(email: String, password: String) {
        networkActionUiState = NetworkActionUiState.Loading
        viewModelScope.launch {
            networkActionUiState = try {
                val signUpResponse = userRepository.signUp(email, password)
                if (!signUpResponse.isSuccessful)
                    throw HttpException(signUpResponse)

                val loginResponse = userRepository.login(email, password)
                if (loginResponse.isSuccessful) {
                    userRepository.onSuccessfulLogin(email, loginResponse.body()!!)
                    NetworkActionUiState.Success(loginResponse.body()!!)
                } else
                    throw HttpException(signUpResponse)
            } catch (throwable: Throwable) {
                NetworkActionUiState.Error(NikeExceptionMapper.map(throwable))
            }
        }
    }

    fun resetNetworkState() {
        networkActionUiState = NetworkActionUiState.Idle
    }

    fun updateEmail(email: String) {
        _authData.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _authData.update { currentState ->
            currentState.copy(password = password)
        }
    }
}