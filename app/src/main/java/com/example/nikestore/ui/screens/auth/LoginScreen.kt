package com.example.nikestore.ui.screens.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nikestore.R
import com.example.nikestore.common.NikeException
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import com.example.nikestore.ui.screens.common.showToast
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel

object LoginDestination : NavigationDestination {
    override val route = "LOGIN"
    override val titleRes = R.string.login_screen_title
}

@Composable
fun LoginScreen(
    navigateToSignUp: () -> Unit,
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
            AuthHeader(title = LoginDestination.titleRes)
            AuthInput(
                email = authData.email,
                onEmailChange = { authViewModel.updateEmail(it) },
                password = authData.password,
                onPasswordChange = { authViewModel.updatePassword(it) },
                authAction = { authViewModel.login(authData.email, authData.password) },
                authUiState = authUiState,
                buttonText = R.string.login,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            AuthActionPrompt(
                promptText = R.string.dont_have_account,
                promptButtonText = R.string.sign_up,
                promptAction = navigateToSignUp
            )
        }
    }
}

@Composable
fun AuthHeader(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nike_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(90.dp),
            tint = NikeTheme.colors.textPrimary
        )
        Text(
            text = stringResource(title),
            color = NikeTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = stringResource(R.string.login_screen_subtitle),
            color = NikeTheme.colors.textSecondary,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AuthInput(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    authAction: () -> Unit,
    authUiState: NetworkActionUiState<*>,
    @StringRes buttonText: Int,
    modifier: Modifier = Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelLarge,
            label = {
                Text(
                    text = stringResource(R.string.email),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(R.string.email),
                    tint = NikeTheme.colors.textPrimary
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NikeTheme.colors.inputFocused,
                unfocusedBorderColor = NikeTheme.colors.inputUnfocused,
                focusedTextColor = NikeTheme.colors.textPrimary,
                unfocusedTextColor = NikeTheme.colors.textPrimary,
                focusedLabelColor = NikeTheme.colors.inputFocused,
                unfocusedLabelColor = NikeTheme.colors.inputHint,
                cursorColor = NikeTheme.colors.inputFocused
            )
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp),
            textStyle = MaterialTheme.typography.labelLarge,
            label = {
                Text(
                    text = stringResource(R.string.password),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = stringResource(R.string.password),
                    tint = NikeTheme.colors.textPrimary
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, stringResource(R.string.password))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NikeTheme.colors.inputFocused,
                unfocusedBorderColor = NikeTheme.colors.inputUnfocused,
                focusedTextColor = NikeTheme.colors.textPrimary,
                unfocusedTextColor = NikeTheme.colors.textPrimary,
                focusedLabelColor = NikeTheme.colors.inputFocused,
                unfocusedLabelColor = NikeTheme.colors.inputHint,
                cursorColor = NikeTheme.colors.inputFocused
            )
        )
        Button(
            onClick = { if (authUiState != NetworkActionUiState.Loading) authAction() },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            enabled = email.isNotBlank() && password.isNotBlank(),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = NikeTheme.colors.button,
                contentColor = NikeTheme.colors.buttonContent
            )
        ) {
            if (authUiState == NetworkActionUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    color = NikeTheme.colors.buttonContent
                )
            } else {
                Text(
                    text = stringResource(buttonText),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AuthActionPrompt(
    @StringRes promptText: Int,
    @StringRes promptButtonText: Int,
    promptAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(promptText),
            color = NikeTheme.colors.textTertiary,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = promptAction,
            colors = ButtonDefaults.textButtonColors(
                contentColor = NikeTheme.colors.textButtonContent
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = stringResource(promptButtonText),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}