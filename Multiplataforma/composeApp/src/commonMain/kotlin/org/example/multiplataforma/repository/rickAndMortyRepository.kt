package org.example.multiplataforma.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.multiplataforma.model.EpisodeResponse
import org.example.multiplataforma.model.PeopleResponse
import org.example.multiplataforma.model.Person
import org.example.multiplataforma.model.PersonDetailResponse

object rickAndMortyRepository{

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

    suspend fun getAllPeople(): List<Person> {
        val allPeople = mutableListOf<Person>()
        var currentPage = 1
        var totalPages: Int

        do {
            val response = getPeople(currentPage)
            allPeople.addAll(response.results)
            totalPages = response.info.pages
            currentPage++
        } while (currentPage <= totalPages)

        return allPeople
    }

    suspend fun getPersonDetail(id: Int): PersonDetailResponse {
        return client.get("https://rickandmortyapi.com/api/character/$id").body()
    }

    suspend fun getEpisode(url: String): EpisodeResponse {
        return client.get(url).body()
    }
}

