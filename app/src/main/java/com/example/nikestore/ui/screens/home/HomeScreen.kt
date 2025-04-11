package com.example.nikestore.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nikestore.R
import com.example.nikestore.data.Banner
import com.example.nikestore.data.Product
import com.example.nikestore.data.SORT_LATEST
import com.example.nikestore.data.SORT_POPULAR
import com.example.nikestore.ui.screens.common.ErrorImageScreen
import com.example.nikestore.ui.screens.common.ErrorScreen
import com.example.nikestore.ui.screens.common.LoadingImageScreen
import com.example.nikestore.ui.screens.common.LoadingScreen
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.theme.NikeTheme
import mx.platacard.pagerindicator.PagerIndicatorOrientation
import mx.platacard.pagerindicator.PagerWormIndicator
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

@Composable
fun HomeScreen(
    navigateToProductDetail: (Product) -> Unit,
    navigateToProductList: (Int) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val homeUiState = homeViewModel.networkDataUiState

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (homeUiState) {
            is NetworkDataUiState.Loading -> LoadingScreen()
            is NetworkDataUiState.Error -> ErrorScreen(retryAction = homeViewModel::getHomeData)
            is NetworkDataUiState.Success -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchTextField(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp)
                    )
                    BannerPager(
                        banners = homeUiState.data.banners,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    SectionHeader(
                        viewAllAction = { navigateToProductList(SORT_LATEST) },
                        titleRes = R.string.latest,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ProductList(
                        products = homeUiState.data.latestProducts,
                        navigateToProductDetail = navigateToProductDetail,
                        onFavoriteChange = { homeViewModel.addToFavorites(it) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    SectionHeader(
                        viewAllAction = { navigateToProductList(SORT_POPULAR) },
                        titleRes = R.string.most_popular,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ProductList(
                        products = homeUiState.data.popularProducts,
                        navigateToProductDetail = navigateToProductDetail,
                        onFavoriteChange = { homeViewModel.addToFavorites(it) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTextField(modifier: Modifier = Modifier) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.Content),
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.labelLarge,
                color = NikeTheme.colors.inputHint
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = NikeTheme.colors.textPrimary
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { keyboardController?.hide() }
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NikeTheme.colors.inputFocused,
            unfocusedBorderColor = NikeTheme.colors.inputUnfocused,
            focusedTextColor = NikeTheme.colors.textPrimary,
            unfocusedTextColor = NikeTheme.colors.textPrimary,
            cursorColor = NikeTheme.colors.inputFocused
        )
    )
}

@Composable
fun BannerPager(
    banners: List<Banner>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { banners.size }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(state = pagerState) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1.77f),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(banners[page].image)
                            .crossfade(true)
                            .build(),
                        contentDescription = banners[page].linkValue,
                        contentScale = ContentScale.Crop,
                        error = { ErrorImageScreen() },
                        loading = { LoadingImageScreen() }
                    )
                }
            }
        }
        PagerWormIndicator(
            pagerState = pagerState,
            activeDotColor = Color(0xFF2A90EC),
            dotColor = Color(0xFFF0F0F0),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.BottomCenter),
            dotCount = banners.size,
            orientation = PagerIndicatorOrientation.Horizontal
        )
    }
}

@Composable
fun SectionHeader(
    viewAllAction: () -> Unit,
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleRes),
            color = NikeTheme.colors.textPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = viewAllAction,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.textButtonColors(contentColor = NikeTheme.colors.textButtonContent)
        ) {
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    navigateToProductDetail: (Product) -> Unit,
    onFavoriteChange: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(products) { product ->
            ProductItem(
                product = product,
                onClick = { navigateToProductDetail(product) },
                onFavoriteChange = { onFavoriteChange(product) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    onFavoriteChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "scaleAnimation"
    )

    Card(
        modifier = modifier
            .width(160.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
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
            IconToggleButton(
                checked = product.isFavorite,
                onCheckedChange = { onFavoriteChange() },
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp)
                    .align(Alignment.TopStart),
                colors = IconButtonDefaults.iconToggleButtonColors(
                    containerColor = Color.LightGray,
                    checkedContainerColor = Color.LightGray
                )
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite),
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
            }
        }
        Text(
            text = product.title,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge.copy(
                textDirection = TextDirection.Content,
                fontWeight = FontWeight.Bold
            ),
            color = NikeTheme.colors.textPrimary
        )
        Text(
            text = buildAnnotatedString {
                append(NumberFormat.getNumberInstance().format(product.previousPrice))
                withStyle(SpanStyle(fontSize = 9.sp)) {
                    append("  " + stringResource(R.string.price_format))
                }
            },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp),
            textDecoration = TextDecoration.LineThrough,
            style = MaterialTheme.typography.labelSmall,
            color = NikeTheme.colors.textSecondary
        )
        Text(
            text = buildAnnotatedString {
                append(NumberFormat.getNumberInstance().format(product.price))
                withStyle(SpanStyle(fontSize = 10.sp)) {
                    append("  " + stringResource(R.string.price_format))
                }
            },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = NikeTheme.colors.textPrimary
        )
    }
}