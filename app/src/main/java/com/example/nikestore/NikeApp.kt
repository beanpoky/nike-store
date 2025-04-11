package com.example.nikestore

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.example.nikestore.data.db.AppDatabase
import com.example.nikestore.data.repo.BannerRepository
import com.example.nikestore.data.repo.BannerRepositoryImpl
import com.example.nikestore.data.repo.CartRepository
import com.example.nikestore.data.repo.CartRepositoryImpl
import com.example.nikestore.data.repo.CommentRepository
import com.example.nikestore.data.repo.CommentRepositoryImpl
import com.example.nikestore.data.repo.OrderRepository
import com.example.nikestore.data.repo.OrderRepositoryImpl
import com.example.nikestore.data.repo.ProductRepository
import com.example.nikestore.data.repo.ProductRepositoryImpl
import com.example.nikestore.data.repo.UserRepository
import com.example.nikestore.data.repo.UserRepositoryImpl
import com.example.nikestore.data.repo.source.BannerRemoteDataSource
import com.example.nikestore.data.repo.source.CartRemoteDataSource
import com.example.nikestore.data.repo.source.CommentRemoteDataSource
import com.example.nikestore.data.repo.source.OrderRemoteDataSource
import com.example.nikestore.data.repo.source.ProductRemoteDataSource
import com.example.nikestore.data.repo.source.UserLocalDataSource
import com.example.nikestore.data.repo.source.UserRemoteDataSource
import com.example.nikestore.services.http.createApiServiceInstance
import com.example.nikestore.ui.screens.auth.AuthViewModel
import com.example.nikestore.ui.screens.cart.CartViewModel
import com.example.nikestore.ui.screens.checkout.CheckoutViewModel
import com.example.nikestore.ui.screens.comment.CommentListViewModel
import com.example.nikestore.ui.screens.favorites.FavoriteProductsViewModel
import com.example.nikestore.ui.screens.home.HomeViewModel
import com.example.nikestore.ui.screens.main.MainViewModel
import com.example.nikestore.ui.screens.product.detail.ProductDetailViewModel
import com.example.nikestore.ui.screens.product.list.ProductListViewModel
import com.example.nikestore.ui.screens.profile.ProfileViewModel
import com.example.nikestore.ui.screens.shipping.ShippingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class NikeApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val myModules = module {
            single { createApiServiceInstance() }
            single { PreferenceDataStoreFactory.create { get<Context>().preferencesDataStoreFile("user_settings") } }
            single { Room.databaseBuilder(this@NikeApp, AppDatabase::class.java, "db_app").build() }

            single { UserLocalDataSource(get()) }
            single<OrderRepository> { OrderRepositoryImpl(OrderRemoteDataSource(get())) }
            single<UserRepository> {
                UserRepositoryImpl(UserRemoteDataSource(get()), UserLocalDataSource(get()))
            }
            factory<ProductRepository> {
                ProductRepositoryImpl(
                    ProductRemoteDataSource(get()),
                    get<AppDatabase>().productDao()
                )
            }
            factory<BannerRepository> { BannerRepositoryImpl(BannerRemoteDataSource(get())) }
            factory<CommentRepository> { CommentRepositoryImpl(CommentRemoteDataSource(get())) }
            factory<CartRepository> { CartRepositoryImpl(CartRemoteDataSource(get())) }

            viewModel { HomeViewModel(get(), get()) }
            viewModel { (handle: SavedStateHandle) ->
                ProductDetailViewModel(handle, get(), get(), get())
            }
            viewModel { (handle: SavedStateHandle) -> CommentListViewModel(handle, get()) }
            viewModel { (handle: SavedStateHandle) -> ProductListViewModel(handle, get()) }
            viewModel { AuthViewModel(get()) }
            viewModel { CartViewModel(get()) }
            viewModel { MainViewModel(get()) }
            viewModel { (handle: SavedStateHandle) -> ShippingViewModel(handle, get()) }
            viewModel { (handle: SavedStateHandle) -> CheckoutViewModel(handle, get()) }
            viewModel { ProfileViewModel(get()) }
            viewModel { FavoriteProductsViewModel(get()) }
        }

        startKoin {
            androidContext(this@NikeApp)
            modules(myModules)
        }

        applicationScope.launch {
            val userRepository: UserRepository = get()
            userRepository.loadToken()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}