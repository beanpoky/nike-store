package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Banner

interface BannerDataSource {

    suspend fun getBanners(): List<Banner>
}