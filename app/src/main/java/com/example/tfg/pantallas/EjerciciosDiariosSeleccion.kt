package com.example.tfg.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun mostrarEjerciciosDiariosParaElegir(
    navController: NavController,
    rutinaDiariaState: RutinaDiariaState
) {
    // Reusamos la lista de ejercicios existente
    val ejercicios = ejerciciosList
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))){
        Box(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 20.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                items(ejercicios) { ejercicio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable {
                                // Actualizar el ejercicio en la posición seleccionada
                                val posicion = rutinaDiariaState.ultimaPosicionSeleccionada
                                if (posicion != null && posicion < rutinaDiariaState.rutina.size) {
                                    rutinaDiariaState.rutina[posicion] = ejercicio
                                }
                                navController.popBackStack()
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6))
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = ejercicio.imagenRes),
                                    contentDescription = ejercicio.titulo,
                                    contentScale = ContentScale.Fit,  // Cambiamos de Crop a Fit
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)  // Añadimos padding para mejorar la apariencia
                                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ejercicio.titulo,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
    }

    }
}