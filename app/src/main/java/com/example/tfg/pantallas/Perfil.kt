package com.example.tfg.pantallas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarPerfil(navController: NavController) {
    val contexto = LocalContext.current
    val sharedPref = contexto.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val userEmail = sharedPref.getString("userEmail", "") ?: ""

    var isLoading by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var mostrarDialogoBorrarCuenta by remember { mutableStateOf(false) }

    // Colores del gradiente para mantener consistencia con el login
    val gradientColors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(gradientColors)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Icono de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(60.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar email del usuario
            Text(
                text = userEmail,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Mi Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card para cerrar sesión
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                onClick = { mostrarDialogoCerrarSesion = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card para borrar cuenta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                onClick = { mostrarDialogoBorrarCuenta = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar Cuenta",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Borrar Cuenta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensajeError,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

        }
        mostrarNavegador(navController,"Perfil")
    }

    // Diálogo para confirmar cierre de sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        cerrarSesion(contexto, navController)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar borrado de cuenta
    if (mostrarDialogoBorrarCuenta) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrarCuenta = false },
            title = { Text("Borrar Cuenta") },
            text = {
                Text("¿Estás seguro de que deseas borrar tu cuenta? Esta acción no se puede deshacer y perderás todos tus datos.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoBorrarCuenta = false
                        isLoading = true
                        borrarCuenta(
                            contexto = contexto,
                            navController = navController,
                            actualizarMensaje = { mensaje ->
                                mensajeError = mensaje
                                isLoading = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Borrar Cuenta")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoBorrarCuenta = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun cerrarSesion(contexto: Context, navController: NavController) {
    // Limpiamos los datos de sesión
    val sharedPref = contexto.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        clear()
        apply()
    }

    // Navegar a la pantalla de login
    navController.navigate("LogIn") {
        popUpTo(0) { inclusive = true }
    }
}

fun borrarCuenta(
    contexto: Context,
    navController: NavController,
    actualizarMensaje: (String) -> Unit
) {
    val sharedPref = contexto.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val userEmail = sharedPref.getString("userEmail", "") ?: ""
    val userId = sharedPref.getString("userId", "") ?: ""

    if (userEmail.isEmpty()) {
        actualizarMensaje("No se encontró información del usuario")
        return
    }

    val db = FirebaseFirestore.getInstance()

    // Crear una función para eliminar documentos de una colección específica
    fun eliminarDocumentosDeColeccion(coleccion: String, campo: String, valor: String, callback: (Boolean) -> Unit) {
        db.collection(coleccion)
            .whereEqualTo(campo, valor)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    callback(true)
                    return@addOnSuccessListener
                }

                // Contador para saber cuándo se han eliminado todos los documentos
                var contadorExito = 0
                val totalDocumentos = documentos.size()

                for (documento in documentos) {
                    db.collection(coleccion)
                        .document(documento.id)
                        .delete()
                        .addOnSuccessListener {
                            contadorExito++
                            if (contadorExito == totalDocumentos) {
                                callback(true)
                            }
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // 1. Eliminar documentos de la colección usuarios
    eliminarDocumentosDeColeccion("usuarios", "email", userEmail) { exitoUsuarios ->
        if (exitoUsuarios) {
            // 2. Eliminar documentos de la colección rutina_semanal
            eliminarDocumentosDeColeccion("rutina_semanal", "userEmail", userEmail) { exitoRutinaSemanal ->
                if (exitoRutinaSemanal) {
                    // 3. Eliminar documentos de la colección rutinaresumen
                    eliminarDocumentosDeColeccion("rutinaresumen", "userEmail", userEmail) { exitoRutinaResumen ->
                        if (exitoRutinaResumen) {
                            // Limpiar los datos de sesión
                            with(sharedPref.edit()) {
                                clear()
                                apply()
                            }

                            // Navegar a la pantalla de login
                            actualizarMensaje("Cuenta eliminada con éxito")
                            navController.navigate("LogIn") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            actualizarMensaje("Error al eliminar datos de rutinaresumen")
                        }
                    }
                } else {
                    actualizarMensaje("Error al eliminar datos de rutina_semanal")
                }
            }
        } else {
            actualizarMensaje("Error al eliminar usuario")
        }
    }
}