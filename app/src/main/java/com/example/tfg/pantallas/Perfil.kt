package com.example.tfg.pantallas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarPerfil(navController: NavController) {
    val contexto = LocalContext.current
    val configuration = LocalConfiguration.current
    val sharedPref = contexto.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val userEmail = sharedPref.getString("userEmail", "") ?: ""

    var isLoading by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var mostrarDialogoBorrarCuenta by remember { mutableStateOf(false) }

    // Responsive dimensions based on screen size
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight

    // Adaptive dimensions
    val profileIconSize = when {
        isTablet -> 150.dp
        isLandscape -> 100.dp
        else -> 120.dp
    }

    val iconSize = when {
        isTablet -> 100.dp
        isLandscape -> 60.dp
        else -> 80.dp
    }

    val cardIconSize = when {
        isTablet -> 40.dp
        else -> 32.dp
    }

    val horizontalPadding = when {
        isTablet -> 40.dp
        screenWidth >= 400.dp -> 20.dp
        else -> 16.dp
    }

    val cardPadding = when {
        isTablet -> 40.dp
        screenWidth >= 400.dp -> 20.dp
        else -> 16.dp
    }

    val titleFontSize = when {
        isTablet -> 36.sp
        isLandscape -> 24.sp
        else -> 28.sp
    }

    val emailFontSize = when {
        isTablet -> 24.sp
        isLandscape -> 16.sp
        else -> 18.sp
    }

    val cardTextSize = when {
        isTablet -> 22.sp
        else -> 18.sp
    }

    val topSpacing = when {
        isLandscape -> 20.dp
        isTablet -> 80.dp
        else -> 60.dp
    }

    // Colores del gradiente para mantener consistencia con el login
    val gradientColors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(bottom = if (isLandscape) 80.dp else 100.dp), // Extra padding for navigation
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacing))

            // Icono de perfil con tamaño adaptativo
            Box(
                modifier = Modifier
                    .size(profileIconSize)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        RoundedCornerShape(profileIconSize / 2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(iconSize),
                    tint = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

            // Email del usuario con texto adaptativo
            Text(
                text = userEmail,
                fontSize = emailFontSize,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 40.dp))

            // Título adaptativo
            Text(
                text = "Mi Perfil",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 32.dp))

            // Layout adaptativo para tablets en landscape
            if (isTablet && isLandscape) {
                // Layout horizontal para tablets en landscape
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Card para cerrar sesión
                    ProfileActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ExitToApp,
                        text = "Cerrar Sesión",
                        iconColor = Color(0xFF1976D2),
                        iconSize = cardIconSize,
                        textSize = cardTextSize,
                        onClick = { mostrarDialogoCerrarSesion = true }
                    )

                    // Card para borrar cuenta
                    ProfileActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Delete,
                        text = "Borrar Cuenta",
                        iconColor = Color(0xFFD32F2F),
                        iconSize = cardIconSize,
                        textSize = cardTextSize,
                        onClick = { mostrarDialogoBorrarCuenta = true }
                    )
                }
            } else {
                // Layout vertical para móviles y tablets en portrait
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card para cerrar sesión
                    ProfileActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.ExitToApp,
                        text = "Cerrar Sesión",
                        iconColor = Color(0xFF1976D2),
                        iconSize = cardIconSize,
                        textSize = cardTextSize,
                        cardPadding = cardPadding,
                        onClick = { mostrarDialogoCerrarSesion = true }
                    )

                    // Card para borrar cuenta
                    ProfileActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Delete,
                        text = "Borrar Cuenta",
                        iconColor = Color(0xFFD32F2F),
                        iconSize = cardIconSize,
                        textSize = cardTextSize,
                        cardPadding = cardPadding,
                        onClick = { mostrarDialogoBorrarCuenta = true }
                    )
                }
            }

            // Mensaje de error adaptativo
            if (mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensajeError,
                    color = Color.Red,
                    fontSize = if (isTablet) 16.sp else 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Loading indicator adaptativo
            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(if (isTablet) 48.dp else 40.dp)
                )
            }
        }

        // Navegador con posición adaptativa
        mostrarNavegador(navController, "Perfil")
    }

    // Diálogos adaptativos
    ProfileDialogs(
        mostrarDialogoCerrarSesion = mostrarDialogoCerrarSesion,
        mostrarDialogoBorrarCuenta = mostrarDialogoBorrarCuenta,
        onDismissCerrarSesion = { mostrarDialogoCerrarSesion = false },
        onDismissBorrarCuenta = { mostrarDialogoBorrarCuenta = false },
        onConfirmCerrarSesion = {
            mostrarDialogoCerrarSesion = false
            cerrarSesion(contexto, navController)
        },
        onConfirmBorrarCuenta = {
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
        isTablet = isTablet
    )
}

@Composable
private fun ProfileActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconColor: Color,
    iconSize: Dp = 32.dp,
    textSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    cardPadding: Dp = 20.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = cardPadding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = textSize,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProfileDialogs(
    mostrarDialogoCerrarSesion: Boolean,
    mostrarDialogoBorrarCuenta: Boolean,
    onDismissCerrarSesion: () -> Unit,
    onDismissBorrarCuenta: () -> Unit,
    onConfirmCerrarSesion: () -> Unit,
    onConfirmBorrarCuenta: () -> Unit,
    isTablet: Boolean
) {
    val dialogTextSize = if (isTablet) 18.sp else 16.sp
    val dialogTitleSize = if (isTablet) 22.sp else 20.sp

    // Diálogo para confirmar cierre de sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = onDismissCerrarSesion,
            title = {
                Text(
                    "Cerrar Sesión",
                    fontSize = dialogTitleSize
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cerrar sesión?",
                    fontSize = dialogTextSize
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmCerrarSesion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text(
                        "Confirmar",
                        fontSize = if (isTablet) 16.sp else 14.sp
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissCerrarSesion) {
                    Text(
                        "Cancelar",
                        fontSize = if (isTablet) 16.sp else 14.sp
                    )
                }
            }
        )
    }

    // Diálogo para confirmar borrado de cuenta
    if (mostrarDialogoBorrarCuenta) {
        AlertDialog(
            onDismissRequest = onDismissBorrarCuenta,
            title = {
                Text(
                    "Borrar Cuenta",
                    fontSize = dialogTitleSize
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas borrar tu cuenta? Esta acción no se puede deshacer y perderás todos tus datos.",
                    fontSize = dialogTextSize
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmBorrarCuenta,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        "Borrar Cuenta",
                        fontSize = if (isTablet) 16.sp else 14.sp
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissBorrarCuenta) {
                    Text(
                        "Cancelar",
                        fontSize = if (isTablet) 16.sp else 14.sp
                    )
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