package com.tavi.repository

import com.tavi.models.ApiResponse
import com.tavi.models.Hunter

interface HunterRepository {

    val hunters: Map<Int, List<Hunter>>

    val page1: List<Hunter>
    val page2: List<Hunter>
    val page3: List<Hunter>
    val page4: List<Hunter>
    val page5: List<Hunter>


    suspend fun getAllHunters(page: Int = 1): ApiResponse
    suspend fun searchHunters(name: String?): ApiResponse
}