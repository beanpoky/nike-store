package com.example.nikestore.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nikestore.R
import com.example.nikestore.ui.screens.main.MainViewModel
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navigateToLogin: () -> Unit,
    navigateToFavoriteProducts: () -> Unit,
    mainViewModel: MainViewModel,
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val email by profileViewModel.email.collectAsStateWithLifecycle()
    val isSignIn by profileViewModel.isSignIn.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileViewModel.updateAuthState()
        mainViewModel.getCartItemCount()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHeader(
            username = if (isSignIn) email else stringResource(R.string.guest_user),
            modifier = Modifier.padding(top = 28.dp)
        )
        HorizontalDivider(
            color = NikeTheme.colors.divider,
            modifier = Modifier.padding(top = 24.dp)
        )
        ProfileItemRow(
            icon = Icons.Default.FavoriteBorder,
            titleRes = R.string.wishlist,
            onClick = navigateToFavoriteProducts
        )
        HorizontalDivider(color = NikeTheme.colors.divider)
        ProfileItemRow(
            icon = Icons.Default.AllInbox,
            titleRes = R.string.order_history,
            onClick = {}
        )
        HorizontalDivider(color = NikeTheme.colors.divider)
        ProfileItemRow(
            icon = if (isSignIn) Icons.AutoMirrored.Default.Logout else Icons.Default.Key,
            titleRes = if (isSignIn) R.string.logout else R.string.login_screen_title,
            onClick = {
                if (isSignIn) profileViewModel.logout() else navigateToLogin()
                mainViewModel.getCartItemCount()
            }
        )
        HorizontalDivider(color = NikeTheme.colors.divider)
    }
}

@Composable
fun ProfileHeader(
    username: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .size(72.dp)
                .border(1.dp, NikeTheme.colors.divider, CircleShape)
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_nike_logo),
                contentDescription = stringResource(R.string.profile),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(NikeTheme.colors.textPrimary)
            )
        }
        Text(
            text = username,
            color = NikeTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileItemRow(
    icon: ImageVector,
    @StringRes titleRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(titleRes),
            tint = NikeTheme.colors.textPrimary
        )
        Text(
            text = stringResource(titleRes),
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            color = NikeTheme.colors.textPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}