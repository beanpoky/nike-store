package com.example.nikestore.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(dateString: String): String? {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = inputFormatter.parse(dateString)
    val outputFormatter = SimpleDateFormat("EEEE dd MMMM yy", Locale.getDefault())
    return date?.let { outputFormatter.format(it) }
}

fun openUrlInCustomTab(context: Context, url: String) {
    try {
        val uri = Uri.parse(url)
        val intentBuilder = CustomTabsIntent.Builder()
        intentBuilder.setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
        intentBuilder.setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
        val customTabsIntent = intentBuilder.build()
        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        customTabsIntent.launchUrl(context, uri)
    } catch (e: Exception) {
        Log.e("TAG", "openUrlInCustomTab: ${e.message}")
    }
}