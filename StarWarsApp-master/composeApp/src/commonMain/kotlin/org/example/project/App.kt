package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var isLoading by remember { mutableStateOf(true) }
    var people by remember { mutableStateOf<List<Person>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = StarWarsRepository.getPeople()
                people = response.results
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage", color = Color.Red)
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(people) { person ->
                    PersonCard(person)
                }
            }
        }
    }
}

@Composable
fun PersonCard(person: Person) {
    val imageUrl = remember { ImageMapper.getImageUrl(person.uid) }
    val imageResource = imageUrl?.let { asyncPainterResource(data = it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(10.dp, shape = MaterialTheme.shapes.medium),
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
                    imageResource == null -> {
                        Text("Sin imagen", color = Color.Red)
                    }

                    imageResource.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }

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
                            Text("Imagen nula", color = Color.Red)
                        }
                    }

                    else -> {
                        Text("No se pudo cargar", color = Color.Red)
                    }
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

                var detail by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(person.uid) {
                    try {
                        val response = StarWarsRepository.getPersonDetail(person.uid)
                        val p = response.result.properties
                        detail = "Altura: ${p.height} cm\nPeso: ${p.mass} kg\nColor de ojos: ${p.eye_color}\nGénero: ${p.gender}"
                    } catch (e: Exception) {
                        detail = "Detalles no disponibles"
                    }
                }

                detail?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color(0xFF00BFFF) // Azul cyan
                    )
                }
            }
        }
    }
}



