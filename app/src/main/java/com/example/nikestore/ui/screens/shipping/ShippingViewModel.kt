package com.example.nikestore.ui.screens.shipping

import androidx.lifecycle.SavedStateHandle
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.data.repo.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

data class ShippingData(
    val firstName: String = "",
    val lastName: String = "",
    val postalCode: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

class ShippingViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : BaseViewModel<Unit, SubmitOrderResult>() {
    private val jsonPurchaseDetail: String =
        checkNotNull(savedStateHandle[ShippingDestination.PURCHASE_DETAIL_ARG])
    val purchaseDetail: PurchaseDetail = Json.decodeFromString(jsonPurchaseDetail)

    private val _shippingData = MutableStateFlow(ShippingData())
    val shippingData = _shippingData.asStateFlow()

    var isOnlineAction: Boolean = false
        private set

    fun updateFirstName(firstName: String) {
        _shippingData.update { currentState ->
            currentState.copy(firstName = firstName)
        }
    }

    fun updateLastName(lastName: String) {
        _shippingData.update { currentState ->
            currentState.copy(lastName = lastName)
        }
    }

    fun updatePostalCode(postalCode: String) {
        _shippingData.update { currentState ->
            currentState.copy(postalCode = postalCode)
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _shippingData.update { currentState ->
            currentState.copy(phoneNumber = phoneNumber)
        }
    }

    fun updateAddress(address: String) {
        _shippingData.update { currentState ->
            currentState.copy(address = address)
        }
    }

    fun submitOrder(
        firstName: String,
        lastName: String,
        postalCode: String,
        phoneNumber: String,
        address: String,
        paymentMethod: String
    ) {
        if (paymentMethod == PAYMENT_METHOD_ONLINE)
            isOnlineAction = true
        else if (paymentMethod == PAYMENT_METHOD_COD)
            isOnlineAction = false

        safeLaunchAction {
            orderRepository.submit(
                firstName = firstName,
                lastName = lastName,
                postalCode = postalCode.toInt(),
                phoneNumber = phoneNumber.toInt(),
                address = address,
                paymentMethod = paymentMethod
            )
        }
    }
}