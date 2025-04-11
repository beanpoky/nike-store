package com.example.nikestore.data.repo.source

import com.example.nikestore.data.Banner
import com.example.nikestore.services.http.ApiService

class BannerRemoteDataSource(private val apiService: ApiService) : BannerDataSource {

    override suspend fun getBanners(): List<Banner> = apiService.getBanners()
}