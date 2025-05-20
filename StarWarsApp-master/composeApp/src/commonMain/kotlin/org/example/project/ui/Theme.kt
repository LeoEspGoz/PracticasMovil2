package org.example.project.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StarWarsColorScheme = darkColorScheme(
    primary = Color(0xFFF6C700),     // amarillo dorado
    onPrimary = Color.Black,
    background = Color(0xFF1B1B1B),  // fondo oscuro
    onBackground = Color.White,
    surface = Color(0xFF2C2C2C),     // tarjetas
    onSurface = Color.White
)

@Composable
fun StarWarsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StarWarsColorScheme,
        typography = MaterialTheme.typography,
        content = {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    )
}
