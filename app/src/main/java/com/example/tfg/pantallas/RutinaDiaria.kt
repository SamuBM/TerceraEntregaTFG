package com.example.tfg.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

class RutinaDiariaState {
    val rutina = mutableStateListOf<Ejercicio?>()

    var ultimaPosicionSeleccionada: Int? = null

    fun obtenerRutinaFiltrada(): List<Ejercicio> {
        return rutina.filterNotNull()
    }

    fun eliminarEjercicio(index: Int) {
        try {
            if (index < rutina.size) {
                rutina.removeAt(index)
                val nuevaLista = rutina.toMutableList()
                rutina.clear()
                rutina.addAll(nuevaLista)
            }
        } catch (e: Exception) {}
    }
}

@Composable
fun mostrarRutinaDiaria(
    navController: NavController,
    rutinaDiariaState: RutinaDiariaState = remember { RutinaDiariaState() }
) {
    // Obtener los tiempos guardados en PantallaTiempo
    val tiempoEjercicio = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoEjercicio") ?: 30
    val tiempoDescanso = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoDescanso") ?: 30
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))) {
        // Título
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Mi Rutina de Hoy",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Rutina diaria (LazyRow con ejercicios)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(rutinaDiariaState.rutina) { index, ejercicio ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ejercicio == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        rutinaDiariaState.ultimaPosicionSeleccionada = index
                                        navController.currentBackStackEntry?.savedStateHandle?.set("origenRutina", "diaria")
                                        navController.navigate("EjerciciosDiariosSeleccion")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("?", fontSize = 32.sp, color = Color.Gray)

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(30.dp)
                                        .background(color = Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(50))
                                        .clickable {
                                            rutinaDiariaState.eliminarEjercicio(index)
                                        }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("X", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Modifica la parte de la Image en el Box de los ejercicios
                                Image(
                                    painter = painterResource(id = ejercicio.imagenRes),
                                    contentDescription = ejercicio.titulo,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)  // Añadimos padding para evitar que toque los bordes
                                        .clickable {
                                            rutinaDiariaState.ultimaPosicionSeleccionada = index
                                            navController.currentBackStackEntry?.savedStateHandle?.set("origenRutina", "diaria")
                                            navController.navigate("EjerciciosDiariosSeleccion")
                                        },
                                    contentScale = ContentScale.Fit  // Cambiamos de Crop a Fit para evitar recortes
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(30.dp)
                                        .background(color = Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(50))
                                        .clickable {
                                            rutinaDiariaState.eliminarEjercicio(index)
                                        }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("X", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }

                // Botón para añadir nuevo ejercicio
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable {
                                val indexNuevo = rutinaDiariaState.rutina.size
                                rutinaDiariaState.rutina.add(null)
                                rutinaDiariaState.ultimaPosicionSeleccionada = indexNuevo
                                navController.currentBackStackEntry?.savedStateHandle?.set("origenRutina", "diaria")
                                navController.navigate("EjerciciosDiariosSeleccion")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }
        }

        // Botón Empezar en la parte inferior de la pantalla
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp, start = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("rutinaTipo", "diaria")
                    navController.navigate("Cronometro/$tiempoEjercicio/$tiempoDescanso")
                },
                modifier = Modifier
                    .height(48.dp)
                    .width(180.dp)
                    .background(Color(0xFF1976D2), shape = RoundedCornerShape(16.dp)),
                content = {
                    Text("Empezar", fontSize = 16.sp, color = Color.White)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            )
        }

        // Mantener el navegador en la parte inferior
        mostrarNavegador(navController, "Home")
    }
}
