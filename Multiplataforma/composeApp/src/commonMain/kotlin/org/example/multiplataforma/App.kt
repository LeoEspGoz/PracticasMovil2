package org.example.multiplataforma

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.core.getOrNull
import io.kamel.core.isLoading
import io.kamel.core.isSuccess
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import multiplataforma.composeapp.generated.resources.Res
import multiplataforma.composeapp.generated.resources.compose_multiplatform
import org.example.multiplataforma.model.Person
import org.example.multiplataforma.repository.rickAndMortyRepository

@Composable
fun App() {
    PersonListScreen()
}

@Composable
fun PersonListScreen() {
    var isLoading by remember { mutableStateOf(true) }
    var personaje by remember { mutableStateOf<List<Person>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            personaje = rickAndMortyRepository.getAllPeople()
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage ?: "Error desconocido", color = Color.Red)
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(personaje) { person ->
                    PersonCard(person)
                }
            }
        }
    }
}
@Composable
fun PersonCard(person: Person) {
    val imageResource = asyncPainterResource(data = person.image)

    var episodeNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var episodeLoading by remember { mutableStateOf(true) }
    var episodeError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(person.episode) {
        episodeLoading = true
        episodeError = null
        try {
            episodeNames = loadEpisodesNames(person.episode)
        } catch (e: Exception) {
            episodeError = "No se pudieron cargar episodios"
        } finally {
            episodeLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageResource.isLoading -> CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    imageResource.isSuccess -> {
                        val painter = imageResource.getOrNull()
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = person.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("No se pudo cargar imagen", color = Color.Red)
                        }
                    }
                    else -> Text("No se pudo cargar imagen", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = person.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700) // Dorado
                )
                Text(
                    text = "Estado: ${person.status}",
                    fontSize = 14.sp,
                    color = when (person.status.lowercase()) {
                        "alive" -> Color.Green
                        "dead" -> Color.Red
                        else -> Color.Gray
                    }
                )
                Text(
                    text = "Especie: ${person.species}",
                    fontSize = 14.sp,
                    color = Color(0xFF00BFFF) // Azul cyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Origen: ${person.origin.name}",
                    fontSize = 14.sp,
                    color = Color(0xFF8B4513) // Marrón para diferenciar
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (episodeLoading) {
                    Text("Cargando episodios...", fontSize = 12.sp, color = Color.Gray)
                } else if (episodeError != null) {
                    Text(episodeError ?: "", fontSize = 12.sp, color = Color.Red)
                } else {
                    Text(
                        text = "Episodios:\n${episodeNames.joinToString(", ")}",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 3
                    )
                }
            }
        }
    }
}
suspend fun loadEpisodesNames(episodeUrls: List<String>): List<String> = coroutineScope {
    episodeUrls.mapNotNull { episodeUrl ->
        val id = episodeUrl.substringAfterLast("/").toIntOrNull()
        if (id != null) {
            async {
                try {
                    val episode = rickAndMortyRepository.getEpisode(episodeUrl)
                    episode.name
                } catch (e: Exception) {
                    null
                }
            }
        } else null
    }.awaitAll().filterNotNull()
}