package com.example.nikestore.data.repo

import com.example.nikestore.data.Checkout
import com.example.nikestore.data.SubmitOrderResult
import retrofit2.Response

interface OrderRepository {

    suspend fun submit(
        firstName: String,
        lastName: String,
        postalCode: Int,
        phoneNumber: Int,
        address: String,
        paymentMethod: String
    ): Response<SubmitOrderResult>

    suspend fun checkout(orderId: Int): Checkout
}