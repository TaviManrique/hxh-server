package com.tavi.plugins

import com.tavi.routes.getAllHunters
import com.tavi.routes.getAllHuntersAlternative
import com.tavi.routes.root
import com.tavi.routes.searchHunters
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.*

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
