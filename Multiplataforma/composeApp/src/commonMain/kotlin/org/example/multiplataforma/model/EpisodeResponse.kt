package org.example.multiplataforma.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeResponse(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String
)