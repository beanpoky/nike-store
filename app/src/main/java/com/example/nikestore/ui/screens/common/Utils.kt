package com.example.nikestore.ui.screens.common

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nikestore.R
import com.example.nikestore.common.NikeException
import com.example.nikestore.ui.theme.NikeTheme

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection),
            modifier = Modifier.padding(bottom = 4.dp),
            color = NikeTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
        )
        TextButton(
            onClick = retryAction,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.textButtonColors(contentColor = NikeTheme.colors.textButtonContent)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.retry),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = NikeTheme.colors.loadingProgress
        )
    }
}

@Composable
fun ErrorImageScreen(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.BrokenImage,
        contentDescription = stringResource(R.string.image_not_found),
        modifier = modifier.size(52.dp),
        tint = NikeTheme.colors.error
    )
}

@Composable
fun LoadingImageScreen(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier.size(48.dp),
        color = NikeTheme.colors.loadingProgress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NikeTopAppBar(
    title: String,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = NikeTheme.colors.appBarTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NikeTheme.colors.appBar),
        navigationIcon = {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(contentColor = NikeTheme.colors.appBarIcon)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = actions
    )
}

@Composable
fun HandleNetworkState(
    networkActionUiState: NetworkActionUiState<*>,
    onStateHandled: () -> Unit,
    onAuthRequired: () -> Unit = {},
    onSuccessHandled: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(networkActionUiState) {
        when (networkActionUiState) {
            is NetworkActionUiState.Error -> {
                val nikeException = networkActionUiState.exception
                when (nikeException.type) {
                    NikeException.Type.SIMPLE -> {
                        showToast(
                            context = context,
                            message = nikeException.serverMessage
                                ?: context.getString(nikeException.userFriendlyMessage)
                        )
                    }

                    NikeException.Type.AUTH -> {
                        onAuthRequired()
                        showToast(context, nikeException.serverMessage!!)
                    }
                }
                onStateHandled()
            }

            is NetworkActionUiState.Success -> {
                onSuccessHandled()
                onStateHandled()
            }

            else -> {}
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}