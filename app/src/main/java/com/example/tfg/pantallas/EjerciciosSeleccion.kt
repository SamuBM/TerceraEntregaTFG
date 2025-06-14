package com.example.tfg.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.ui.graphics.Brush

@Composable
fun mostrarEjerciciosParaElegir(navController: NavController, rutinaState: RutinaState) {
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) } // Estado para controlar operaciones en curso
    val contexto = LocalContext.current
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

    LaunchedEffect(Unit) {
        delay(1000) // Simulamos carga de datos
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))) {
        if (isLoading || isProcessing) {
            // Cargando...
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isProcessing) "Guardando ejercicio..." else "Cargando ejercicios...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            // Pantalla con los ejercicios
            Box(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 20.dp)) {

                Row {
                    Text(
                        text = "Selecciona un Ejercicio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color(0xFF1976D2),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 80.dp, top = 50.dp)
                ) {
                    items(ejerciciosList) { ejercicio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable {
                                    // Verifica que la posición seleccionada no sea null
                                    rutinaState.ultimaPosicionSeleccionada?.let { (dia, index) ->
                                        // Activamos el estado de procesamiento
                                        isProcessing = true

                                        // Creamos un nuevo ejercicio basado en la selección
                                        val ejercicioActual = if (index < rutinaState.rutina[dia].size) {
                                            rutinaState.rutina[dia][index]
                                        } else null

                                        // Dentro de la función clickable del ejercicio seleccionado:
                                        if (ejercicioActual != null && ejercicioActual.documentId != null) {
                                            // Es una actualización - creamos un nuevo ejercicio con el mismo document ID
                                            val nuevoEjercicio = Ejercicio(
                                                id = ejercicioActual.id,
                                                titulo = ejercicio.titulo,
                                                descripcion = ejercicio.descripcion,
                                                imagenRes = ejercicio.imagenRes,
                                                videoRes = ejercicio.videoRes,
                                                documentId = ejercicioActual.documentId
                                            )

                                            // Actualizar en Firebase y luego en la lista local
                                            try {
                                                rutinaState.actualizarEjercicioEnBD(contexto, nuevoEjercicio, dia, index) { exito ->
                                                    if (exito) {
                                                        // Actualizamos la lista local
                                                        rutinaState.rutina[dia][index] = nuevoEjercicio
                                                        // Navegamos solo después de completar la operación
                                                        navController.navigate("RutinaSemanal") {
                                                            popUpTo("RutinaSemanal") { inclusive = true }
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            contexto,
                                                            "Error al actualizar ejercicio",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    isProcessing = false // Desactivamos el estado de procesamiento
                                                }
                                            } catch (e: Exception) {
                                                Log.e("EjerciciosSeleccion", "Error en actualización: ${e.message}")
                                                Toast.makeText(
                                                    contexto,
                                                    "Error: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                isProcessing = false
                                            }
                                        } else {
                                            // Es una inserción - usamos un ID temporal
                                            val nuevoEjercicio = Ejercicio(
                                                id = -1, // ID temporal
                                                titulo = ejercicio.titulo,
                                                descripcion = ejercicio.descripcion,
                                                imagenRes = ejercicio.imagenRes,
                                                videoRes = ejercicio.videoRes
                                            )

                                            // Guardar en Firebase y luego actualizar con el ID en la lista local
                                            try {
                                                rutinaState.guardarEjercicioEnBD(contexto, dia, nuevoEjercicio, index) { exito, documentId ->
                                                    if (exito && documentId.isNotEmpty()) {
                                                        // Crear nuevo ejercicio con el ID del documento asignado por Firebase
                                                        val ejercicioConId = Ejercicio(
                                                            id = documentId.hashCode(), // Usamos el hashCode del documentId como ID
                                                            titulo = nuevoEjercicio.titulo,
                                                            descripcion = nuevoEjercicio.descripcion,
                                                            imagenRes = nuevoEjercicio.imagenRes,
                                                            videoRes = nuevoEjercicio.videoRes,
                                                            documentId = documentId
                                                        )

                                                        // Asegurarse de que el índice sigue siendo válido
                                                        if (index < rutinaState.rutina[dia].size) {
                                                            rutinaState.rutina[dia][index] = ejercicioConId
                                                        } else {
                                                            // Por si acaso el índice ya no es válido
                                                            rutinaState.rutina[dia].add(ejercicioConId)
                                                        }

                                                        // Navegamos solo después de completar la operación
                                                        navController.navigate("RutinaSemanal") {
                                                            popUpTo("RutinaSemanal") { inclusive = true }
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            contexto,
                                                            "Error al guardar ejercicio",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    isProcessing = false // Desactivamos el estado de procesamiento
                                                }
                                            } catch (e: Exception) {
                                                Log.e("EjerciciosSeleccion", "Error en inserción: ${e.message}")
                                                Toast.makeText(
                                                    contexto,
                                                    "Error: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                isProcessing = false
                                            }
                                        }
                                    } ?: run {
                                        // Si la posición es null, regresa sin hacer cambios
                                        navController.popBackStack()
                                    }
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
}