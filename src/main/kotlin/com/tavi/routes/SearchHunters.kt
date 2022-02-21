package com.tavi.routes

import com.tavi.repository.HunterRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHunters() {
    val hunterRepository: HunterRepository by inject()
    get("/hunterxhunter/hunters/search") {
        val name = call.request.queryParameters["name"]

        val apiResponse = hunterRepository.searchHunters(name = name)
        call.respond(
            message = apiResponse,
            status = HttpStatusCode.OK
        )
    }
}