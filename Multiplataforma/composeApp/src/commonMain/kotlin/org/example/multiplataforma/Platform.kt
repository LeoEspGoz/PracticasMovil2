package org.example.multiplataforma

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform