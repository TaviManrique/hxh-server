package com.tavi.routes

import com.tavi.models.ApiResponse
import com.tavi.repository.HunterRepositoryAlternative
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.getAllHuntersAlternative() {
    val hunterRepositoryAlternative: HunterRepositoryAlternative by inject()
    get("/hunterxhunter/hunters") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val limit = call.request.queryParameters["limit"]?.toInt() ?: 4
            val apiResponse = hunterRepositoryAlternative.getAllHunters(
                page = page,
                limit = limit
            )
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