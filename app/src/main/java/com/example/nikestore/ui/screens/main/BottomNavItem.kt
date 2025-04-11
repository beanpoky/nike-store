package com.example.nikestore.ui.screens.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nikestore.R

enum class BottomNavItem(@StringRes val title: Int, val icon: ImageVector) {
    HOME(R.string.home, Icons.Default.Home),
    CART(R.string.cart, Icons.Default.ShoppingCart),
    PROFILE(R.string.profile, Icons.Default.Person)
}