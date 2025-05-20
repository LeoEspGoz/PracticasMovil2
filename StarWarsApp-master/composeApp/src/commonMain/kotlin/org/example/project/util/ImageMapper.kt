package org.example.project.util

object ImageMapper {
    private val imageMap = mapOf(
        "1" to "https://i.imgur.com/2BAsrex.jpeg", // Luke Skywalker
        "2" to "https://i.imgur.com/qsbFfDQ.jpeg", // C-3PO
        "3" to "https://i.imgur.com/bXks492.jpeg", // R2-D2
        "4" to "https://i.imgur.com/VNRpAxD.jpeg", // Darth Vader
        "5" to "https://i.imgur.com/6V0cOU9.jpeg", // Leia Organa
        "6" to "https://i.imgur.com/70HDx9N.jpeg", // Owen Lars
        "7" to "https://i.imgur.com/8f5jRfq.jpeg", // Beru Whitesun
        "8" to "https://i.imgur.com/779VukD.jpeg", // R5-D4
        "9" to "https://i.imgur.com/aufGnbS.jpeg", // Biggs Darklighter
        "10" to "https://i.imgur.com/kSPjphd.jpeg" // Obi-Wan Kenobi
    )

    fun getImageUrl(uid: String): String? = imageMap[uid]
}
