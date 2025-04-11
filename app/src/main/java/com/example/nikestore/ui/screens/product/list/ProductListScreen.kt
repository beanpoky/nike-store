package com.example.nikestore.ui.screens.product.list

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nikestore.R
import com.example.nikestore.data.Product
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.ErrorImageScreen
import com.example.nikestore.ui.screens.common.ErrorScreen
import com.example.nikestore.ui.screens.common.LoadingImageScreen
import com.example.nikestore.ui.screens.common.LoadingScreen
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.screens.common.NikeTopAppBar
import com.example.nikestore.ui.screens.home.ProductItem
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

object ProductListDestination : NavigationDestination {
    override val route = "PRODUCT_LIST"
    override val titleRes = R.string.product_list
    const val SORT_ARG = "sort"
    val routeWithArg = "$route/{$SORT_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onBack: () -> Unit,
    navigateToProductDetail: (Product) -> Unit,
    productListViewModel: ProductListViewModel = koinViewModel()
) {
    val productsUiState = productListViewModel.networkDataUiState
    val productListUiState by productListViewModel.productListUiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            NikeTopAppBar(
                title = stringResource(ProductListDestination.titleRes),
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                ProductListHeader(
                    selectSort = { productListViewModel.showSortDialog(true) },
                    sortTitle = productListViewModel.sortTitles[productListUiState.sort],
                    selectLayout = { productListViewModel.selectLayout(!productListUiState.isLinearLayout) },
                    isLinearLayout = productListUiState.isLinearLayout
                )
                when (productsUiState) {
                    is NetworkDataUiState.Loading -> LoadingScreen()
                    is NetworkDataUiState.Error -> ErrorScreen(retryAction = productListViewModel::getProducts)
                    is NetworkDataUiState.Success -> {
                        if (productListUiState.isLinearLayout) {
                            LinearProductList(
                                products = productsUiState.data,
                                navigateToProductDetail = navigateToProductDetail,
                                onFavoriteChange = { productListViewModel.addToFavorites(it) },
                                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            )
                        } else {
                            GridProductList(
                                products = productsUiState.data,
                                navigateToProductDetail = navigateToProductDetail,
                                onFavoriteChange = { productListViewModel.addToFavorites(it) },
                                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            )
                        }
                    }
                }
            }
            if (productListUiState.isShowSortDialog) {
                SortDialog(
                    onDismissRequest = { productListViewModel.showSortDialog(false) },
                    selectSort = { productListViewModel.selectSort(it) },
                    sort = productListUiState.sort,
                    sortTitles = productListViewModel.sortTitles
                )
            }
        }
    }
}

@Composable
fun ProductListHeader(
    selectSort: () -> Unit,
    @StringRes sortTitle: Int,
    selectLayout: () -> Unit,
    isLinearLayout: Boolean,
    modifier: Modifier = Modifier,
    headerHeight: Dp = 56.dp
) {
    Column(modifier = modifier.background(NikeTheme.colors.backgroundOverlay)) {
        HorizontalDivider(color = NikeTheme.colors.divider)
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .height(headerHeight)
                    .weight(1f)
                    .clickable { selectSort() }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Sort,
                    contentDescription = stringResource(R.string.sort),
                    modifier = Modifier.padding(end = 16.dp),
                    tint = NikeTheme.colors.appBarIcon
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sort),
                        color = NikeTheme.colors.textPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = stringResource(sortTitle),
                        color = NikeTheme.colors.textSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            VerticalDivider(
                modifier = Modifier.height(headerHeight),
                color = NikeTheme.colors.divider
            )
            Box(
                modifier = Modifier
                    .size(headerHeight)
                    .clickable { selectLayout() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLinearLayout) Icons.Outlined.Square else Icons.Default.GridView,
                    contentDescription = stringResource(R.string.grid),
                    tint = NikeTheme.colors.appBarIcon
                )
            }
        }
        HorizontalDivider(color = NikeTheme.colors.divider)
    }
}

@Composable
fun GridProductList(
    products: List<Product>,
    navigateToProductDetail: (Product) -> Unit,
    onFavoriteChange: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(products) { product ->
            ProductItem(
                product = product,
                onClick = { navigateToProductDetail(product) },
                onFavoriteChange = { onFavoriteChange(product) }
            )
        }
    }
}

@Composable
fun LinearProductList(
    products: List<Product>,
    navigateToProductDetail: (Product) -> Unit,
    onFavoriteChange: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(products) { product ->
            ProductItemLarge(
                product = product,
                onClick = { navigateToProductDetail(product) },
                onFavoriteChange = { onFavoriteChange(product) },
            )
        }
    }
}

@Composable
fun ProductItemLarge(
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
            .fillMaxWidth()
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
        colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .aspectRatio(1.3f),
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
                    .padding(14.dp)
                    .size(44.dp)
                    .align(Alignment.TopStart),
                colors = IconButtonDefaults.iconToggleButtonColors(
                    containerColor = Color.LightGray,
                    checkedContainerColor = Color.LightGray
                )
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite),
                    modifier = Modifier.size(26.dp),
                    tint = Color.Black
                )
            }
        }
        Text(
            text = product.title,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = NikeTheme.colors.textPrimary
        )
        Text(
            text = buildAnnotatedString {
                append(NumberFormat.getNumberInstance().format(product.previousPrice))
                withStyle(SpanStyle(fontSize = 10.sp)) {
                    append("  " + stringResource(R.string.price_format))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            textDecoration = TextDecoration.LineThrough,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = NikeTheme.colors.textSecondary
        )
        Text(
            text = buildAnnotatedString {
                append(NumberFormat.getNumberInstance().format(product.price))
                withStyle(SpanStyle(fontSize = 12.sp)) {
                    append("  " + stringResource(R.string.price_format))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = NikeTheme.colors.textPrimary
        )
    }
}

@Composable
fun SortDialog(
    onDismissRequest: () -> Unit,
    selectSort: (Int) -> Unit,
    sort: Int,
    sortTitles: Array<Int>,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = NikeTheme.colors.appBar,
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(sortTitles) { index, sortTitle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectSort(index)
                                onDismissRequest()
                            }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (sort == index),
                            onClick = {
                                selectSort(index)
                                onDismissRequest()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NikeTheme.colors.radioButtonSelected,
                                unselectedColor = NikeTheme.colors.radioButtonUnselected
                            )
                        )
                        Text(
                            text = stringResource(sortTitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = NikeTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}