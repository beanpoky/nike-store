package com.example.nikestore.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.Banner
import com.example.nikestore.data.Product
import com.example.nikestore.data.SORT_LATEST
import com.example.nikestore.data.SORT_POPULAR
import com.example.nikestore.data.repo.BannerRepository
import com.example.nikestore.data.repo.ProductRepository
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class HomeData(
    val latestProducts: List<Product>,
    val popularProducts: List<Product>,
    val banners: List<Banner>
)

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val bannerRepository: BannerRepository
) : BaseViewModel<HomeData, Unit>() {

    init {
        getHomeData()
    }

    fun getHomeData() {
        safeLaunchData {
            coroutineScope {
                val latestProducts = async { productRepository.getProducts(SORT_LATEST) }
                val popularProducts = async { productRepository.getProducts(SORT_POPULAR) }
                val banners = async { bannerRepository.getBanners() }
                HomeData(
                    latestProducts = latestProducts.await(),
                    popularProducts = popularProducts.await(),
                    banners = banners.await()
                )
            }
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            if (product.isFavorite)
                productRepository.deleteFromFavorites(product)
            else
                productRepository.addToFavorites(product)

            if (networkDataUiState is NetworkDataUiState.Success) {
                val data = (networkDataUiState as NetworkDataUiState.Success<HomeData>).data
                val updatedLatestProducts = data.latestProducts.map {
                    if (it.id == product.id) it.apply { isFavorite = !product.isFavorite } else it
                }
                val updatedPopularProducts = data.popularProducts.map {
                    if (it.id == product.id) it.apply { isFavorite = !product.isFavorite } else it
                }
                updateDataState(
                    NetworkDataUiState.Success(
                        HomeData(
                            latestProducts = updatedLatestProducts,
                            popularProducts = updatedPopularProducts,
                            banners = data.banners
                        )
                    )
                )
            }
        }
    }
}