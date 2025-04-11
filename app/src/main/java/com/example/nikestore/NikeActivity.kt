package com.example.nikestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nikestore.ui.screens.NikeNavGraph
import com.example.nikestore.ui.theme.NikeStoreTheme

class NikeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NikeStoreTheme {
                NikeNavGraph(intentData = intent.data)
            }
        }
    }
}