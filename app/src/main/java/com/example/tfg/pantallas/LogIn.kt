package com.example.tfg.pantallas

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfg.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarLogIn(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Tamaños responsive
    val isSmallScreen = screenHeight < 700.dp || screenWidth < 400.dp
    val logoSize = if (isSmallScreen) 80.dp else 120.dp
    val titleSize = if (isSmallScreen) 22.sp else 28.sp
    val textSize = if (isSmallScreen) 12.sp else 14.sp
    val buttonTextSize = if (isSmallScreen) 14.sp else 16.sp
    val buttonHeight = if (isSmallScreen) 45.dp else 50.dp
    val paddingSize = if (isSmallScreen) 12.dp else 16.dp
    val spacingLarge = if (isSmallScreen) 20.dp else 32.dp
    val spacingMedium = if (isSmallScreen) 12.dp else 16.dp
    val spacingSmall = if (isSmallScreen) 16.dp else 24.dp
    val cardPadding = if (isSmallScreen) 16.dp else 24.dp
    val topSpacing = if (isSmallScreen) 30.dp else 60.dp

    val gradientColors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))

    // Validaciones
    val emailValido = email.isEmpty() || validarEmailLogin(email)
    val camposCompletos = email.isNotEmpty() && contraseña.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(paddingSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacing))

            // Logo más pequeño para pantallas pequeñas
            Box(
                modifier = Modifier
                    .size(logoSize)
                    .clip(RoundedCornerShape(logoSize / 2))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logofitmate),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize * 0.85f), // 60% del tamaño del contenedor
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 40.dp))

            Text(
                text = "Iniciar Sesión",
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSmallScreen) 12.dp else 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(cardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 100) email = it },
                        label = { Text("Correo electrónico", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${email.length}/100", fontSize = (textSize.value - 2).sp)
                        },
                        isError = !emailValido,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2),
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(spacingMedium))

                    OutlinedTextField(
                        value = contraseña,
                        onValueChange = { if (it.length <= 30) contraseña = it },
                        label = { Text("Contraseña", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${contraseña.length}/30", fontSize = (textSize.value - 2).sp)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2)
                        )
                    )

                    if (mensajeError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mensajeError,
                            color = Color.Red,
                            fontSize = textSize,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(spacingSmall))

                    Button(
                        onClick = {
                            if (camposCompletos && emailValido) {
                                isLoading = true
                                validarUsuarioFirestore(
                                    navController = navController,
                                    email = email,
                                    contraseña = contraseña,
                                    contexto = contexto,
                                    actualizarMensaje = { mensaje ->
                                        mensajeError = mensaje
                                        isLoading = false
                                    }
                                )
                            } else {
                                mensajeError = if (!emailValido) {
                                    "Email debe ser @gmail.com o @outlook.es"
                                } else {
                                    "Por favor, completa todos los campos"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 24.dp)
                            )
                        } else {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontSize = buttonTextSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isSmallScreen) 12.dp else 20.dp))

            TextButton(
                onClick = { navController.navigate("Registro") },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate ahora",
                    color = Color.White,
                    fontSize = buttonTextSize,
                    textDecoration = TextDecoration.Underline
                )
            }

            // Espaciado final para evitar que se corte el contenido
            Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
        }
    }
}

fun validarEmailLogin(email: String): Boolean {
    if (!email.contains("@")) return false
    val dominiosPermitidos = listOf("gmail.com", "outlook.es")
    return dominiosPermitidos.any { dominio -> email.endsWith("@$dominio") }
}

fun validarUsuarioFirestore(
    navController: NavController,
    email: String,
    contraseña: String,
    contexto: Context,
    actualizarMensaje: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("usuarios")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documentos ->
            if (documentos.isEmpty) {
                actualizarMensaje("Usuario no encontrado")
            } else {
                val usuario = documentos.documents[0]
                val passwordAlmacenada = usuario.getString("contraseña") ?: ""

                if (contraseña == passwordAlmacenada) {
                    val sharedPref = contexto.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("userEmail", email)
                        putString("userId", usuario.id)
                        apply()
                    }

                    actualizarMensaje("Inicio de sesión exitoso")
                    navController.navigate("Home") {
                        popUpTo("LogIn") { inclusive = true }
                    }
                } else {
                    actualizarMensaje("Contraseña incorrecta")
                }
            }
        }
        .addOnFailureListener { excepcion ->
            actualizarMensaje("Error al iniciar sesión: ${excepcion.message}")
        }
}