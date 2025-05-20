package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonDetailResponse(
    val result: PersonDetailResult
)

@Serializable
data class PersonDetailResult(
    val properties: PersonProperties
)

@Serializable
data class PersonProperties(
    val name: String,
    val height: String,
    val mass: String,
    val hair_color: String,
    val skin_color: String,
    val eye_color: String,
    val birth_year: String,
    val gender: String,
    val homeworld: String
)
