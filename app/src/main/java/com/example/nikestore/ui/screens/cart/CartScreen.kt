package com.example.nikestore.ui.screens.cart

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nikestore.R
import com.example.nikestore.data.CartItem
import com.example.nikestore.data.Product
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.ui.screens.common.EmptyState
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

@Composable
fun CartScreen(
    navigateToProductDetail: (Product) -> Unit,
    navigateToLogin: () -> Unit,
    navigationToShipping: (PurchaseDetail) -> Unit,
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel = koinViewModel()
) {
    val cartUiState = cartViewModel.networkDataUiState
    val cartActionUiState = cartViewModel.networkActionUiState
    val isActiveRemoveAction = cartViewModel.isActiveRemoveAction
    val selectedItemId = cartViewModel.selectedItemId
    val emptyState = cartViewModel.emptyState

    val context = LocalContext.current
    HandleNetworkState(
        networkActionUiState = cartActionUiState,
        onStateHandled = cartViewModel::resetNetworkState,
        onSuccessHandled = {
            if (isActiveRemoveAction)
                showToast(context, context.getString(R.string.item_removed_from_cart))
            mainViewModel.getCartItemCount()
        }
    )

    if (emptyState.mustShow)
        EmptyState(emptyState, navigateToLogin)
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            when (cartUiState) {
                is NetworkDataUiState.Loading -> LoadingScreen()
                is NetworkDataUiState.Error -> ErrorScreen(retryAction = cartViewModel::getCartData)
                is NetworkDataUiState.Success -> {
                    val data = cartUiState.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(data.cartItems) { cartItem ->
                            CartItem(
                                cartItem = cartItem,
                                increaseItemCount = { cartViewModel.increaseCartItemCount(cartItem) },
                                decreaseItemCount = { cartViewModel.decreaseCartItemCount(cartItem) },
                                removeItemFromCartAction = {
                                    cartViewModel.removeItemFromCart(cartItem)
                                },
                                navigateToProductDetail = { navigateToProductDetail(cartItem.product) },
                                cartUiState = cartActionUiState,
                                isActiveRemoveAction = isActiveRemoveAction,
                                selectedItemId = selectedItemId,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                        item {
                            PurchaseDetailCard(
                                totalPrice = data.purchaseDetail.totalPrice,
                                shippingCost = data.purchaseDetail.shippingCost,
                                payablePrice = data.purchaseDetail.payablePrice,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    }
                    PayButton(
                        payAction = { navigationToShipping(data.purchaseDetail) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CartItem(
    cartItem: CartItem,
    increaseItemCount: () -> Unit,
    decreaseItemCount: () -> Unit,
    removeItemFromCartAction: () -> Unit,
    navigateToProductDetail: () -> Unit,
    cartUiState: NetworkActionUiState<*>,
    isActiveRemoveAction: Boolean,
    selectedItemId: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { navigateToProductDetail() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 18.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(cartItem.product.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = cartItem.product.title,
                        contentScale = ContentScale.Crop,
                        error = { ErrorImageScreen() },
                        loading = { LoadingImageScreen() }
                    )
                }
                Text(
                    text = cartItem.product.title,
                    modifier = Modifier
                        .padding(start = 18.dp)
                        .weight(1f),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NikeTheme.colors.textPrimary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.count),
                    modifier = Modifier.width(90.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = NikeTheme.colors.textTertiary
                )
                Text(
                    text = buildAnnotatedString {
                        append(
                            NumberFormat.getNumberInstance().format(cartItem.product.previousPrice)
                        )
                        withStyle(SpanStyle(fontSize = 9.sp)) {
                            append("  " + stringResource(R.string.price_format))
                        }
                    },
                    textDecoration = TextDecoration.LineThrough,
                    style = MaterialTheme.typography.labelSmall,
                    color = NikeTheme.colors.textSecondary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedIconButton(
                        onClick = { if (cartUiState != NetworkActionUiState.Loading) increaseItemCount() },
                        modifier = Modifier.size(25.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.5.dp, NikeTheme.colors.textPrimary),
                        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = NikeTheme.colors.textPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (cartUiState == NetworkActionUiState.Loading && !isActiveRemoveAction && selectedItemId == cartItem.cartItemId) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(20.dp),
                            strokeWidth = 3.dp,
                            color = NikeTheme.colors.textPrimary
                        )
                    } else {
                        Text(
                            text = cartItem.count.toString(),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(30.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = NikeTheme.colors.textPrimary
                        )
                    }
                    OutlinedIconButton(
                        onClick = { if (cartUiState != NetworkActionUiState.Loading) decreaseItemCount() },
                        modifier = Modifier.size(25.dp),
                        shape = RoundedCornerShape(6.dp),
                        enabled = cartItem.count > 1,
                        border = BorderStroke(
                            1.5.dp,
                            if (cartItem.count > 1) NikeTheme.colors.textPrimary else Color.Transparent
                        ),
                        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = NikeTheme.colors.textPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.remove),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = buildAnnotatedString {
                        append(NumberFormat.getNumberInstance().format(cartItem.product.price))
                        withStyle(SpanStyle(fontSize = 10.sp)) {
                            append("  " + stringResource(R.string.price_format))
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = NikeTheme.colors.textPrimary
                )
            }
            HorizontalDivider(color = NikeTheme.colors.divider)
            TextButton(
                onClick = { if (cartUiState != NetworkActionUiState.Loading) removeItemFromCartAction() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = NikeTheme.colors.textButtonContent)
            ) {
                if (cartUiState == NetworkActionUiState.Loading && isActiveRemoveAction && selectedItemId == cartItem.cartItemId) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = NikeTheme.colors.textButtonContent
                    )
                } else {
                    Text(
                        text = stringResource(R.string.remove_from_cart),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseDetailCard(
    totalPrice: Int,
    shippingCost: Int,
    payablePrice: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.purchase_detail),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = NikeTheme.colors.textSecondary
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                PurchaseDetailRow(
                    titleRes = R.string.total_price,
                    price = totalPrice
                )
                HorizontalDivider(color = NikeTheme.colors.divider)
                PurchaseDetailRow(
                    titleRes = R.string.shipping_cost,
                    price = shippingCost
                )
                HorizontalDivider(color = NikeTheme.colors.divider)
                PurchaseDetailRow(
                    titleRes = R.string.payable_price,
                    price = payablePrice
                )
            }
        }
    }
}

@Composable
fun PurchaseDetailRow(
    @StringRes titleRes: Int,
    price: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = NikeTheme.colors.textPrimary
        )
        Text(
            text = buildAnnotatedString {
                append(NumberFormat.getNumberInstance().format(price))
                withStyle(SpanStyle(fontSize = 12.sp)) {
                    append("  " + stringResource(R.string.price_format))
                }
            },
            style = MaterialTheme.typography.labelLarge,
            color = NikeTheme.colors.textPrimary
        )
    }
}

@Composable
fun PayButton(
    payAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = payAction,
        containerColor = NikeTheme.colors.actionButton,
        contentColor = NikeTheme.colors.actionButtonContent,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.pay),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}