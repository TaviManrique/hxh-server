package com.tavi

import com.tavi.models.ApiResponse
import com.tavi.repository.HunterRepositoryImpl
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals


class ApplicationTest {

    @Test
    fun `access root endpoint, assert correct information`() = testApplication {
        val response = client.get("/")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        assertEquals(
            expected = "Welcome to Hunter x Hunter API",
            actual = response.bodyAsText()
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, assert correct information`() = testApplication {
        val hunterRepository = HunterRepositoryImpl()
        val response = client.get("/hunterxhunter/hunters")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
        val expected = ApiResponse(
            success = true,
            message = "Ok",
            prevPage = null,
            nextPage = 2,
            hunters = hunterRepository.page1,
            lastUpdated = actual.lastUpdated
        )

        println("EXPECTED: $expected")
        println("ACTUAL: $actual")
        assertEquals(
            expected = expected,
            actual = actual
        )

    }

    @ExperimentalSerializationApi
    @Test
    fun `access all hunters endpoint, query all pages, assert correct information`() = testApplication {
        val hunterRepository = HunterRepositoryImpl()
        val pages = 1..5
        val hunters = listOf(
            hunterRepository.page1,
            hunterRepository.page2,
            hunterRepository.page3,
            hunterRepository.page4,
            hunterRepository.page5
        )
        pages.forEach { page ->
            val response = client.get("/hunterxhunter/hunters?page=$page")
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status
            )
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
            val expected = ApiResponse(
                success = true,
                message = "Ok",
                prevPage = calculatePage(page)["prevPage"],
                nextPage = calculatePage(page)["nextPage"],
                hunters = hunters[page - 1],
                lastUpdated = actual.lastUpdated
            )
            println("PREV PAGE: ${calculatePage(page)["prevPage"]}")
            println("NEXT PAGE :${calculatePage(page)["nextPage"]}")
            //println("HUNTERS: ${hunters[page-1]}")
            assertEquals(
                expected = expected,
                actual = actual
            )
        }

    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() = testApplication {
        val response = client.get("/hunterxhunter/hunters?page=6")
        assertEquals(
            expected = HttpStatusCode.NotFound,
            actual = response.status
        )
        assertEquals(
            expected = "Page not Found.",
            actual = response.bodyAsText()
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() = testApplication {
        val response = client.get("/hunterxhunter/hunters?page=invalid")
        assertEquals(
            expected = HttpStatusCode.BadRequest,
            actual = response.status
        )
        val expected = ApiResponse(
            success = false,
            message = "Only Numbers Allowed."
        )

        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
        println("EXPECTED: $expected")
        println("ACTUAL: $actual")
        assertEquals(
            expected = expected,
            actual = actual
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hunter name, assert single hunter result`() = testApplication {
        val response = client.get("/hunterxhunter/hunters/search?name=gon")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).hunters.size
        assertEquals(
            expected = 1,
            actual = actual
        )

    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hunter name, assert multiple hunters result`() = testApplication {
        val response = client.get("/hunterxhunter/hunters/search?name=zoldyck")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).hunters.size
        assertEquals(
            expected = 2,
            actual = actual
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() = testApplication {
        val response = client.get("/hunterxhunter/hunters/search?name=")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).hunters
        assertEquals(
            expected = emptyList(),
            actual = actual
        )
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query non existing hunter, assert empty list as a result`() = testApplication {
        val response = client.get("/hunterxhunter/hunters/search?name=unknown")
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).hunters
        assertEquals(
            expected = emptyList(),
            actual = actual
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint, assert not found`() = testApplication {
        val response = client.get("/unknown")
        assertEquals(
            expected = HttpStatusCode.NotFound,
            actual = response.status
        )
        assertEquals(
            expected = "Page not Found.",
            actual = response.bodyAsText()
        )
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf("prevPage" to prevPage, "nextPage" to nextPage)
    }
}