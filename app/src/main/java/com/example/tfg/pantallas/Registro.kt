package com.example.tfg.pantallas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarRegistro(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasena2 by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    val gradientColors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))

    val contraseñasCoinciden = contrasena == contrasena2 || contrasena2.isEmpty()
    val camposCompletos = nombre.isNotEmpty() && email.isNotEmpty() &&
            contrasena.isNotEmpty() && contrasena2.isNotEmpty() &&
            telefono.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Registro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
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
                    Text(
                        text = "Crear una cuenta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nombre",
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
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
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
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = contrasena.isNotEmpty() && contrasena2.isNotEmpty() && !contraseñasCoinciden,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2),
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contrasena2,
                        onValueChange = { contrasena2 = it },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirmar contraseña",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = contrasena.isNotEmpty() && contrasena2.isNotEmpty() && !contraseñasCoinciden,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2),
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Teléfono",
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

                    if (!contraseñasCoinciden) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    if (!camposCompletos && (nombre.isNotEmpty() || email.isNotEmpty() ||
                                contrasena.isNotEmpty() || contrasena2.isNotEmpty() || telefono.isNotEmpty())) {
                        Text(
                            text = "Todos los campos son obligatorios",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    if (mensajeError.isNotEmpty()) {
                        Text(
                            text = mensajeError,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (camposCompletos && contraseñasCoinciden) {
                                isLoading = true
                                registrarUsuarioFirestore(
                                    contexto,
                                    nombre,
                                    email,
                                    contrasena,
                                    telefono,
                                    navController,
                                    { mensaje ->
                                        mensajeError = mensaje
                                        isLoading = false
                                    }
                                )
                            }
                        },
                        enabled = camposCompletos && contraseñasCoinciden && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "REGISTRARSE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun registrarUsuarioFirestore(
    context: Context,
    nombre: String,
    email: String,
    contrasena: String,
    telefono: String,
    navController: NavController,
    actualizarMensaje: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Creamos un mapa con los datos del usuario
    val userData = hashMapOf(
        "nombre" to nombre,
        "email" to email,
        "contraseña" to contrasena,
        "telefono" to telefono
    )

    // Primero verificamos si ya existe un usuario con este email
    db.collection("usuarios")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // No existe usuario con ese email, procedemos a crear uno nuevo
                db.collection("usuarios")
                    .add(userData)
                    .addOnSuccessListener { documentReference ->
                        // Usuario registrado correctamente
                        val usuarioId = documentReference.id

                        // Guardar el ID del usuario en SharedPreferences para usarlo en toda la app
                        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("userId", usuarioId)
                            putString("userEmail", email)
                            putString("userName", nombre)
                            apply()
                        }

                        // Ya no creamos subcolecciones como rutinaresumen o rutina_semanal
                        // Ahora usaremos una colección común: rutina_resumen

                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        navController.navigate("Home") {
                            popUpTo("Registro") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        actualizarMensaje("Error al registrar: ${e.message}")
                        Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Ya existe un usuario con ese email
                actualizarMensaje("Ya existe un usuario con este correo electrónico")
                Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            actualizarMensaje("Error al verificar email: ${e.message}")
            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
        }
}