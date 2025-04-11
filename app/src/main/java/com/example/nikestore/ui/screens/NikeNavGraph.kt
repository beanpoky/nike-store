package com.example.nikestore.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nikestore.ui.screens.auth.LoginDestination
import com.example.nikestore.ui.screens.auth.LoginScreen
import com.example.nikestore.ui.screens.auth.SignUpDestination
import com.example.nikestore.ui.screens.auth.SignUpScreen
import com.example.nikestore.ui.screens.checkout.CheckoutDestination
import com.example.nikestore.ui.screens.checkout.CheckoutScreen
import com.example.nikestore.ui.screens.comment.CommentListDestination
import com.example.nikestore.ui.screens.comment.CommentListScreen
import com.example.nikestore.ui.screens.favorites.FavoriteProductsDestination
import com.example.nikestore.ui.screens.favorites.FavoriteProductsScreen
import com.example.nikestore.ui.screens.main.MainDestination
import com.example.nikestore.ui.screens.main.MainScreen
import com.example.nikestore.ui.screens.main.MainViewModel
import com.example.nikestore.ui.screens.product.detail.ProductDetailDestination
import com.example.nikestore.ui.screens.product.detail.ProductDetailScreen
import com.example.nikestore.ui.screens.product.list.ProductListDestination
import com.example.nikestore.ui.screens.product.list.ProductListScreen
import com.example.nikestore.ui.screens.shipping.ShippingDestination
import com.example.nikestore.ui.screens.shipping.ShippingScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

interface NavigationDestination {
    val route: String
    val titleRes: Int
}

@Composable
fun NikeNavGraph(
    intentData: Uri?,
    mainViewModel: MainViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val startDestination = MainDestination.route

    LaunchedEffect(intentData, mainViewModel.hasIntent) {
        if (mainViewModel.hasIntent)
            intentData?.getQueryParameter("order_id")?.let { orderId ->
                mainViewModel.setIntent()
                navController.navigate("${CheckoutDestination.route}/$orderId")
            }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestination.route) {
            MainScreen(
                navigateToProductDetail = { product ->
                    val productJson = Uri.encode(Json.encodeToString(product))
                    navController.navigate("${ProductDetailDestination.route}/$productJson")
                },
                navigateToProductList = { sort ->
                    navController.navigate("${ProductListDestination.route}/$sort")
                },
                navigateToLogin = {
                    navController.navigate(LoginDestination.route)
                },
                navigationToShipping = { purchaseDetail ->
                    val jsonPurchaseDetail = Json.encodeToString(purchaseDetail)
                    navController.navigate("${ShippingDestination.route}/$jsonPurchaseDetail")
                },
                navigateToFavoriteProducts = {
                    navController.navigate(FavoriteProductsDestination.route)
                },
                mainViewModel = mainViewModel
            )
        }

        composable(
            route = ProductDetailDestination.routeWithArg,
            arguments = listOf(
                navArgument(ProductDetailDestination.PRODUCT_ARG) { type = NavType.StringType }
            )
        ) {
            ProductDetailScreen(
                onBack = { navController.navigateUp() },
                navigateToCommentList = { productId ->
                    navController.navigate("${CommentListDestination.route}/$productId")
                },
                navigateToLogin = {
                    navController.navigate(LoginDestination.route)
                },
                mainViewModel = mainViewModel
            )
        }

        composable(
            route = CommentListDestination.routeWithArg,
            arguments = listOf(
                navArgument(CommentListDestination.PRODUCT_ID_ARG) { type = NavType.IntType }
            )
        ) {
            CommentListScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = ProductListDestination.routeWithArg,
            arguments = listOf(
                navArgument(ProductListDestination.SORT_ARG) { type = NavType.IntType }
            )
        ) {
            ProductListScreen(
                onBack = { navController.navigateUp() },
                navigateToProductDetail = { product ->
                    val productJson = Uri.encode(Json.encodeToString(product))
                    navController.navigate("${ProductDetailDestination.route}/$productJson")
                }
            )
        }

        composable(LoginDestination.route) {
            LoginScreen(
                navigateToSignUp = {
                    navController.navigate(SignUpDestination.route) {
                        popUpTo(LoginDestination.route) { inclusive = true }
                    }
                },
                onAuthSuccess = { navController.popBackStack() }
            )
        }

        composable(SignUpDestination.route) {
            SignUpScreen(
                navigateToLogin = {
                    navController.navigate(LoginDestination.route) {
                        popUpTo(SignUpDestination.route) { inclusive = true }
                    }
                },
                onAuthSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = ShippingDestination.routeWithArg,
            arguments = listOf(
                navArgument(ShippingDestination.PURCHASE_DETAIL_ARG) { type = NavType.StringType }
            )
        ) {
            ShippingScreen(
                onBack = { navController.navigateUp() },
                navigationToCheckout = { orderId ->
                    navController.navigate("${CheckoutDestination.route}/$orderId") {
                        popUpTo(ShippingDestination.routeWithArg) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = CheckoutDestination.routeWithArg,
            arguments = listOf(
                navArgument(CheckoutDestination.ORDER_ID_ARG) { type = NavType.IntType }
            )
        ) {
            CheckoutScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable(FavoriteProductsDestination.route) {
            FavoriteProductsScreen(
                onBack = { navController.navigateUp() },
                navigateToProductDetail = { product ->
                    val productJson = Uri.encode(Json.encodeToString(product))
                    navController.navigate("${ProductDetailDestination.route}/$productJson")
                }
            )
        }
    }
}