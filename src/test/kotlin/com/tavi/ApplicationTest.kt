package com.tavi

import com.tavi.models.ApiResponse
import com.tavi.repository.HunterRepository
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.assertEquals


class ApplicationTest {

    private val hunterRepository: HunterRepository by inject(HunterRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Welcome to Hunter x Hunter API",
                    actual = response.content
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
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
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all hunters endpoint, query all pages, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val hunters = listOf(
                hunterRepository.page1,
                hunterRepository.page2,
                hunterRepository.page3,
                hunterRepository.page4,
                hunterRepository.page5
            )
            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/hunterxhunter/hunters?page=$page").apply {
                    println("CURRENT PAGE: $page")
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    val expected = ApiResponse(
                        success = true,
                        message = "Ok",
                        prevPage = calculatePage(page)["prevPage"],
                        nextPage = calculatePage(page)["nextPage"],
                        hunters = hunters[page-1],
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
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Hunters not Found."
                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("EXPECTED: $expected")
                println("ACTUAL: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Only Numbers Allowed."
                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("EXPECTED: $expected")
                println("ACTUAL: $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hunter name, assert single hunter result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters/search?name=go").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).hunters.size
                assertEquals(
                    expected = 1,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hunter name, assert multiple hunters result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters/search?name=zoldyck").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).hunters.size
                assertEquals(
                    expected = 2,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters/search?name=").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).hunters
                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query non existing hunter, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/hunterxhunter/hunters/search?name=unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).hunters
                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint, assert not found`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Page not Found.",
                    actual = response.content
                )
            }
        }
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