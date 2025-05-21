package org.example.project.repository

import org.example.project.model.PeopleResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.model.PersonDetailResponse

object StarWarsRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getPeople(page: Int = 1): PeopleResponse {
        return client.get("https://rickandmortyapi.com/api/character?page=$page").body()
    }
}