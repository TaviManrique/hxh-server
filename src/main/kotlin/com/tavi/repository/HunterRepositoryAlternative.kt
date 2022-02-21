package com.tavi.repository

import com.tavi.models.ApiResponse
import com.tavi.models.Hunter

interface HunterRepositoryAlternative {

    val hunters: List<Hunter>

    suspend fun getAllHunters(page: Int = 1, limit: Int = 4): ApiResponse
    suspend fun searchHunters(name: String?): ApiResponse
}