package com.tavi.plugins

import com.tavi.routes.getAllHunters
import com.tavi.routes.root
import com.tavi.routes.searchHunters
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHunters()
        //getAllHuntersAlternative()
        searchHunters()

        static("/images") {
            resources("images")
        }
    }
}
