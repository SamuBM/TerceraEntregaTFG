package com.example.tfg.pantallas

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.tfg.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun mostrarEjercicio(navController: NavController, ejercicioId: Int) {
    val ejercicio = ejerciciosList.find { it.id == ejercicioId } ?: return
    var isLoading by remember { mutableStateOf(true) }
    var showVideo by remember { mutableStateOf(true) }

    // VideoView para manejar la reproducción del video
    val videoView = remember { android.widget.VideoView(navController.context) }

    // Detener el video cuando la pantalla se destruya
    DisposableEffect(Unit) {
        onDispose {
            videoView.stopPlayback()
        }
    }

    // Simula la carga durante 2 segundos
    LaunchedEffect(ejercicioId) {
        delay(2000)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            // Indicador de carga centrado
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Contenido del ejercicio
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp), // espacio inferior
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🏷 Título con sombra y estilo
                Text(
                    text = ejercicio.titulo,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color(0xFF1976D2),
                        shadow = Shadow(color = Color.Gray, offset = Offset(2f, 2f), blurRadius = 4f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🎥 Video o imagen con bordes redondeados
                if (showVideo) {
                    AndroidView(factory = { context ->
                        videoView.apply {
                            setVideoURI(Uri.parse("android.resource://${context.packageName}/${ejercicio.videoRes}"))
                            setOnPreparedListener { it.isLooping = true; start() }
                        }
                    },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
                            .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)) // Borde de video
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.fotoblanca),
                        contentDescription = "Imagen Blanca",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
                            .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)) // Borde de imagen
                            .padding(horizontal = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 📄 Descripción con un texto más estilizado
                Text(
                    text = ejercicio.descripcion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 🔙 Botón volver con color de fondo y bordes redondeados
                Button(
                    onClick = {
                        showVideo = false
                        navController.navigate("Ejercicios")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(48.dp)
                        .fillMaxWidth(0.8f) // Hacer el botón más grande
                        .clip(RoundedCornerShape(24.dp)) // Bordes redondeados
                        .background(MaterialTheme.colorScheme.primary), // Color de fondo
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Volver", color = Color.White)
                }
            }
        }
    }
}


