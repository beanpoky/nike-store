package com.example.nikestore.ui.screens.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nikestore.R
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.Product
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.cart.CartScreen
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.screens.home.HomeScreen
import com.example.nikestore.ui.screens.profile.ProfileScreen
import com.example.nikestore.ui.theme.NikeTheme

object MainDestination : NavigationDestination {
    override val route = "HOME"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToProductDetail: (Product) -> Unit,
    navigateToProductList: (Int) -> Unit,
    navigateToLogin: () -> Unit,
    navigationToShipping: (PurchaseDetail) -> Unit,
    navigateToFavoriteProducts: () -> Unit,
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: BottomNavItem.HOME.name
    var cartItemCount = 0
    if (mainViewModel.networkDataUiState is NetworkDataUiState.Success)
        cartItemCount =
            (mainViewModel.networkDataUiState as NetworkDataUiState.Success<CartItemCount>).data.count

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when (currentRoute) {
                        BottomNavItem.HOME.name -> {
                            Icon(
                                painter = painterResource(R.drawable.ic_nike_logo),
                                contentDescription = stringResource(R.string.app_name),
                                tint = NikeTheme.colors.appBarTitle
                            )
                        }

                        BottomNavItem.CART.name -> {
                            Text(
                                text = stringResource(R.string.cart),
                                color = NikeTheme.colors.appBarTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        BottomNavItem.PROFILE.name -> {
                            Text(
                                text = stringResource(R.string.profile),
                                color = NikeTheme.colors.appBarTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NikeTheme.colors.appBar)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onSelectedItem = {
                    navController.navigate(it.name) {
                        popUpTo(BottomNavItem.HOME.name) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                cartItemCount = cartItemCount
            )
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.HOME.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.HOME.name) {
                HomeScreen(
                    navigateToProductDetail = navigateToProductDetail,
                    navigateToProductList = navigateToProductList
                )
            }
            composable(BottomNavItem.CART.name) {
                CartScreen(
                    navigateToProductDetail = navigateToProductDetail,
                    navigateToLogin = navigateToLogin,
                    navigationToShipping = navigationToShipping,
                    mainViewModel = mainViewModel
                )
            }
            composable(BottomNavItem.PROFILE.name) {
                ProfileScreen(
                    navigateToLogin = navigateToLogin,
                    navigateToFavoriteProducts = navigateToFavoriteProducts,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onSelectedItem: (BottomNavItem) -> Unit,
    cartItemCount: Int,
    modifier: Modifier = Modifier
) {
    val items = BottomNavItem.entries.toTypedArray()

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = NikeTheme.colors.appBar
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.name,
                onClick = { onSelectedItem(item) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item == BottomNavItem.CART && cartItemCount > 0) {
                                Badge(
                                    containerColor = NikeTheme.colors.badge,
                                    contentColor = NikeTheme.colors.badgeContent
                                ) { Text(cartItemCount.toString()) }
                            }
                        }
                    ) {
                        Icon(item.icon, stringResource(item.title))
                    }
                },
                label = { Text(stringResource(item.title)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = NikeTheme.colors.navigationSelected,
                    selectedIconColor = NikeTheme.colors.navigationSelected,
                    unselectedTextColor = NikeTheme.colors.navigationUnselected,
                    unselectedIconColor = NikeTheme.colors.navigationUnselected
                )
            )
        }
    }
}