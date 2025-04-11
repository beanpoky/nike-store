package com.example.nikestore.data.repo

import com.example.nikestore.data.Banner

interface BannerRepository {

    suspend fun getBanners(): List<Banner>
}