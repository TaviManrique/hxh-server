package com.tavi.routes

import com.tavi.models.ApiResponse
import com.tavi.repository.HunterRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getAllHunters() {
    val hunterRepository: HunterRepository by inject()
    get("/hunterxhunter/hunters") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            require(page in 1..5)
            val apiResponse = hunterRepository.getAllHunters(page)
            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )
        }catch (e: NumberFormatException) {
            call.respond(
                message = ApiResponse(success = false, message = "Only Numbers Allowed."),
                status = HttpStatusCode.BadRequest
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                message = ApiResponse(success = false, message = "Hunters not Found."),
                status = HttpStatusCode.NotFound
            )
        }
    }
}