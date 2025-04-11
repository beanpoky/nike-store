package com.example.nikestore.data.repo

import com.example.nikestore.data.Banner
import com.example.nikestore.data.repo.source.BannerDataSource

class BannerRepositoryImpl(
    private val bannerRemoteDataSource: BannerDataSource
) : BannerRepository {

    override suspend fun getBanners(): List<Banner> = bannerRemoteDataSource.getBanners()
}