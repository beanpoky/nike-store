package com.example.nikestore.ui.screens.checkout

import androidx.lifecycle.SavedStateHandle
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.Checkout
import com.example.nikestore.data.repo.OrderRepository

class CheckoutViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : BaseViewModel<Checkout, Unit>() {
    private val orderIdArg: Int = checkNotNull(savedStateHandle[CheckoutDestination.ORDER_ID_ARG])

    init {
        getCheckout()
    }

    fun getCheckout() {
        safeLaunchData {
            orderRepository.checkout(orderIdArg)
        }
    }
}