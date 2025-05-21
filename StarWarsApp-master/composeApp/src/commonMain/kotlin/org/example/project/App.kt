package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.model.Person
import org.example.project.repository.StarWarsRepository
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.shadow
import io.kamel.core.getOrNull
import io.kamel.core.isLoading
import io.kamel.core.isSuccess
import org.example.project.ui.StarWarsTheme
import org.example.project.util.ImageMapper // ✅ IMPORTANTE

@Composable
fun App() {
    StarWarsTheme {
        PersonListScreen()
    }
}

@Composable
fun PersonListScreen() {
    val scope = rememberCoroutineScope()
    var people by remember { mutableStateOf<List<Person>>(emptyList()) }
    var currentPage by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasMore by remember { mutableStateOf(true) }

    // Esto evita actualizaciones después de que el composable se destruya
    var isActive by remember { mutableStateOf(true) }
    DisposableEffect(Unit) {
        onDispose { isActive = false }
    }

    fun loadNextPage() {
        if (isLoading || !hasMore) return
        isLoading = true

        scope.launch {
            try {
                val response = StarWarsRepository.getPeople(currentPage)
                if (isActive) {
                    people += response.results
                    currentPage++
                    hasMore = response.info.next != null
                }
            } catch (e: Exception) {
                if (isActive) {
                    errorMessage = "Error al cargar página $currentPage: ${e.message}"
                }
            } finally {
                if (isActive) isLoading = false
            }
        }
    }

    // Cargar la primera página
    LaunchedEffect(Unit) {
        loadNextPage()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (errorMessage != null) {
            Text("Error: $errorMessage", color = Color.Red)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(people) { index, person ->
                PersonCard(person)

                // Cargar más cuando se acerque al final
                if (index == people.lastIndex - 5 && !isLoading && hasMore) {
                    loadNextPage()
                }
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PersonCard(person: Person) {
    val imageResource = asyncPainterResource(person.image)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(Modifier.fillMaxSize().padding(10.dp)) {
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageResource.isSuccess -> {
                        val painter = imageResource.getOrNull()
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = person.name,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Imagen nula", color = Color.Red)
                        }
                    }
                    imageResource.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }
                    else -> {
                        Text("Sin imagen", color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(text = person.name, fontSize = 20.sp, color = Color(0xFF00FFAA))
                Text(text = "Género: ${person.gender}")
                Text(text = "Especie: ${person.species}")
                Text(text = "Origen: ${person.origin.name}")
            }
        }
    }
}