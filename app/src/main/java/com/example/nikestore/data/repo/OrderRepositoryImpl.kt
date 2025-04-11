package com.example.nikestore.data.repo

import com.example.nikestore.data.Checkout
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.data.repo.source.OrderDataSource
import retrofit2.Response

class OrderRepositoryImpl(private val orderRemoteDataSource: OrderDataSource) : OrderRepository {

    override suspend fun submit(
        firstName: String,
        lastName: String,
        postalCode: Int,
        phoneNumber: Int,
        address: String,
        paymentMethod: String
    ): Response<SubmitOrderResult> = orderRemoteDataSource.submit(
        firstName = firstName,
        lastName = lastName,
        postalCode = postalCode,
        phoneNumber = phoneNumber,
        address = address,
        paymentMethod = paymentMethod
    )

    override suspend fun checkout(orderId: Int): Checkout = orderRemoteDataSource.checkout(orderId)
}