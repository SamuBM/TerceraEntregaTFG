package com.example.tfg.pantallas

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RutinaState {
    // Lista que almacena la rutina semanal (7 días)
    val rutina = mutableStateListOf<MutableList<Ejercicio?>>().apply {
        repeat(7) { add(mutableStateListOf()) }
    }

    var ultimaPosicionSeleccionada: Pair<Int, Int>? = null
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    // Referencia a Firestore
    private val db = Firebase.firestore

    // Email del usuario actual
    private var userEmail: String = ""

    // Inicializar userEmail desde SharedPreferences
    fun inicializarUserEmail(context: Context) {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        userEmail = sharedPref.getString("userEmail", "") ?: ""

        if (userEmail.isEmpty()) {
            Log.e("RutinaState", "No se encontró email de usuario en SharedPreferences")
            errorMessage = "Error: No se pudo identificar al usuario actual"
        } else {
            Log.d("RutinaState", "Email de usuario cargado: $userEmail")
        }
    }

    fun obtenerRutinaFiltrada(): List<List<Ejercicio>> {
        return rutina.map { dia ->
            dia.filterNotNull()
        }
    }

    // Función para eliminar un ejercicio localmente
    fun eliminarEjercicio(diaIndex: Int, index: Int) {
        try {
            if (index < rutina[diaIndex].size) {
                rutina[diaIndex].removeAt(index)

                // Forzar la actualización creando una nueva lista
                val nuevaLista = rutina[diaIndex].toMutableList()
                rutina[diaIndex] = nuevaLista.toMutableStateList()
            }
        } catch (e: Exception) {
            // Capturamos cualquier excepción para evitar crashes
            Log.e("RutinaState", "Error al eliminar ejercicio: ${e.message}")
        }
    }

    // Función para cargar ejercicios desde Firebase
    fun cargarEjerciciosDesdeBD(contexto: Context) {
        // Asegurarnos de que tengamos el userEmail actual
        inicializarUserEmail(contexto)

        // Si no hay email de usuario, no podemos cargar datos
        if (userEmail.isEmpty()) {
            isLoading = false
            errorMessage = "No se pudo identificar el usuario. Inicie sesión nuevamente."
            return
        }

        isLoading = true
        errorMessage = null

        // Limpiar rutina actual
        rutina.forEach { it.clear() }

        // Referencia a la colección principal rutina_semanal filtrando solo por el email del usuario actual
        val rutinaRef = db.collection("rutina_semanal")
            .whereEqualTo("userEmail", userEmail)

        rutinaRef.get()
            .addOnSuccessListener { documents ->
                try {
                    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

                    // Crear una estructura temporal para organizar por día y posición
                    val ejerciciosPorDia = mutableMapOf<Int, MutableList<Pair<Int, Ejercicio>>>()

                    for (document in documents) {
                        val ejercicioData = document.data
                        val dia = ejercicioData["dia"] as? String ?: continue
                        val posicion = (ejercicioData["posicion"] as? Long)?.toInt() ?: 0
                        val diaIndex = diasSemana.indexOf(dia)

                        if (diaIndex != -1) {
                            val nuevoEjercicio = Ejercicio(
                                id = document.id.hashCode(), // Usamos el hashCode del ID del documento como ID del ejercicio
                                titulo = ejercicioData["titulo"] as String,
                                descripcion = ejercicioData["descripcion"] as String,
                                imagenRes = (ejercicioData["imagenRes"] as Long).toInt(),
                                videoRes = (ejercicioData["videoRes"] as Long).toInt(),
                                documentId = document.id // Guardamos el ID del documento para futuras operaciones
                            )

                            // Añadir a la estructura temporal
                            if (!ejerciciosPorDia.containsKey(diaIndex)) {
                                ejerciciosPorDia[diaIndex] = mutableListOf()
                            }
                            ejerciciosPorDia[diaIndex]!!.add(Pair(posicion, nuevoEjercicio))
                        }
                    }

                    // Ahora organizar por posición y añadir a rutina
                    for ((diaIndex, ejerciciosDelDia) in ejerciciosPorDia) {
                        // Ordenar por posición
                        val ejerciciosOrdenados = ejerciciosDelDia.sortedBy { it.first }

                        // Limpiar el día actual
                        rutina[diaIndex].clear()

                        // Añadir ejercicios en orden
                        for ((_, ejercicio) in ejerciciosOrdenados) {
                            rutina[diaIndex].add(ejercicio)
                        }
                    }

                    Log.d("RutinaState", "Datos cargados correctamente: ${documents.size()} ejercicios para el usuario $userEmail")
                } catch (e: Exception) {
                    errorMessage = "Error al procesar datos: ${e.message}"
                    Log.e("RutinaState", "Error al procesar datos: ${e.message}")
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error de conexión: ${e.message}"
                Log.e("RutinaState", "Error al obtener datos de Firestore: ${e.message}")
                isLoading = false
            }
    }

    // Función para guardar un ejercicio en Firebase
    fun guardarEjercicioEnBD(contexto: Context, diaIndex: Int, ejercicio: Ejercicio, posicion: Int, onComplete: (Boolean, String) -> Unit) {
        // Asegurarnos de que tengamos el userEmail actual
        if (userEmail.isEmpty()) {
            inicializarUserEmail(contexto)
        }

        // Si sigue vacío, no podemos guardar
        if (userEmail.isEmpty()) {
            onComplete(false, "")
            return
        }

        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val dia = diasSemana[diaIndex]

        // Crear un mapa con los datos del ejercicio
        val ejercicioMap = hashMapOf(
            "userEmail" to userEmail,  // Usamos el email del usuario en lugar del ID
            "dia" to dia,
            "posicion" to posicion,  // NUEVO: Guardamos la posición
            "titulo" to ejercicio.titulo,
            "descripcion" to ejercicio.descripcion,
            "imagenRes" to ejercicio.imagenRes,
            "videoRes" to ejercicio.videoRes
        )

        // Referencia a la colección principal de rutina_semanal
        db.collection("rutina_semanal")
            .add(ejercicioMap)
            .addOnSuccessListener { documentReference ->
                Log.d("RutinaState", "Ejercicio guardado con ID: ${documentReference.id} para el usuario $userEmail en posición $posicion")
                onComplete(true, documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.e("RutinaState", "Error al guardar ejercicio: ${e.message}")
                onComplete(false, "")
            }
    }

    // Función para actualizar un ejercicio en Firebase
    fun actualizarEjercicioEnBD(contexto: Context, ejercicio: Ejercicio, diaIndex: Int, posicion: Int, onComplete: (Boolean) -> Unit) {
        // Verificar que tengamos userEmail
        if (userEmail.isEmpty()) {
            inicializarUserEmail(contexto)
        }

        // Si sigue vacío, no podemos actualizar
        if (userEmail.isEmpty()) {
            onComplete(false)
            return
        }

        // Verificar que el ejercicio tenga un documentId
        if (ejercicio.documentId.isNullOrEmpty()) {
            Log.e("RutinaState", "Error: No se puede actualizar un ejercicio sin documentId")
            onComplete(false)
            return
        }

        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val dia = diasSemana[diaIndex]

        // Crear un mapa con los datos actualizados
        val ejercicioMap = hashMapOf(
            "userEmail" to userEmail,  // Mantenemos el mismo email
            "dia" to dia,
            "posicion" to posicion,  // NUEVO: Actualizamos también la posición
            "titulo" to ejercicio.titulo,
            "descripcion" to ejercicio.descripcion,
            "imagenRes" to ejercicio.imagenRes,
            "videoRes" to ejercicio.videoRes
        )

        // Actualizar el documento en Firestore
        db.collection("rutina_semanal")
            .document(ejercicio.documentId!!)
            .update(ejercicioMap as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("RutinaState", "Ejercicio actualizado correctamente para el usuario $userEmail en posición $posicion")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("RutinaState", "Error al actualizar ejercicio: ${e.message}")
                onComplete(false)
            }
    }

    // Función para eliminar un ejercicio de Firebase
    fun eliminarEjercicioDeBD(contexto: Context, ejercicio: Ejercicio, onComplete: (Boolean) -> Unit) {
        // Verificar que tengamos userEmail
        if (userEmail.isEmpty()) {
            inicializarUserEmail(contexto)
        }

        // Si sigue vacío, no podemos eliminar
        if (userEmail.isEmpty()) {
            onComplete(false)
            return
        }

        // Verificar que el ejercicio tenga un documentId
        if (ejercicio.documentId.isNullOrEmpty()) {
            Log.e("RutinaState", "Error: No se puede eliminar un ejercicio sin documentId")
            onComplete(false)
            return
        }

        // Eliminar el documento de Firestore
        db.collection("rutina_semanal")
            .document(ejercicio.documentId!!)
            .delete()
            .addOnSuccessListener {
                Log.d("RutinaState", "Ejercicio eliminado correctamente para el usuario $userEmail")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("RutinaState", "Error al eliminar ejercicio: ${e.message}")
                onComplete(false)
            }
    }
}

@Composable
fun mostrarRutinaSemanal(
    navController: NavController,
    rutinaState: RutinaState = remember { RutinaState() }
) {
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val contexto = LocalContext.current
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))
    // Cargar datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        rutinaState.cargarEjerciciosDesdeBD(contexto)
    }

    // Obtener los tiempos guardados en PantallaTiempo
    val tiempoEjercicio = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoEjercicio") ?: 30
    val tiempoDescanso = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoDescanso") ?: 30

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))) {
        // Mostrar cargando si es necesario
        if (rutinaState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando rutina...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            // Título en la parte superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 32.dp) // Separar el título de la parte superior
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "RUTINA SEMANAL",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                        color = Color(0xFF1976D2)
                    )
                }

                // Mostrar mensaje de error si existe
                rutinaState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Contenido de la rutina (LazyColumn)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 230.dp) // Deja espacio para el botón de empezar
                ) {
                    itemsIndexed(diasSemana) { diaIndex, dia ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = dia,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(rutinaState.rutina[diaIndex]) { index, ejercicio ->
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
                                                        rutinaState.ultimaPosicionSeleccionada = diaIndex to index
                                                        navController.navigate("EjerciciosSeleccion")
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("?", fontSize = 32.sp, color = Color.Gray)
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .size(30.dp)
                                                        .background(
                                                            color = Color.White.copy(alpha = 0.8f),
                                                            shape = RoundedCornerShape(50)
                                                        )
                                                        .clickable {
                                                            rutinaState.eliminarEjercicio(diaIndex, index)
                                                        }
                                                        .padding(2.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "X",
                                                        color = Color.Red,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 20.sp
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Image(
                                                    painter = painterResource(id = ejercicio.imagenRes),
                                                    contentDescription = ejercicio.titulo,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(4.dp) // Añade un pequeño padding para evitar que toque los bordes
                                                        .clickable {
                                                            rutinaState.ultimaPosicionSeleccionada = diaIndex to index
                                                            navController.navigate("EjerciciosSeleccion")
                                                        },
                                                    contentScale = ContentScale.Fit // Muestra la imagen completa
                                                )

                                                // Botón para eliminar ejercicios
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .size(30.dp)
                                                        .background(
                                                            color = Color.White.copy(alpha = 0.8f),
                                                            shape = RoundedCornerShape(50)
                                                        )
                                                        .clickable {
                                                            // Eliminar de Firebase y luego localmente
                                                            ejercicio?.let { ej ->
                                                                rutinaState.eliminarEjercicioDeBD(contexto, ej) { exito ->
                                                                    if (exito) {
                                                                        // Después eliminar localmente
                                                                        rutinaState.eliminarEjercicio(diaIndex, index)
                                                                    } else {
                                                                        Toast.makeText(contexto, "Error al eliminar ejercicio", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        .padding(2.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "X",
                                                        color = Color.Red,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 20.sp
                                                    )
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
                                                val indexNuevo = rutinaState.rutina[diaIndex].size
                                                rutinaState.rutina[diaIndex].add(null)
                                                rutinaState.ultimaPosicionSeleccionada = diaIndex to indexNuevo
                                                navController.navigate("EjerciciosSeleccion")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón Empezar en la parte inferior de la pantalla
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp, start = 16.dp, end = 16.dp)
            ) {
                Button(
                    onClick = {
                        // Usar los tiempos para navegar a la pantalla del cronómetro
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

            // Mantener el navegador en la parte inferior de la pantalla
            mostrarNavegador(navController, "Home")
        }
    }
}