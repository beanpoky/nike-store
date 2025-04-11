package com.example.nikestore.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.nikestore.R

data class EmptyState(
    val mustShow: Boolean,
    @DrawableRes val imageResId: Int = R.drawable.ic_undraw_empty_cart,
    @StringRes val messageResId: Int = 0,
    val mustShowCallToActionButton: Boolean = false
)