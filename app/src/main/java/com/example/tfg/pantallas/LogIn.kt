package com.example.tfg.pantallas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarLogIn(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

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

            // Logo placeholder - replace R.drawable.logo with your actual logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LOGO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                // Uncomment below and remove Text above when you have a logo
                // Image(
                //     painter = painterResource(id = R.drawable.logo),
                //     contentDescription = "Logo",
                //     modifier = Modifier.size(80.dp),
                //     contentScale = ContentScale.Fit
                // )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contraseña,
                        onValueChange = { contraseña = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && contraseña.isNotEmpty()) {
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
                                mensajeError = "Por favor, completa todos los campos"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = { navController.navigate("Registro") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate ahora",
                    color = Color.White,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

fun validarUsuarioFirestore(
    navController: NavController,
    email: String,
    contraseña: String,
    contexto: Context,
    actualizarMensaje: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Buscamos un usuario con el email proporcionado
    db.collection("usuarios")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documentos ->
            if (documentos.isEmpty) {
                // No se encontró ningún usuario con ese email
                actualizarMensaje("Usuario no encontrado")
            } else {
                // Verificamos la contraseña
                val usuario = documentos.documents[0]
                val passwordAlmacenada = usuario.getString("contraseña") ?: ""

                if (contraseña == passwordAlmacenada) {
                    // Contraseña correcta, guardamos el ID de usuario en sesión
                    // Aquí podrías almacenar el ID del usuario en SharedPreferences o similar
                    // para mantener la sesión activa
                    val userId = usuario.id
                    // Ejemplo: guardarEnPreferences(contexto, "userId", userId)

                    // Contraseña correcta, guardamos el email en SharedPreferences
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
                    // Contraseña incorrecta
                    actualizarMensaje("Contraseña incorrecta")
                }
            }
        }
        .addOnFailureListener { excepcion ->
            actualizarMensaje("Error al iniciar sesión: ${excepcion.message}")
        }
}