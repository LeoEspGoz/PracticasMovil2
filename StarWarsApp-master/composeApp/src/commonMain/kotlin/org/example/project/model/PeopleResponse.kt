package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class PeopleResponse(
    val results: List<Person>
)

@Serializable
data class Person(
    val name: String,
    val uid: String,
    val url: String
)

