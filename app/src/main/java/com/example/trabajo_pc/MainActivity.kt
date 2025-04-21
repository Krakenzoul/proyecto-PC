package com.example.trabajo_pc

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.shadow



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JuegoCuatroEnRaya()


        }
    }
}

@Composable
fun JuegoCuatroEnRaya() {
    var tablero by remember { mutableStateOf(MutableList(6) { MutableList(7) { "" } }) }
    var turnoX by remember { mutableStateOf(true) }
    var ganador by remember { mutableStateOf<String?>(null) }

    // Función para encontrar fila vacía en una columna
    fun obtenerFilaDisponible(columna: Int): Int? {
        for (i in 5 downTo 0) {
            if (tablero[i][columna] == "") return i
        }
        return null
    }

    // IA básica (elige columna aleatoria válida)
    fun jugadaIA() {
        val opciones = (0..6).mapNotNull { col ->
            obtenerFilaDisponible(col)?.let { fila -> fila to col }
        }
        if (opciones.isNotEmpty()) {
            val (fila, col) = opciones.random()
            tablero[fila][col] = "O"
            ganador = verificarGanador(tablero)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tablero
        for (fila in 0 until 6) {
            Row {
                for (col in 0 until 7) {
                    val valor = tablero[fila][col]
                    CeldaTablero(valor) {
                        val filaDisponible = obtenerFilaDisponible(col)
                        if (filaDisponible != null && ganador == null && turnoX) {
                            tablero[filaDisponible][col] = "X"
                            ganador = verificarGanador(tablero)
                            if (ganador == null) {
                                turnoX = false
                                jugadaIA()
                                turnoX = true
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resultado o turno
        if (ganador != null) {
            Text("¡Ganó $ganador!", fontSize = 45.sp, color = Color.Green)
        } else {
            Text("Turno: ${if (turnoX) "X" else "O"}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            tablero = MutableList(6) { MutableList(7) { "" } }
            ganador = null
            turnoX = true
        }) {
            Text("Reiniciar")
        }
    }
}

fun verificarGanador(tablero: List<List<String>>): String? {
    val filas = tablero.size
    val columnas = tablero[0].size

    val direcciones = listOf(
        Pair(0, 1),   // →
        Pair(1, 0),   // ↓
        Pair(1, 1),   // ↘
        Pair(1, -1)   // ↙
    )

    for (i in 0 until filas) {
        for (j in 0 until columnas) {
            val jugador = tablero[i][j]
            if (jugador != "") {
                for ((dx, dy) in direcciones) {
                    var count = 1
                    var x = i + dx
                    var y = j + dy

                    while (
                        x in 0 until filas &&
                        y in 0 until columnas &&
                        y >= 0 &&
                        tablero[x][y] == jugador
                    ) {
                        count++
                        if (count == 4) return jugador
                        x += dx
                        y += dy
                    }
                }
            }
        }
    }

    return null
}


@Composable
fun FichaRoja() {
    Box(
        modifier = Modifier
            .size(35.dp) // Un poco más grande
            .background(
                brush = Brush.radialGradient( // Degradado radial para más profundidad
                    colors = listOf(Color(0xFFE57373), Color.Red),
                    center = Offset(50f, 50f),
                    radius = 24f
                ),
                shape = CircleShape
            )
            .border(2.dp, Color(0xFFB71C1C), CircleShape) // Borde más oscuro
            .shadow(elevation = 1.dp, shape = CircleShape) // Sombra sutil
            .padding(3.dp) // Un poco de espacio interior
    )
}

@Composable
fun FichaAmarilla() {
    Box(
        modifier = Modifier
            .size(35.dp) // Igual tamaño que la roja para consistencia
            .background(
                brush = Brush.radialGradient( // Mismo tipo de degradado
                    colors = listOf(Color(0xFFFFF176), Color.Yellow),
                    center = Offset(50f, 50f),
                    radius = 24f
                ),
                shape = CircleShape
            )
            .border(2.dp, Color(0xFFF9A825), CircleShape) // Borde más intenso
            .shadow(elevation = 1.dp, shape = CircleShape) // Misma sombra
            .padding(3.dp) // Mismo padding
    )
}

@Composable
fun CeldaTablero(valor: String, onClick: () -> Unit) {
    val colorFondo = Color(0xFFBCAAA4) // café claro
    val colorBorde = Color(0xFF6D4C41) // café oscuro
    val radio = 12.dp

    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .background(colorFondo)
            .border(2.dp, colorBorde, RoundedCornerShape(radio))
            .clickable(enabled = valor.isEmpty()) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        when (valor) {
            "X" -> FichaRoja()
            "O" -> FichaAmarilla()
        }
    }
}
