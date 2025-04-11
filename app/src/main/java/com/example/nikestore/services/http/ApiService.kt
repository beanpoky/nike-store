package com.example.nikestore.services.http

import com.example.nikestore.data.AddToCartRequest
import com.example.nikestore.data.AddToCartResponse
import com.example.nikestore.data.Banner
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.CartResponse
import com.example.nikestore.data.ChangeCountCartRequest
import com.example.nikestore.data.Checkout
import com.example.nikestore.data.Comment
import com.example.nikestore.data.MessageResponse
import com.example.nikestore.data.Product
import com.example.nikestore.data.RefreshRequest
import com.example.nikestore.data.RegisterRequest
import com.example.nikestore.data.RemoveFromCartRequest
import com.example.nikestore.data.SubmitOrderRequest
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.TokenRequest
import com.example.nikestore.data.TokenResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("product/list")
    suspend fun getProducts(@Query("sort") sort: Int): List<Product>

    @GET("banner/slider")
    suspend fun getBanners(): List<Banner>

    @GET("comment/list")
    suspend fun getComments(@Query("product_id") productId: Int): List<Comment>

    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>

    @POST("cart/remove")
    suspend fun removeItemFromCart(@Body request: RemoveFromCartRequest): Response<MessageResponse>

    @GET("cart/list")
    suspend fun getCart(): CartResponse

    @POST("cart/changeCount")
    suspend fun changeCount(@Body request: ChangeCountCartRequest): Response<AddToCartResponse>

    @GET("cart/count")
    suspend fun getCartItemsCount(): CartItemCount

    @POST("auth/token")
    suspend fun login(@Body request: TokenRequest): Response<TokenResponse>

    @POST("user/register")
    suspend fun signUp(@Body request: RegisterRequest): Response<MessageResponse>

    @POST("auth/token")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<TokenResponse>

    @POST("order/submit")
    suspend fun submitOrder(@Body request: SubmitOrderRequest): Response<SubmitOrderResult>

    @GET("order/checkout")
    suspend fun checkout(@Query("order_id") orderId: Int): Checkout
}

fun createApiServiceInstance(): ApiService {
    val json = Json { ignoreUnknownKeys = true }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val oldRequest = it.request()
            val newRequestBuilder = oldRequest.newBuilder()
            if (TokenContainer.token != null)
                newRequestBuilder.addHeader("Authorization", "Bearer ${TokenContainer.token}")
            newRequestBuilder.method(oldRequest.method, oldRequest.body)
            return@addInterceptor it.proceed(newRequestBuilder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .build()

    return Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl("http://exmaple.com/")
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)
}