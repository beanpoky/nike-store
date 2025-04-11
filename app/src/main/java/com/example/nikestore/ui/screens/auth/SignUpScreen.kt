package com.example.nikestore.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nikestore.R
import com.example.nikestore.common.NikeException
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import com.example.nikestore.ui.screens.common.showToast
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel

object SignUpDestination : NavigationDestination {
    override val route = "SIGN_UP"
    override val titleRes = R.string.sign_up_screen_title
}

@Composable
fun SignUpScreen(
    navigateToLogin: () -> Unit,
    onAuthSuccess: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val authUiState = authViewModel.networkActionUiState
    val authData by authViewModel.authData.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(authUiState) {
        when (authUiState) {
            is NetworkActionUiState.Error -> {
                val nikeException = authUiState.exception
                when (nikeException.type) {
                    NikeException.Type.SIMPLE -> {
                        showToast(
                            context = context,
                            message = nikeException.serverMessage
                                ?: context.getString(nikeException.userFriendlyMessage)
                        )
                    }

                    else -> {}
                }
                authViewModel.resetNetworkState()
            }

            is NetworkActionUiState.Success -> {
                onAuthSuccess()
                authViewModel.resetNetworkState()
            }

            else -> {}
        }
    }

    Scaffold(
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AuthHeader(title = SignUpDestination.titleRes)
            AuthInput(
                email = authData.email,
                onEmailChange = { authViewModel.updateEmail(it) },
                password = authData.password,
                onPasswordChange = { authViewModel.updatePassword(it) },
                authAction = { authViewModel.signUp(authData.email, authData.password) },
                authUiState = authUiState,
                buttonText = R.string.sign_up,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            AuthActionPrompt(
                promptText = R.string.already_registered,
                promptButtonText = R.string.login,
                promptAction = navigateToLogin
            )
        }
    }
}