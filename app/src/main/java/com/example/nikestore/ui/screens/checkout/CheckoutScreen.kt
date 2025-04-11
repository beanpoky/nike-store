package com.example.nikestore.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nikestore.R
import com.example.nikestore.data.Checkout
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.ErrorScreen
import com.example.nikestore.ui.screens.common.LoadingScreen
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.screens.common.NikeTopAppBar
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

object CheckoutDestination : NavigationDestination {
    override val route = "CHECKOUT"
    override val titleRes = R.string.payment_receipt
    const val ORDER_ID_ARG = "order_id"
    val routeWithArg = "$route/{$ORDER_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    checkoutViewModel: CheckoutViewModel = koinViewModel()
) {
    val checkoutUiState = checkoutViewModel.networkDataUiState

    Scaffold(
        topBar = {
            NikeTopAppBar(
                title = stringResource(CheckoutDestination.titleRes),
                onBack = onBack
            )
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (checkoutUiState) {
                is NetworkDataUiState.Loading -> LoadingScreen()
                is NetworkDataUiState.Error -> ErrorScreen(retryAction = checkoutViewModel::getCheckout)
                is NetworkDataUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PaymentReceiptCard(
                            checkout = checkoutUiState.data,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        CheckoutActionButtons(
                            returnToHomeAction = onBack,
                            orderHistoryAction = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentReceiptCard(
    checkout: Checkout,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = NikeTheme.colors.backgroundOverlay)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (checkout.purchaseSuccess)
                    stringResource(R.string.purchase_completed_successfully)
                else
                    stringResource(R.string.purchase_failed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NikeTheme.colors.button,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.order_status),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NikeTheme.colors.textPrimary
                )
                Text(
                    text = checkout.paymentStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = NikeTheme.colors.textPrimary
                )
            }
            HorizontalDivider(color = NikeTheme.colors.divider)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.amount),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NikeTheme.colors.textPrimary
                )
                Text(
                    text = buildAnnotatedString {
                        append(NumberFormat.getNumberInstance().format(checkout.payablePrice))
                        withStyle(SpanStyle(fontSize = 12.sp)) {
                            append("  " + stringResource(R.string.price_format))
                        }
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = NikeTheme.colors.textPrimary
                )
            }
        }
    }
}

@Composable
fun CheckoutActionButtons(
    returnToHomeAction: () -> Unit,
    orderHistoryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Button(
            onClick = returnToHomeAction,
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NikeTheme.colors.button,
                contentColor = NikeTheme.colors.buttonContent
            ),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = stringResource(R.string.return_to_home),
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(
            onClick = orderHistoryAction,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NikeTheme.colors.button),
            border = BorderStroke(1.dp, NikeTheme.colors.buttonContent),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = stringResource(R.string.order_history),
                fontWeight = FontWeight.Bold
            )
        }
    }
}