package com.example.nikestore.ui.screens.product.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nikestore.R
import com.example.nikestore.common.BaseViewModel
import com.example.nikestore.data.Product
import com.example.nikestore.data.repo.ProductRepository
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListUiState(
    val sort: Int,
    val isLinearLayout: Boolean = false,
    val isShowSortDialog: Boolean = false
)

class ProductListViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : BaseViewModel<List<Product>, Unit>() {
    private val sortArg: Int = checkNotNull(savedStateHandle[ProductListDestination.SORT_ARG])

    private val _productListUiState = MutableStateFlow(ProductListUiState(sort = sortArg))
    val productListUiState: StateFlow<ProductListUiState> = _productListUiState.asStateFlow()

    val sortTitles =
        arrayOf(R.string.latest, R.string.most_popular, R.string.most_expensive, R.string.cheapest)

    init {
        getProducts()
    }

    fun getProducts() {
        safeLaunchData {
            productRepository.getProducts(_productListUiState.value.sort)
        }
    }

    fun selectLayout(isLinearLayout: Boolean) {
        _productListUiState.update { currentState ->
            currentState.copy(isLinearLayout = isLinearLayout)
        }
    }

    fun selectSort(sort: Int) {
        _productListUiState.update { currentState ->
            currentState.copy(sort = sort)
        }
        getProducts()
    }

    fun showSortDialog(isShowSortDialog: Boolean) {
        _productListUiState.update { currentState ->
            currentState.copy(isShowSortDialog = isShowSortDialog)
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            if (product.isFavorite) {
                productRepository.deleteFromFavorites(product)
            } else {
                productRepository.addToFavorites(product)
            }

            if (networkDataUiState is NetworkDataUiState.Success) {
                val data = (networkDataUiState as NetworkDataUiState.Success<List<Product>>).data
                val updatedProducts = data.map {
                    if (it.id == product.id) it.apply { isFavorite = !product.isFavorite } else it
                }
                updateDataState(
                    NetworkDataUiState.Success(
                        data = updatedProducts
                    )
                )
            }
        }
    }
}