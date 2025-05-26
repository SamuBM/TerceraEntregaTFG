import android.net.Uri
import android.os.CountDownTimer
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.tfg.pantallas.RutinaState
import java.util.Calendar

@Composable
fun mostrarEjerciciosConCrono(
    navController: NavController,
    rutinaState: RutinaState,
    tiempoEjercicio: Int = 30,
    tiempoDescanso: Int = 30
) {
    val diasSemana =
        listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val calendar = Calendar.getInstance()
    val diaIndexActual = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7  // Lunes = 0, Domingo = 6

    // Verificar si viene de la rutina diaria
    val vieneDesdeDiaria =
        navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("rutinaDiaria")
            ?: false

    // Limpiar la bandera después de usarla
    LaunchedEffect(Unit) {
        navController.previousBackStackEntry?.savedStateHandle?.remove<Boolean>("rutinaDiaria")
    }

    // Lista de ejercicios a mostrar basada en la fuente (diaria o semanal)
    val rutinaDelDia = remember {
        rutinaState.rutina[diaIndexActual].filterNotNull()
    }

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
            val videoUri =
                Uri.parse("android.resource://${navController.context.packageName}/${rutinaDelDia[ejercicioActual].videoRes}")
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 16.dp, end = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título que muestra de dónde proviene la rutina
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (vieneDesdeDiaria)
                        "Rutina para hoy (${diasSemana[diaIndexActual]})"
                    else
                        "Rutina del ${diasSemana[diaIndexActual]}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            // Cuadro de video o imagen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp),
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
                            Image(
                                painter = painterResource(id = rutinaDelDia[ejercicioActual].imagenRes),
                                contentDescription = rutinaDelDia[ejercicioActual].titulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit  // <- Cambiado a Fit para evitar recortes
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
                                fontSize = 32.sp,
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
                                fontSize = 32.sp,
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
                                fontSize = 20.sp
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
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(16.dp)
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Estado (Ejercicio o Descanso)
                    Text(
                        text = when {
                            rutinaCompletada -> "¡RUTINA COMPLETADA!"
                            esDescanso -> "DESCANSO"
                            else -> "EJERCICIO"
                        },
                        fontSize = 24.sp,
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
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            rutinaCompletada -> Color(0xFFFF9800)
                            esDescanso -> Color(0xFF1976D2)
                            else -> Color(0xFF388E3C)
                        }
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Arreglado: Cálculo correcto del progreso
                        val progress = when {
                            rutinaCompletada -> 1f
                            else -> ((ejercicioActual * 2) + (if (esDescanso) 1 else 0)).toFloat() / (rutinaDelDia.size * 2 - 1).coerceAtLeast(1)
                        }

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = when {
                                rutinaCompletada -> Color(0xFFFF9800)
                                esDescanso -> Color(0xFF2196F3)
                                else -> Color(0xFF4CAF50)
                            },
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Text(
                            text = "Progreso: ${ejercicioActual + 1}/${rutinaDelDia.size} ejercicios",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Controles
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                        Text(
                            if (estaCorriendo) "Pausar" else "Iniciar",
                            fontWeight = FontWeight.Bold
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
                        Text("Reiniciar", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { siguienteEjercicio() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Siguiente", fontWeight = FontWeight.Bold)
                    }
                }
            }

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

                    // Añadir información sobre el tipo de rutina
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "rutinaSemanal",
                            true
                        )

                    // Navegar al historial
                    navController.navigate("Historial")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "GUARDAR Y SALIR",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}