package com.example.nikestore.ui.screens.product.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nikestore.R
import com.example.nikestore.common.formatDate
import com.example.nikestore.data.Comment
import com.example.nikestore.data.Product
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.ErrorImageScreen
import com.example.nikestore.ui.screens.common.ErrorScreen
import com.example.nikestore.ui.screens.common.HandleNetworkState
import com.example.nikestore.ui.screens.common.LoadingImageScreen
import com.example.nikestore.ui.screens.common.LoadingScreen
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.screens.common.showToast
import com.example.nikestore.ui.screens.main.MainViewModel
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

object ProductDetailDestination : NavigationDestination {
    override val route = "PRODUCT_DETAIL"
    override val titleRes = R.string.home
    const val PRODUCT_ARG = "product"
    val routeWithArg = "$route/{$PRODUCT_ARG}"
}

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    navigateToCommentList: (Int) -> Unit,
    navigateToLogin: () -> Unit,
    mainViewModel: MainViewModel,
    productDetailViewModel: ProductDetailViewModel = koinViewModel()
) {
    val product = productDetailViewModel.product
    val commentsUiState = productDetailViewModel.networkDataUiState
    val cartUiState = productDetailViewModel.networkActionUiState

    val scrollState = rememberScrollState()
    val imageHeight by remember { derivedStateOf { 360.dp - (scrollState.value / 2).dp } }
    val progress by remember { derivedStateOf { (scrollState.value / 280f).coerceIn(0f, 1f) } }
    val topAppBarBackground = lerp(Color.Transparent, NikeTheme.colors.appBar, progress)
    val iconColor = lerp(Color.Black, NikeTheme.colors.appBarIcon, progress)
    val titleColor = lerp(Color.Transparent, NikeTheme.colors.appBarTitle, progress)

    val context = LocalContext.current
    HandleNetworkState(
        networkActionUiState = cartUiState,
        onStateHandled = productDetailViewModel::resetNetworkState,
        onAuthRequired = navigateToLogin,
        onSuccessHandled = {
            showToast(context, context.getString(R.string.product_has_been_added_to_cart))
            mainViewModel.getCartItemCount()
        }
    )

    Box(
        modifier = Modifier
            .background(NikeTheme.colors.background)
            .padding(
                bottom = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = WindowInsets.safeDrawing
                        .asPaddingValues()
                        .calculateTopPadding()
                )
                .verticalScroll(scrollState)
        ) {
            ProductImage(
                product = product,
                modifier = Modifier.height(imageHeight)
            )
            ProductInfo(product)
            when (commentsUiState) {
                is NetworkDataUiState.Loading -> LoadingScreen(Modifier.padding(24.dp))
                is NetworkDataUiState.Error -> {
                    ErrorScreen(
                        retryAction = productDetailViewModel::getComments,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                is NetworkDataUiState.Success -> {
                    CommentsHeader(
                        postCommentAction = {},
                        modifier = Modifier
                            .padding(top = 24.dp)
                    )
                    CommentList(
                        comments = commentsUiState.data.take(5)
                    )
                    ViewAllCommentsButton(
                        viewAllCommentsAction = { navigateToCommentList(product.id) },
                        modifier = Modifier.padding(top = 16.dp, bottom = 100.dp)
                    )
                }
            }
        }

        ProductTopAppBar(
            backgroundColor = topAppBarBackground,
            iconColor = iconColor,
            titleColor = titleColor,
            product = product,
            onBack = onBack,
            productDetailViewModel = productDetailViewModel
        )

        AddToCartButton(
            addToCartAction = { productDetailViewModel.addToCart() },
            cartUiState = cartUiState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}

@Composable
fun ProductTopAppBar(
    backgroundColor: Color,
    iconColor: Color,
    titleColor: Color,
    product: Product,
    onBack: () -> Unit,
    productDetailViewModel: ProductDetailViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(
                top = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = iconColor
            )
        }
        Text(
            text = product.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = titleColor
        )
        IconToggleButton(
            checked = productDetailViewModel.isFavorite,
            onCheckedChange = { productDetailViewModel.addToFavorites(product) },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = if (productDetailViewModel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.favorite),
                tint = iconColor
            )
        }
    }
}

@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
}

@Composable
fun ProductInfo(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 12.dp)
        ) {
            Text(
                text = product.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textDirection = TextDirection.Content,
                    textAlign = TextAlign.Justify,
                    fontSize = 18.sp
                ),
                color = NikeTheme.colors.textPrimary
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = buildAnnotatedString {
                        append(NumberFormat.getNumberInstance().format(product.previousPrice))
                        withStyle(SpanStyle(fontSize = 12.sp)) {
                            append("  " + stringResource(R.string.price_format))
                        }
                    },
                    textDecoration = TextDecoration.LineThrough,
                    style = MaterialTheme.typography.labelLarge,
                    color = NikeTheme.colors.textSecondary
                )
                Text(
                    text = buildAnnotatedString {
                        append(NumberFormat.getNumberInstance().format(product.price))
                        withStyle(SpanStyle(fontSize = 14.sp)) {
                            append("  " + stringResource(R.string.price_format))
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = NikeTheme.colors.textPrimary
                )
            }
        }
        Text(
            text = stringResource(R.string.product_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Content,
                textAlign = TextAlign.Justify
            ),
            color = NikeTheme.colors.textPrimary
        )
    }
}

@Composable
fun CommentsHeader(
    postCommentAction: () -> Unit,
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
            text = stringResource(R.string.user_comments),
            color = NikeTheme.colors.textSecondary,
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = postCommentAction,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.textButtonColors(contentColor = NikeTheme.colors.textButtonContent)
        ) {
            Text(
                text = stringResource(R.string.post_comment),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun CommentList(
    comments: List<Comment>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        for (comment in comments) {
            CommentItem(comment)
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, NikeTheme.colors.divider, RoundedCornerShape(0.dp))
            .padding(14.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    color = NikeTheme.colors.textPrimary,
                )
                Text(
                    text = formatDate(comment.date)!!,
                    color = NikeTheme.colors.textTertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = comment.author.email,
                color = NikeTheme.colors.textSecondary,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = comment.content,
                modifier = Modifier.padding(top = 8.dp),
                color = NikeTheme.colors.textPrimary,
                style = MaterialTheme.typography.labelLarge.copy(
                    textDirection = TextDirection.Content,
                    textAlign = TextAlign.Justify
                )
            )
        }
    }
}

@Composable
fun ViewAllCommentsButton(
    viewAllCommentsAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = viewAllCommentsAction,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(1.dp, NikeTheme.colors.textButtonContent),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NikeTheme.colors.textButtonContent)
    ) {
        Text(
            text = stringResource(R.string.view_all_comments),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AddToCartButton(
    addToCartAction: () -> Unit,
    cartUiState: NetworkActionUiState<*>,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = { if (cartUiState != NetworkActionUiState.Loading) addToCartAction() },
        containerColor = NikeTheme.colors.actionButton,
        contentColor = NikeTheme.colors.actionButtonContent,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        if (cartUiState == NetworkActionUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(26.dp),
                color = NikeTheme.colors.buttonContent
            )
        } else {
            Text(
                text = stringResource(R.string.add_to_cart),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}