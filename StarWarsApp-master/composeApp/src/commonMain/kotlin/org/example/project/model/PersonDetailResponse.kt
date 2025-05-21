package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonDetailResponse(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String
)

