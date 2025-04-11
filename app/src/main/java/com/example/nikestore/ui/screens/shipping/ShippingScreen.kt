package com.example.nikestore.ui.screens.shipping

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nikestore.R
import com.example.nikestore.common.openUrlInCustomTab
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.cart.PurchaseDetailCard
import com.example.nikestore.ui.screens.common.HandleNetworkState
import com.example.nikestore.ui.screens.common.NetworkActionUiState
import com.example.nikestore.ui.screens.common.NikeTopAppBar
import com.example.nikestore.ui.screens.common.showToast
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel

object ShippingDestination : NavigationDestination {
    override val route = "SHIPPING"
    override val titleRes = R.string.select_recipient_and_payment_method
    const val PURCHASE_DETAIL_ARG = "purchase_detail"
    val routeWithArg = "$route/{$PURCHASE_DETAIL_ARG}"
}

const val PAYMENT_METHOD_COD = "cash_on_delivery"
const val PAYMENT_METHOD_ONLINE = "online"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingScreen(
    onBack: () -> Unit,
    navigationToCheckout: (Int) -> Unit,
    shippingViewModel: ShippingViewModel = koinViewModel()
) {
    val purchaseDetail = shippingViewModel.purchaseDetail
    val shippingData by shippingViewModel.shippingData.collectAsStateWithLifecycle()
    val shippingActionUiState = shippingViewModel.networkActionUiState
    val isOnlineAction = shippingViewModel.isOnlineAction

    val context = LocalContext.current
    HandleNetworkState(
        networkActionUiState = shippingActionUiState,
        onStateHandled = shippingViewModel::resetNetworkState,
        onSuccessHandled = {
            if (shippingActionUiState is NetworkActionUiState.Success) {
                if (shippingActionUiState.data.bankGatewayUrl.isNotEmpty()) {
                    openUrlInCustomTab(context, shippingActionUiState.data.bankGatewayUrl)
                } else {
                    navigationToCheckout(shippingActionUiState.data.orderId)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            NikeTopAppBar(
                title = stringResource(ShippingDestination.titleRes),
                onBack = onBack
            )
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ShippingTextField(
                value = shippingData.firstName,
                onValueChange = { shippingViewModel.updateFirstName(it) },
                labelTextRes = R.string.first_name,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.padding(top = 6.dp)
            )
            ShippingTextField(
                value = shippingData.lastName,
                onValueChange = { shippingViewModel.updateLastName(it) },
                labelTextRes = R.string.last_name,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.padding(top = 6.dp)
            )
            ShippingTextField(
                value = shippingData.postalCode,
                onValueChange = { shippingViewModel.updatePostalCode(it) },
                labelTextRes = R.string.postal_code,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.padding(top = 6.dp)
            )
            ShippingTextField(
                value = shippingData.phoneNumber,
                onValueChange = { shippingViewModel.updatePhoneNumber(it) },
                labelTextRes = R.string.phone_number,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.padding(top = 6.dp)
            )
            ShippingTextField(
                value = shippingData.address,
                onValueChange = { shippingViewModel.updateAddress(it) },
                labelTextRes = R.string.address,
                keyboardType = KeyboardType.Text,
                isImeActionDone = true,
                modifier = Modifier.padding(top = 6.dp)
            )
            PurchaseDetailCard(
                totalPrice = purchaseDetail.totalPrice,
                shippingCost = purchaseDetail.shippingCost,
                payablePrice = purchaseDetail.payablePrice,
                modifier = Modifier.padding(top = 24.dp)
            )
            ShippingActionButtons(
                cashOnDeliveryAction = {
                    if (shippingData.firstName.isNotBlank() && shippingData.lastName.isNotBlank()
                        && shippingData.postalCode.isNotBlank() && shippingData.phoneNumber.isNotBlank()
                        && shippingData.address.isNotBlank()
                    ) {
                        shippingViewModel.submitOrder(
                            firstName = shippingData.firstName,
                            lastName = shippingData.lastName,
                            postalCode = shippingData.postalCode,
                            phoneNumber = shippingData.phoneNumber,
                            address = shippingData.address,
                            paymentMethod = PAYMENT_METHOD_COD
                        )
                    } else {
                        showToast(context, context.getString(R.string.fields_must_not_be_empty))
                    }
                },
                onlinePaymentAction = {
                    if (shippingData.firstName.isNotBlank() && shippingData.lastName.isNotBlank()
                        && shippingData.postalCode.isNotBlank() && shippingData.phoneNumber.isNotBlank()
                        && shippingData.address.isNotBlank()
                    ) {
                        shippingViewModel.submitOrder(
                            firstName = shippingData.firstName,
                            lastName = shippingData.lastName,
                            postalCode = shippingData.postalCode,
                            phoneNumber = shippingData.phoneNumber,
                            address = shippingData.address,
                            paymentMethod = PAYMENT_METHOD_ONLINE
                        )
                    } else {
                        showToast(context, context.getString(R.string.fields_must_not_be_empty))
                    }
                },
                shippingActionUiState = shippingActionUiState,
                isOnlineAction = isOnlineAction,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun ShippingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelTextRes: Int,
    keyboardType: KeyboardType,
    isImeActionDone: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        textStyle = MaterialTheme.typography.labelLarge,
        label = {
            Text(
                text = stringResource(labelTextRes),
                style = MaterialTheme.typography.labelLarge
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = if (isImeActionDone) ImeAction.Done else ImeAction.Next
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
}

@Composable
fun ShippingActionButtons(
    cashOnDeliveryAction: () -> Unit,
    onlinePaymentAction: () -> Unit,
    shippingActionUiState: NetworkActionUiState<*>,
    isOnlineAction: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        OutlinedButton(
            onClick = { if (shippingActionUiState != NetworkActionUiState.Loading) cashOnDeliveryAction() },
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NikeTheme.colors.button),
            border = BorderStroke(1.dp, NikeTheme.colors.buttonContent),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            if (shippingActionUiState == NetworkActionUiState.Loading && !isOnlineAction) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    strokeWidth = 2.dp,
                    color = NikeTheme.colors.buttonContent
                )
            } else {
                Text(
                    text = stringResource(R.string.cash_on_delivery),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Button(
            onClick = { if (shippingActionUiState != NetworkActionUiState.Loading) onlinePaymentAction() },
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NikeTheme.colors.button,
                contentColor = NikeTheme.colors.buttonContent
            ),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            if (shippingActionUiState == NetworkActionUiState.Loading && isOnlineAction) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    strokeWidth = 2.dp,
                    color = NikeTheme.colors.buttonContent
                )
            } else {
                Text(
                    text = stringResource(R.string.online_payment),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}