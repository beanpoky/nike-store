package com.example.nikestore.ui.screens.favorites

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nikestore.R
import com.example.nikestore.data.Product
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.EmptyState
import com.example.nikestore.ui.screens.common.ErrorImageScreen
import com.example.nikestore.ui.screens.common.LoadingImageScreen
import com.example.nikestore.ui.screens.common.NikeTopAppBar
import com.example.nikestore.ui.theme.NikeTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

object FavoriteProductsDestination : NavigationDestination {
    override val route = "FAVORITE_PRODUCTS"
    override val titleRes = R.string.wishlist
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteProductsScreen(
    onBack: () -> Unit,
    navigateToProductDetail: (Product) -> Unit,
    favoriteProductsViewModel: FavoriteProductsViewModel = koinViewModel()
) {
    val favoriteProducts by favoriteProductsViewModel.favoriteProducts.collectAsStateWithLifecycle()
    val emptyState = favoriteProductsViewModel.emptyState

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackBarMessage = stringResource(R.string.press_and_hold_on_the_product_to_delete_it)

    Scaffold(
        topBar = {
            NikeTopAppBar(
                title = stringResource(FavoriteProductsDestination.titleRes),
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(snackBarMessage)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = NikeTheme.colors.appBarIcon)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.info)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = NikeTheme.colors.button,
                    contentColor = NikeTheme.colors.buttonContent
                )
            }
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        if (favoriteProducts.isEmpty()) {
            EmptyState(emptyState, {})
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(favoriteProducts) { product ->
                    FavoriteProductItem(
                        product = product,
                        onClick = { navigateToProductDetail(product) },
                        onLongClick = { favoriteProductsViewModel.deleteFromFavorites(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteProductItem(
    product: Product,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    error = { ErrorImageScreen() },
                    loading = { LoadingImageScreen() }
                )
            }
            Text(
                text = product.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = NikeTheme.colors.textPrimary
            )
        }
    }
}