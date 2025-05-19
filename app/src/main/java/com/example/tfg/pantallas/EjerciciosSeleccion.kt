package com.example.tfg.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import android.widget.Toast

@Composable
fun mostrarEjerciciosParaElegir(navController: NavController, rutinaState: RutinaState) {
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) } // Estado para controlar operaciones en curso
    val contexto = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1000) // Simulamos carga de datos
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Título de la pantalla
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 32.dp), // Separa el título de la parte superior
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona un Ejercicio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color(0xFF1976D2)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 120.dp) // Espacio para el botón "Volver"
                ) {
                    items(ejerciciosList) { ejercicio ->
                        // Cada fila de ejercicio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                                rutinaState.actualizarEjercicioEnBD(contexto, nuevoEjercicio) { exito ->
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
                                                rutinaState.guardarEjercicioEnBD(contexto, dia, nuevoEjercicio) { exito, documentId ->
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
                                }
                                .padding(8.dp)
                                .background(Color.White, shape = RoundedCornerShape(12.dp))
                                .border(2.dp, Color.LightGray, shape = RoundedCornerShape(12.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = ejercicio.imagenRes),
                                contentDescription = ejercicio.titulo,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(5.dp), // Padding por todos lados, no solo start
                                contentScale = ContentScale.Fit // Muestra la imagen completa
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = ejercicio.titulo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp)) // Espacio entre ejercicios
                    }
                }

                // Botón de "Volver"
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Volver",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}