package com.example.nikestore.ui.screens.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nikestore.R
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.CartItem
import com.example.nikestore.data.EmptyState
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.repo.CartRepository
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import retrofit2.Response

data class CartData(
    val cartItems: List<CartItem>,
    val purchaseDetail: PurchaseDetail
)

class CartViewModel(private val cartRepository: CartRepository) : BaseViewModel<CartData, Any>() {
    var isActiveRemoveAction: Boolean = false
        private set
    var selectedItemId: Int = 0
        private set
    var emptyState: EmptyState by mutableStateOf(EmptyState(false))
        private set

    init {
        getCartData()
    }

    fun getCartData() {
        if (!TokenContainer.token.isNullOrEmpty()) {
            safeLaunchData {
                val response = cartRepository.get()
                if (response.cartItems.isEmpty())
                    emptyState =
                        EmptyState(mustShow = true, messageResId = R.string.theres_nothing_to_show)
                CartData(
                    cartItems = response.cartItems,
                    purchaseDetail = PurchaseDetail(
                        response.totalPrice,
                        response.shippingCost,
                        response.payablePrice
                    )
                )
            }
        } else emptyState = EmptyState(
            mustShow = true,
            messageResId = R.string.login_to_your_account,
            mustShowCallToActionButton = true
        )
    }

    fun removeItemFromCart(cartItem: CartItem) {
        isActiveRemoveAction = true
        selectedItemId = cartItem.cartItemId

        safeLaunchAction(
            onSuccess = { updatePurchaseDetail(cartItem.cartItemId, remove = true) }
        ) {
            val response = cartRepository.remove(cartItem.cartItemId)
            response as Response<Any>
        }
    }

    fun increaseCartItemCount(cartItem: CartItem) {
        isActiveRemoveAction = false
        selectedItemId = cartItem.cartItemId

        safeLaunchAction(
            onSuccess = { updatePurchaseDetail(cartItem.cartItemId, cartItem.count + 1) }
        ) {
            val response = cartRepository.changeCount(cartItem.cartItemId, cartItem.count + 1)
            response as Response<Any>
        }
    }

    fun decreaseCartItemCount(cartItem: CartItem) {
        isActiveRemoveAction = false
        selectedItemId = cartItem.cartItemId

        safeLaunchAction(
            onSuccess = { updatePurchaseDetail(cartItem.cartItemId, cartItem.count - 1) }
        ) {
            val response = cartRepository.changeCount(cartItem.cartItemId, cartItem.count - 1)
            response as Response<Any>
        }
    }

    private fun updatePurchaseDetail(
        cartItemId: Int,
        newCount: Int? = null,
        remove: Boolean = false
    ) {
        if (networkDataUiState is NetworkDataUiState.Success) {
            val data = (networkDataUiState as NetworkDataUiState.Success<CartData>).data
            val updatedCartItems = if (remove) {
                data.cartItems.filter { it.cartItemId != cartItemId }
            } else {
                data.cartItems.map {
                    if (it.cartItemId == cartItemId && newCount != null) it.copy(count = newCount) else it
                }
            }
            val payablePrice = updatedCartItems.sumOf { it.product.price * it.count }
            val totalPrice = payablePrice + data.purchaseDetail.shippingCost

            if (updatedCartItems.isEmpty())
                emptyState =
                    EmptyState(mustShow = true, messageResId = R.string.theres_nothing_to_show)

            updateDataState(
                NetworkDataUiState.Success(
                    CartData(
                        updatedCartItems,
                        PurchaseDetail(totalPrice, data.purchaseDetail.shippingCost, payablePrice)
                    )
                )
            )
        }
    }
}