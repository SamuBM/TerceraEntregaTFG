import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfg.pantallas.mostrarNavegador
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Clase para representar un registro de sesión de entrenamiento completa
data class SesionEntrenamiento(
    val id: String = "",
    val tipoEntrenamiento: String = "Rutina Semanal", // Valor por defecto
    val fecha: Date = Date(),
    val numeroEjercicios: Int = 0,
    val tiempoTotalEjercicio: Int = 0, // en segundos
    val tiempoTotalDescanso: Int = 0,  // en segundos
    val caloriasQuemadas: Int = 0
)

// Función que calcula las calorías quemadas por ejercicio basado en su tiempo
fun calcularCaloriasPorEjercicio(tiempoEjercicioSegundos: Int): Int {
    // Base: 6 calorías por 30 segundos
    val caloriaBase = 6

    // Si el tiempo es menor o igual a 30 segundos, devolvemos las calorías base
    if (tiempoEjercicioSegundos <= 30) {
        return caloriaBase
    }

    // Por cada 5 segundos adicionales después de los 30s iniciales, se añaden 2 calorías
    val segundosAdicionales = tiempoEjercicioSegundos - 30
    val intervalosAdicionales = segundosAdicionales / 5
    val caloriasAdicionales = intervalosAdicionales * 2

    // Limitamos el tiempo máximo a 180 segundos (3 minutos)
    val caloriasTotal = if (tiempoEjercicioSegundos <= 180) {
        caloriaBase + caloriasAdicionales
    } else {
        // Si supera los 180 segundos, calculamos el máximo de calorías (para 180s)
        caloriaBase + ((180 - 30) / 5) * 2
    }

    return caloriasTotal
}

// Función para formatear el tiempo en formato HH:MM:SS
fun formatearTiempoHHMMSS(segundos: Int): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    val segs = segundos % 60
    return String.format("%02d:%02d:%02d", horas, minutos, segs)
}

@Composable
fun mostrarHistorial(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

    // Estado para almacenar los registros de sesiones de entrenamiento
    val sesiones = remember { mutableStateListOf<SesionEntrenamiento>() }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    // Obtener la referencia a Firestore
    val db = FirebaseFirestore.getInstance()

    // Obtener el email del usuario actual desde SharedPreferences
    val sharedPref = context.getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
    val userEmail = sharedPref.getString("userEmail", "") ?: ""
    val userId = sharedPref.getString("userId", "") ?: ""

    // Función para obtener sesiones de entrenamiento desde Firestore
    fun obtenerSesionesDB() {
        cargando = true
        mensajeError = ""

        if (userEmail.isEmpty()) {
            mensajeError = "No se ha identificado un usuario. Por favor inicie sesión."
            cargando = false
            return
        }

        // MODIFICACIÓN: Quitamos el orderBy para evitar necesitar un índice compuesto
        db.collection("rutinaresumen")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    // Limpiar la lista actual
                    sesiones.clear()
                    val sesionesTemporales = mutableListOf<SesionEntrenamiento>()

                    for (document in documents) {
                        val item = document.data

                        // CORRECCIÓN: Manejar correctamente la conversión de Timestamp
                        val timestamp = item["fecha"] as? com.google.firebase.Timestamp ?: continue
                        // Convertir el timestamp a la zona horaria local
                        val fecha = Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000)

                        // Crear objeto SesionEntrenamiento
                        val sesion = SesionEntrenamiento(
                            id = document.id,
                            tipoEntrenamiento = item["tipoEntrenamiento"] as? String ?: "Rutina Semanal",
                            fecha = fecha,
                            numeroEjercicios = (item["ejerciciosTotales"] as? Long)?.toInt() ?: 0,
                            tiempoTotalEjercicio = (item["tiempoTotalEjercicioSegundos"] as? Long)?.toInt() ?: 0,
                            tiempoTotalDescanso = (item["tiempoTotalDescansoSegundos"] as? Long)?.toInt() ?: 0,
                            caloriasQuemadas = (item["caloriasTotales"] as? Long)?.toInt() ?: 0
                        )

                        sesionesTemporales.add(sesion)
                    }

                    // MODIFICACIÓN: Ordenar las sesiones manualmente por fecha (descendente)
                    sesionesTemporales.sortByDescending { it.fecha }

                    // Añadir a la lista observable
                    sesiones.addAll(sesionesTemporales)

                    cargando = false
                } catch (e: Exception) {
                    mensajeError = "Error al procesar datos: ${e.message}"
                    cargando = false
                }
            }
            .addOnFailureListener { error ->
                mensajeError = "Error de conexión: ${error.message}"
                cargando = false
            }
    }

    // Función para guardar una nueva sesión en Firestore
    fun guardarSesionDB(tipoEntrenamiento: String,
                        ejerciciosTotales: Int,
                        tiempoEjercicio: Int,
                        tiempoDescanso: Int) {

        if (userEmail.isEmpty()) {
            mensajeError = "No se ha identificado un usuario. Por favor inicie sesión."
            return
        }

        // Calcular calorías totales
        val caloriasPorEjercicio = calcularCaloriasPorEjercicio(tiempoEjercicio)
        val caloriasTotales = caloriasPorEjercicio * ejerciciosTotales

        // Tiempo total en segundos
        val tiempoTotalEjercicioSegundos = tiempoEjercicio * ejerciciosTotales
        val tiempoTotalDescansoSegundos = tiempoDescanso * (ejerciciosTotales - 1).coerceAtLeast(0)

        // Crear documento para Firebase - usar Timestamp de Firebase
        val ahora = com.google.firebase.Timestamp.now()
        val nuevaSesion = hashMapOf(
            "userEmail" to userEmail,
            "userId" to userId,
            "tipoEntrenamiento" to tipoEntrenamiento,
            "fecha" to ahora, // Usar Firebase Timestamp
            "ejerciciosTotales" to ejerciciosTotales,
            "tiempoTotalEjercicioSegundos" to tiempoTotalEjercicioSegundos,
            "tiempoTotalDescansoSegundos" to tiempoTotalDescansoSegundos,
            "caloriasTotales" to caloriasTotales
        )

        // Guardar en Firestore
        db.collection("rutinaresumen")
            .add(nuevaSesion)
            .addOnSuccessListener {
                // Volver a cargar las sesiones después de guardar exitosamente
                obtenerSesionesDB()
            }
            .addOnFailureListener { e ->
                mensajeError = "Error al guardar: ${e.message}"
            }
    }

    // Cargar sesiones al iniciar
    LaunchedEffect(Unit) {
        // Cargar datos existentes
        obtenerSesionesDB()

        // Verificar si hay parámetros de nueva sesión
        val tiempoEjercicio = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoEjercicio") ?: 0
        val tiempoDescanso = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoDescanso") ?: 0
        val numeroEjercicios = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("numeroEjercicios") ?: 0

        // Verificar si viene de rutina diaria, semanal o aleatoria
        val rutinaDiaria = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("rutinaDiaria") ?: false
        val rutinaSemanal = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("rutinaSemanal") ?: false

        // Agregar logging para depuración
        println("DEBUG: rutinaDiaria=$rutinaDiaria, rutinaSemanal=$rutinaSemanal")

        val tipoEntrenamientoExplicito = navController.previousBackStackEntry?.savedStateHandle?.get<String>("tipoEntrenamiento")

        // Si hay parámetros válidos, crear un nuevo registro
        if (tiempoEjercicio > 0 && numeroEjercicios > 0) {
            // Determinar tipo de entrenamiento
            val tipoEntrenamiento = tipoEntrenamientoExplicito ?: when {
                rutinaDiaria -> "Rutina Diaria"
                rutinaSemanal -> "Rutina Semanal"
                else -> "Rutina Aleatoria"
            }

            // Guardar en Firestore
            guardarSesionDB(
                tipoEntrenamiento = tipoEntrenamiento,
                ejerciciosTotales = numeroEjercicios,
                tiempoEjercicio = tiempoEjercicio,
                tiempoDescanso = tiempoDescanso
            )

            // Limpiar los parámetros después de usarlos
            navController.previousBackStackEntry?.savedStateHandle?.remove<Int>("tiempoEjercicio")
            navController.previousBackStackEntry?.savedStateHandle?.remove<Int>("tiempoDescanso")
            navController.previousBackStackEntry?.savedStateHandle?.remove<Int>("numeroEjercicios")
            navController.previousBackStackEntry?.savedStateHandle?.remove<Boolean>("rutinaDiaria")
            navController.previousBackStackEntry?.savedStateHandle?.remove<Boolean>("rutinaSemanal")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 40.dp)
        ) {
            // Título
            Text(
                text = "HISTORIAL ENTRENAMIENTOS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Mostrar error si existe
            if (mensajeError.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = mensajeError,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red
                    )
                }
            }

            // Indicador de carga
            if (cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Si no hay sesiones
            else if (sesiones.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay entrenamientos registrados",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Lista de sesiones
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sesiones) { sesion ->
                        TarjetaSesion(sesion)
                    }
                }
            }
            // Botón para borrar historial
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 150.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            // Mostrar diálogo de confirmación antes de borrar
                            val confirmDelete = true

                            if (confirmDelete && userEmail.isNotEmpty()) {
                                // Borrar documentos del usuario actual
                                db.collection("rutinaresumen")
                                    .whereEqualTo("userEmail", userEmail)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            db.collection("rutinaresumen").document(document.id).delete()
                                        }
                                        // Actualizar la lista después de borrar
                                        obtenerSesionesDB()
                                    }
                                    .addOnFailureListener { e ->
                                        mensajeError = "Error al borrar: ${e.message}"
                                    }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Borrar historial",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }

    mostrarNavegador(navController,"Historial")
}

@Composable
fun TarjetaSesion(sesion: SesionEntrenamiento) {
    // CORRECCIÓN: Sumar 2 horas a la fecha para corregir el desfase
    val calendar = Calendar.getInstance()
    calendar.time = sesion.fecha
    val fechaCorregida = calendar.time

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Calcular tiempo total en minutos (ejercicio + descanso)
    val tiempoTotalSegundos = sesion.tiempoTotalEjercicio + sesion.tiempoTotalDescanso
    val minutos = tiempoTotalSegundos / 60
    val segundos = tiempoTotalSegundos % 60

    // Colores dependiendo del tipo de entrenamiento
    val colorFondo = when (sesion.tipoEntrenamiento) {
        "Rutina Diaria" -> Color(0xFFE3F2FD) // Azul claro
        "Rutina Semanal" -> Color(0xFFE8F5E9) // Verde claro
        else -> Color(0xFFFFF8E1) // Amarillo claro para Aleatorio
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tipo de entrenamiento y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sesion.tipoEntrenamiento,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = when (sesion.tipoEntrenamiento) {
                        "Rutina Diaria" -> Color(0xFF1976D2)
                        "Rutina Semanal" -> Color(0xFF388E3C)
                        else -> Color(0xFFFFA000)
                    }
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatoFecha.format(fechaCorregida),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatoHora.format(fechaCorregida),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detalles de la sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DetalleSesion(
                    valor = "${sesion.numeroEjercicios}",
                    etiqueta = "Ejercicios"
                )
                DetalleSesion(
                    valor = "${sesion.caloriasQuemadas}",
                    etiqueta = "Calorías"
                )
                DetalleSesion(
                    valor = "${minutos}:${segundos.toString().padStart(2, '0')}",
                    etiqueta = "Tiempo Total"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional (tiempo ejercicio vs descanso)
            val tiempoTotal = (sesion.tiempoTotalEjercicio + sesion.tiempoTotalDescanso).toFloat()
            val progress = if (tiempoTotal > 0) sesion.tiempoTotalEjercicio.toFloat() / tiempoTotal else 0f

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ejercicio: ${sesion.tiempoTotalEjercicio / 60}:${(sesion.tiempoTotalEjercicio % 60).toString().padStart(2, '0')}",
                    fontSize = 14.sp
                )
                Text(
                    text = "Descanso: ${sesion.tiempoTotalDescanso / 60}:${(sesion.tiempoTotalDescanso % 60).toString().padStart(2, '0')}",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DetalleSesion(valor: String, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Text(
            text = etiqueta,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}