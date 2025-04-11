package com.example.nikestore.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _isSignIn = MutableStateFlow(TokenContainer.token != null)
    val isSignIn = _isSignIn.asStateFlow()

    init {
        viewModelScope.launch {
            _email.value = userRepository.getEmail()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            updateAuthState()
        }
    }

    fun updateAuthState() {
        _isSignIn.value = TokenContainer.token != null
    }
}