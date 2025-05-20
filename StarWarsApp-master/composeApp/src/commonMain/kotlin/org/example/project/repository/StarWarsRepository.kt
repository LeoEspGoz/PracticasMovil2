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

    // Cliente HTTP multiplataforma
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // Función suspendida para obtener los personajes
    suspend fun getPeople(): PeopleResponse {
        val response = client.get("https://www.swapi.tech/api/people").body<PeopleResponse>()
        return response
    }

    // Nueva función suspend para obtener detalles
    suspend fun getPersonDetail(id: String): PersonDetailResponse {
        return client.get("https://www.swapi.tech/api/people/$id").body()
    }
}
