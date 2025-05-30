package com.example.tfg.pantallas

import android.net.Uri
import android.os.CountDownTimer
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun mostrarEjerciciosConCronoDiario(
    navController: NavController,
    rutinaDiariaState: RutinaDiariaState,
    tiempoEjercicio: Int = 30,
    tiempoDescanso: Int = 30
) {
    // Obtener configuración de pantalla para responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val isTallScreen = screenHeight > 800.dp

    // Definir tamaños responsive para UI
    val topPadding = if (isTallScreen) 40.dp else 24.dp
    val sidePadding = if (isSmallScreen) 12.dp else 16.dp
    val cardSpacing = if (isSmallScreen) 12.dp else 16.dp
    val videoHeight = (screenHeight * 0.25f).coerceIn(180.dp, 280.dp)
    val buttonPadding = if (isSmallScreen) 6.dp else 8.dp
    val cardPadding = if (isSmallScreen) 12.dp else 16.dp
    val verticalSpacing = if (isSmallScreen) 6.dp else 8.dp

    // Definir tamaños responsive para texto
    val titleFontSize = if (isSmallScreen) 16.sp else 18.sp
    val exerciseTitleFontSize = if (isSmallScreen) 18.sp else 22.sp
    val statusFontSize = if (isSmallScreen) 20.sp else 24.sp
    val timerFontSize = if (isSmallScreen) 45.sp else 60.sp
    val videoStatusFontSize = if (isSmallScreen) 24.sp else 32.sp
    val noExerciseFontSize = if (isSmallScreen) 16.sp else 20.sp
    val progressFontSize = if (isSmallScreen) 14.sp else 16.sp
    val buttonFontSize = if (isSmallScreen) 14.sp else 16.sp
    val exitButtonFontSize = if (isSmallScreen) 12.sp else 14.sp

    // Filtrar todos los ejercicios no nulos de la rutina diaria
    val rutinaDelDia = remember { rutinaDiariaState.rutina.filterNotNull() }

    // Estados para controlar el cronómetro y los ejercicios
    var ejercicioActual by remember { mutableStateOf(0) }
    var tiempoRestante by remember { mutableStateOf(tiempoEjercicio) }
    var estaCorriendo by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    var esDescanso by remember { mutableStateOf(false) }
    var rutinaCompletada by remember { mutableStateOf(false) }

    // VideoView para manejar la reproducción del video
    val videoView = remember { VideoView(navController.context) }

    // Detener el video cuando la pantalla sea destruida
    DisposableEffect(Unit) {
        onDispose {
            videoView.stopPlayback()
            timer?.cancel()
        }
    }

    // Función para iniciar el cronómetro definida como función local
    fun iniciarTimer() {
        timer?.cancel()

        timer = object : CountDownTimer(tiempoRestante * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                tiempoRestante = 0

                if (esDescanso) {
                    // Si estábamos en descanso, ahora pasamos al siguiente ejercicio
                    if (ejercicioActual < rutinaDelDia.lastIndex) {
                        ejercicioActual++
                        esDescanso = false
                        tiempoRestante = tiempoEjercicio
                        iniciarTimer()
                    } else {
                        // Terminamos todos los ejercicios
                        estaCorriendo = false
                        rutinaCompletada = true
                        videoView.stopPlayback()
                    }
                } else {
                    // Si estábamos en ejercicio, ahora pasamos a descanso
                    esDescanso = true
                    tiempoRestante = tiempoDescanso
                    iniciarTimer()
                }
            }
        }.start()
    }

    // Función para cambiar al siguiente ejercicio
    fun siguienteEjercicio() {
        timer?.cancel()
        if (ejercicioActual < rutinaDelDia.lastIndex || esDescanso) {
            if (esDescanso) {
                // Si estamos en descanso, pasamos al siguiente ejercicio
                ejercicioActual++
                esDescanso = false
                tiempoRestante = tiempoEjercicio
            } else {
                // Si estamos en ejercicio, pasamos a descanso
                esDescanso = true
                tiempoRestante = tiempoDescanso
            }

            videoView.stopPlayback()
            if (estaCorriendo) {
                iniciarTimer()
            }
        } else {
            // Último ejercicio, mostramos fin
            timer?.cancel()
            estaCorriendo = false
            rutinaCompletada = true
            videoView.stopPlayback()
        }
    }

    // LaunchedEffect para manejar la pausa/reinicio del timer y el video
    LaunchedEffect(estaCorriendo, ejercicioActual, esDescanso) {
        if (estaCorriendo && rutinaDelDia.isNotEmpty() && !esDescanso) {
            iniciarTimer()
            val videoUri = Uri.parse("android.resource://${navController.context.packageName}/${rutinaDelDia[ejercicioActual].videoRes}")
            videoView.setVideoURI(videoUri)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true  // Configurar el video para reproducirse en bucle
                // Ajustar el video para que se vea igual que la imagen (sin bordes oscuros)
                mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            }
            videoView.start()
        } else if (estaCorriendo && esDescanso) {
            // Solo iniciamos el timer durante el descanso, sin video
            iniciarTimer()
            videoView.stopPlayback()
        } else {
            timer?.cancel()
            videoView.pause()
        }
    }

    // Colores para gradientes
    val ejercicioGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
    )
    val descansoGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
    )
    val completadoGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFF9800), Color(0xFFFFEB3B))
    )
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(top = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    bottom = 30.dp,
                    end = sidePadding,
                    start = sidePadding
                ),
            verticalArrangement = Arrangement.spacedBy(cardSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título que muestra la rutina diaria
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Rutina Diaria",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = titleFontSize
                    ),
                    modifier = Modifier
                        .padding(cardPadding)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
            }

            // Cuadro de video o imagen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(videoHeight),
                shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (rutinaDelDia.isNotEmpty() && !esDescanso) {
                        if (estaCorriendo) {
                            AndroidView(
                                factory = { context ->
                                    videoView.apply {
                                        layoutParams = android.view.ViewGroup.LayoutParams(
                                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    }
                                    videoView
                                },
                                update = { view ->
                                    view.layoutParams = android.view.ViewGroup.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Si el cronómetro no ha comenzado, mostramos la imagen
                            Image(
                                painter = painterResource(id = rutinaDelDia[ejercicioActual].imagenRes),
                                contentDescription = rutinaDelDia[ejercicioActual].titulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else if (esDescanso) {
                        // Pantalla durante el descanso
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DESCANSO",
                                color = Color.White,
                                fontSize = videoStatusFontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (rutinaCompletada) {
                        // Pantalla cuando se completa la rutina
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "¡COMPLETADO!",
                                color = Color.White,
                                fontSize = videoStatusFontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF424242)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay ejercicios programados",
                                color = Color.White,
                                fontSize = noExerciseFontSize
                            )
                        }
                    }
                }
            }

            // Título del ejercicio (solo mostrar si no estamos en descanso)
            if (rutinaDelDia.isNotEmpty() && !esDescanso && !rutinaCompletada) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = rutinaDelDia[ejercicioActual].titulo,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = exerciseTitleFontSize
                        ),
                        modifier = Modifier
                            .padding(cardPadding)
                            .align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Cronómetro y estado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        rutinaCompletada -> Color(0xFFFFF9C4)
                        esDescanso -> Color(0xFFE3F2FD)
                        else -> Color(0xFFE8F5E9)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(cardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {
                    // Estado (Ejercicio o Descanso)
                    Text(
                        text = when {
                            rutinaCompletada -> "¡RUTINA COMPLETADA!"
                            esDescanso -> "DESCANSO"
                            else -> "EJERCICIO"
                        },
                        fontSize = statusFontSize,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            rutinaCompletada -> Color(0xFFFF9800)
                            esDescanso -> Color(0xFF2196F3)
                            else -> Color(0xFF4CAF50)
                        }
                    )

                    // Cronómetro
                    Text(
                        text = if (rutinaCompletada) "¡Bien hecho!" else if (tiempoRestante > 0) "$tiempoRestante s" else "¡Terminado!",
                        fontSize = timerFontSize,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            rutinaCompletada -> Color(0xFFFF9800)
                            esDescanso -> Color(0xFF1976D2)
                            else -> Color(0xFF388E3C)
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Progreso
            if (rutinaDelDia.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(cardPadding),
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                    ) {
                        // Cálculo correcto del progreso
                        val progress = when {
                            rutinaCompletada -> 1f
                            else -> ((ejercicioActual * 2) + (if (esDescanso) 1 else 0)).toFloat() / (rutinaDelDia.size * 2 - 1).coerceAtLeast(1)
                        }

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isSmallScreen) 10.dp else 12.dp)
                                .clip(RoundedCornerShape(if (isSmallScreen) 5.dp else 6.dp)),
                            color = when {
                                rutinaCompletada -> Color(0xFFFF9800)
                                esDescanso -> Color(0xFF2196F3)
                                else -> Color(0xFF4CAF50)
                            },
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Text(
                            text = "Progreso: ${ejercicioActual + 1}/${rutinaDelDia.size} ejercicios",
                            fontWeight = FontWeight.Medium,
                            fontSize = progressFontSize
                        )
                    }
                }
            }

            // Controles - Layout responsive para botones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isSmallScreen) {
                    // En pantallas pequeñas, usar layout vertical
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(cardPadding),
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                    ) {
                        Button(
                            onClick = {
                                estaCorriendo = !estaCorriendo
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (estaCorriendo) Color(0xFFF44336) else Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (estaCorriendo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (estaCorriendo) "Pausar" else "Iniciar",
                                tint = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 24.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(verticalSpacing)
                        ) {
                            Button(
                                onClick = {
                                    timer?.cancel()
                                    tiempoRestante = if (esDescanso) tiempoDescanso else tiempoEjercicio
                                    estaCorriendo = false
                                    videoView.stopPlayback()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reiniciar",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSmallScreen) 18.dp else 20.dp)
                                )
                            }

                            Button(
                                onClick = { siguienteEjercicio() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Siguiente",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSmallScreen) 18.dp else 20.dp)
                                )
                            }
                        }
                    }
                } else {
                    // En pantallas normales, usar layout horizontal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(cardPadding),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                estaCorriendo = !estaCorriendo
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (estaCorriendo) Color(0xFFF44336) else Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (estaCorriendo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (estaCorriendo) "Pausar" else "Iniciar",
                                tint = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                timer?.cancel()
                                tiempoRestante = if (esDescanso) tiempoDescanso else tiempoEjercicio
                                estaCorriendo = false
                                videoView.stopPlayback()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reiniciar",
                                tint = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { siguienteEjercicio() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Siguiente",
                                tint = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 22.dp)
                            )
                        }
                    }
                }
            }

            // Botón salir
            Button(
                onClick = {
                    // Guardar los datos para el historial antes de salir
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "tiempoEjercicio",
                        tiempoEjercicio
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "tiempoDescanso",
                        tiempoDescanso
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "numeroEjercicios",
                        rutinaDelDia.size
                    )

                    // Además de establecer rutinaDiaria como true
                    navController.currentBackStackEntry?.savedStateHandle?.set("rutinaDiaria", true)
                    // Añade esta línea también para ser más explícito:
                    navController.currentBackStackEntry?.savedStateHandle?.set("tipoEntrenamiento", "Rutina Diaria")

                    // Navegar al historial
                    navController.navigate("Historial")
                },
                enabled = rutinaCompletada,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = verticalSpacing),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rutinaCompletada) Color(0xFF9C27B0) else Color(0xFFBDBDBD),
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (rutinaCompletada) "GUARDAR Y SALIR" else "COMPLETA LA RUTINA PARA CONTINUAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = exitButtonFontSize,
                    color = if (rutinaCompletada) Color.White else Color(0xFF757575),
                    modifier = Modifier.padding(vertical = buttonPadding)
                )
            }
        }
    }
}