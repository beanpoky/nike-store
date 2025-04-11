package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Checkout
import com.example.nikestore.data.SubmitOrderRequest
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.services.http.ApiService
import retrofit2.Response

class OrderRemoteDataSource(private val apiService: ApiService) : OrderDataSource {

    override suspend fun submit(
        firstName: String,
        lastName: String,
        postalCode: Int,
        phoneNumber: Int,
        address: String,
        paymentMethod: String
    ): Response<SubmitOrderResult> = apiService.submitOrder(
        SubmitOrderRequest(
            firstName = firstName,
            lastName = lastName,
            postalCode = postalCode,
            phoneNumber = phoneNumber,
            address = address,
            paymentMethod = paymentMethod
        )
    )

    override suspend fun checkout(orderId: Int): Checkout = apiService.checkout(orderId)
}