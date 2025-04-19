package com.example.trabajo_pc

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import kotlin.random.Random
import com.example.trabajo_pc.ui.theme.Trabajo_PcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Trabajo_PcTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JuegoTresEnRayaConIA(

                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun JuegoTresEnRayaConIA(modifier: Modifier = Modifier) {
    var tablero by remember { mutableStateOf(MutableList(6) { MutableList(7) { "" } }) }
    var turnoX by remember { mutableStateOf(true) }
    var ganador by remember { mutableStateOf<String?>(null) }

    // Función para verificar si hay un ganador
    fun verificarGanador(tablero: List<List<String>>, n: Int, m: Int): String? {
        val lineas = mutableListOf<List<String>>()

        // Filas
        for (i in 0 until n) {
            lineas.add(tablero[i])
        }

        // Columnas
        for (j in 0 until m) {
            val columna = mutableListOf<String>()
            for (i in 0 until n) {
                columna.add(tablero[i][j])
            }
            lineas.add(columna)
        }

        // Diagonales
        for (i in 0 until n - 3) {
            for (j in 0 until m - 3) {
                val diagonal1 = mutableListOf<String>()
                val diagonal2 = mutableListOf<String>()
                for (k in 0 until 4) {
                    diagonal1.add(tablero[i + k][j + k]) // Diagonal principal
                    diagonal2.add(tablero[i + k][j + 3 - k]) // Diagonal secundaria
                }
                lineas.add(diagonal1)
                lineas.add(diagonal2)
            }
        }

        // Verificar si hay una línea ganadora
        for (linea in lineas) {
            if (linea.all { it == "X" }) return "X"
            if (linea.all { it == "O" }) return "O"
        }

        return null
    }
    // Función para obtener la primera fila vacía en una columna (Esta es la más importante pana para que la IA y el jugador vayan hacía la ultima fila)
    fun obtenerFilaDisponible(columna: Int): Int? {
        for (i in 5 downTo 0) { // Comienza desde la fila más baja (6) hacia arriba
            if (tablero[i][columna] == "") {
                return i
            }
        }
        return null
    }
    // Función para realizar la jugada de la IA (elige una celda vacía aleatoria esta por ahora como IA, pero creo que se puede mejorar haciendo que seleccione una o dos celdas después del movimiento de uno)
    fun jugadaIA(tablero: MutableList<MutableList<String>>, n: Int, m: Int) {
        val celdasVacias = mutableListOf<Pair<Int, Int>>()

        // Buscar todas las celdas vacías y su fila correspondiente
        for (j in 0 until m) { // Recorremos las columnas
            val filaDisponible = obtenerFilaDisponible(j) // Obtenemos la primera fila vacía de cada columna
            if (filaDisponible != null) {
                celdasVacias.add(Pair(filaDisponible, j)) // Si hay fila vacía, la agregamos a la lista
            }
        }

        if (celdasVacias.isNotEmpty()) {
            // La IA selecciona una celda vacía aleatoria
            val (i, j) = celdasVacias[Random.nextInt(celdasVacias.size)]
            tablero[i][j] = "O" // Coloca su ficha en la posición seleccionada

            // Verificar si la IA ha ganado después de su jugada
            ganador = verificarGanador(tablero, n, m)
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0 until 6) {
            Row {
                for (j in 0 until 7) {
                    val valor = tablero[i][j]
                    Box(
                        modifier = Modifier
                            .size((300 / 7).dp)
                            .padding(4.dp)
                            .border(2.dp, Color.Black)
                            .background(Color.White)
                            .clickable(enabled = valor.isEmpty() && ganador == null && turnoX) {
                                val filaDisponible = obtenerFilaDisponible(j)
                                if (filaDisponible != null) {
                                    // Colocar la ficha en la fila disponible de la columna seleccionada
                                    tablero[filaDisponible][j] = "X"
                                    ganador = verificarGanador(tablero, 6, 7)
                                    if (ganador == null) {
                                        turnoX = false
                                        // aqui se pone en el tuerno para que después de uno hacerle la ia haga un movimiento
                                        jugadaIA(tablero, 6, 7)
                                        turnoX = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = valor,
                            fontSize = 32.sp,
                            color = if (valor == "X") Color.Red else Color.Blue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (ganador != null) {
            Text(text = "¡Ganó $ganador!", fontSize = 24.sp, color = Color.Green)
        } else {
            Text(text = "Turnooooo: ${if (turnoX) "X" else "O"}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        // solo resetea el tablero y los puntajes junt0o al turno ganador
        Button(onClick = {
            tablero = MutableList(6) { MutableList(7) { "" } }
            turnoX = true
            ganador = null
        }) {
            Text("Reiniciar")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Tabla3x3Preview() {
    Trabajo_PcTheme {
        JuegoTresEnRayaConIA()
    }
}