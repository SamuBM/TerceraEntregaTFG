package com.example.tfg.pantallas

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

@Composable
fun mostrarEjercicio(navController: NavController, ejercicioId: Int) {
    val ejercicio = ejerciciosList.find { it.id == ejercicioId } ?: return
    var isLoading by remember { mutableStateOf(true) }
    var showVideo by remember { mutableStateOf(true) }
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

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
            .background(Brush.verticalGradient(gradientColors))
    ) {
        if (isLoading) {
            // Indicador de carga centrado con diseño mejorado
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando ejercicio...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        } else {
            // Contenido del ejercicio
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp,  top = 40.dp,  bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    // 🏷 Título con diseño mejorado
                    Spacer(modifier = Modifier.height(30.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        )
                    ) {
                        Text(
                            text = ejercicio.titulo,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 8f
                                )
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🎥 Video o imagen con diseño mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            if (showVideo) {
                                AndroidView(
                                    factory = { context ->
                                        videoView.apply {
                                            setVideoURI(Uri.parse("android.resource://${context.packageName}/${ejercicio.videoRes}"))
                                            setOnPreparedListener { it.isLooping = true; start() }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(16.dp)
                                        )
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.fotoblanca),
                                    contentDescription = "Imagen Blanca",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(16.dp)
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 📄 Descripción con diseño mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = ejercicio.descripcion,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 🔙 Botón volver con diseño mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        Button(
                            onClick = {
                                showVideo = false
                                navController.navigate("Ejercicios")
                            },
                            modifier = Modifier
                                .fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                "Volver",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}