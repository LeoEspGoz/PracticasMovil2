package com.example.futbilito

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var _x by mutableStateOf(0f)
    private var _y by mutableStateOf(0f)
    private var scoreLocal by mutableStateOf(0)
    private var scoreVisitante by mutableStateOf(0)

    private var velocityX by mutableStateOf(0f)
    private var velocityY by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SensorManager::class.java)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            FutbolitoGame(_x, _y, scoreLocal, scoreVisitante,
                onLocalGoal = {
                    scoreLocal++
                    Log.d("GOAL", "⚽ Gol del equipo LOCAL! Marcador: $scoreLocal - $scoreVisitante")
                    resetBall()
                },
                onVisitanteGoal = {
                    scoreVisitante++
                    Log.d("GOAL", "⚽ Gol del equipo VISITANTE! Marcador: $scoreLocal - $scoreVisitante")
                    resetBall()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val ax = it.values[0]
            val ay = it.values[1]

            // Modificar la velocidad para que la pelota se mueva más rápido
            velocityX -= ax * 3
            velocityY += ay * 3

            // Limitar la velocidad para que no se descontrole
            velocityX = max(-15f, min(15f, velocityX))
            velocityY = max(-15f, min(15f, velocityY))

            // Actualizar la posición de la pelota
            _x += velocityX
            _y += velocityY

            // Limitar la posición de la pelota para que no salga de la pantalla
            if (_x < -450f) {
                _x = -450f
                velocityX = -velocityX
            } else if (_x > 450f) {
                _x = 450f
                velocityX = -velocityX
            }

            // Ajuste en el límite superior para permitir que la pelota llegue a la portería superior
            if (_y < -650f) { // Permitimos que suba más
                _y = -650f
                velocityY = -velocityY



        } else if (_y > 650f) {
                _y = 650f
                velocityY = -velocityY
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun resetBall() {
        _x = 0f
        _y = 0f
        velocityX = 0f
        velocityY = 0f
    }
}

@Composable
fun FutbolitoGame(
    x: Float,
    y: Float,
    scoreLocal: Int,
    scoreVisitante: Int,
    onLocalGoal: () -> Unit,
    onVisitanteGoal: () -> Unit
) {
    var isBallInLocalGoal by remember { mutableStateOf(false) }
    var isBallInVisitanteGoal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de la cancha
        Image(
            painter = painterResource(id = R.drawable.cancha),
            contentDescription = "Cancha de fútbol",
            modifier = Modifier.fillMaxSize()
        )

        // Marcador
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 53.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LOCAL", fontSize = 20.sp, color = Color.Black)
                    Text("$scoreLocal", fontSize = 70.sp, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VISITANTE", fontSize = 18.sp, color = Color.Black)
                    Text("$scoreVisitante", fontSize = 70.sp, color = Color.Black)
                }
            }
        }

        // Canvas para la pelota y las porterías
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ballRadius = 14.dp.toPx()

            // Tamaño y posición de las porterías
            val goalWidth = 120.dp.toPx()
            val goalHeight = 40.dp.toPx()

            // Ajuste de la portería local más abajo
            val goalOffsetTop = 200.dp.toPx()  // Movemos la portería superior más abajo
            val goalOffsetBottom = 200.dp.toPx()

            val goalXStart = (size.width - goalWidth) / 2
            val goalXEnd = goalXStart + goalWidth

            // Dibujar las porterías
            drawRect(Color.Black, topLeft = Offset(goalXStart, goalOffsetTop), size = Size(goalWidth, goalHeight))
            drawRect(Color.Black, topLeft = Offset(goalXStart, size.height - goalHeight - goalOffsetBottom), size = Size(goalWidth, goalHeight))

            // Posición de la pelota
            val ballPosition = Offset(size.width / 2 + x, size.height / 2 + y)

            // Verificación si la pelota está dentro de las porterías
            val isBallInsideGoalX = ballPosition.x in goalXStart..goalXEnd
            val isBallInLocal = isBallInsideGoalX && (ballPosition.y - ballRadius <= goalOffsetTop + goalHeight)
            val isBallInVisitante = isBallInsideGoalX && (ballPosition.y + ballRadius >= size.height - goalOffsetBottom - goalHeight)

            // Detectar gol para el equipo local
            if (isBallInLocal && !isBallInLocalGoal) {
                onLocalGoal()
                isBallInLocalGoal = true
            } else if (!isBallInLocal) {
                isBallInLocalGoal = false
            }

            // Detectar gol para el equipo visitante
            if (isBallInVisitante && !isBallInVisitanteGoal) {
                onVisitanteGoal()
                isBallInVisitanteGoal = true
            } else if (!isBallInVisitante) {
                isBallInVisitanteGoal = false
            }

            // Dibujar la pelota
            drawCircle(
                color = Color(0.0f, 0.737f, 0.831f, 1.0f),
                radius = ballRadius,
                center = ballPosition
            )
        }
    }
}
