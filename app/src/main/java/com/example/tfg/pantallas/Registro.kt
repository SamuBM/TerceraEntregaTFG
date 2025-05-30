package com.example.tfg.pantallas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Tamaños responsive
    val isSmallScreen = screenHeight < 700.dp || screenWidth < 400.dp
    val headerSize = if (isSmallScreen) 20.sp else 24.sp
    val titleSize = if (isSmallScreen) 16.sp else 20.sp
    val textSize = if (isSmallScreen) 12.sp else 14.sp
    val buttonHeight = if (isSmallScreen) 45.dp else 50.dp
    val paddingSize = if (isSmallScreen) 12.dp else 16.dp
    val spacingSize = if (isSmallScreen) 8.dp else 16.dp
    val cardPadding = if (isSmallScreen) 16.dp else 24.dp

    val gradientColors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))

    // Validaciones
    val emailValido = validarEmail(email)
    val contrasenaValida = validarContrasena(contrasena)
    val contraseñasCoinciden = contrasena == contrasena2 || contrasena2.isEmpty()
    val nombreValido = nombre.length <= 50
    val telefonoValido = telefono.length <= 15 && telefono.all { it.isDigit() || it == '+' || it == ' ' }

    val camposCompletos = nombre.isNotEmpty() && email.isNotEmpty() &&
            contrasena.isNotEmpty() && contrasena2.isNotEmpty() &&
            telefono.isNotEmpty()

    val todosLosCamposValidos = emailValido && contrasenaValida && contraseñasCoinciden &&
            nombreValido && telefonoValido && camposCompletos

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingSize)
        ) {
            // Header más compacto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (isSmallScreen) 8.dp else 16.dp, bottom = spacingSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(if (isSmallScreen) 24.dp else 28.dp)
                    )
                }

                Text(
                    text = "Registro",
                    fontSize = headerSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = spacingSize)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSmallScreen) 4.dp else 8.dp),
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
                    Text(
                        text = "Crear una cuenta",
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(bottom = spacingSize)
                    )

                    // Campo Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { if (it.length <= 50) nombre = it },
                        label = { Text("Nombre completo", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nombre",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${nombre.length}/50", fontSize = (textSize.value - 2).sp)
                        },
                        isError = !nombreValido,
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

                    Spacer(modifier = Modifier.height(spacingSize))

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 100) email = it },
                        label = { Text("Correo electrónico", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${email.length}/100 (gmail.com, outlook.es)", fontSize = (textSize.value - 2).sp)
                        },
                        isError = email.isNotEmpty() && !emailValido,
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

                    Spacer(modifier = Modifier.height(spacingSize))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { if (it.length <= 30) contrasena = it },
                        label = { Text("Contraseña", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${contrasena.length}/30 (May, min, especial)", fontSize = (textSize.value - 2).sp)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = contrasena.isNotEmpty() && !contrasenaValida,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2),
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(spacingSize))

                    // Campo Confirmar Contraseña
                    OutlinedTextField(
                        value = contrasena2,
                        onValueChange = { if (it.length <= 30) contrasena2 = it },
                        label = { Text("Confirmar contraseña", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirmar contraseña",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = contrasena.isNotEmpty() && contrasena2.isNotEmpty() && !contraseñasCoinciden,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF1976D2),
                            errorBorderColor = Color.Red
                        )
                    )

                    Spacer(modifier = Modifier.height(spacingSize))

                    // Campo Teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = {
                            if (it.length <= 15 && (it.all { char -> char.isDigit() || char == '+' || char == ' ' } || it.isEmpty())) {
                                telefono = it
                            }
                        },
                        label = { Text("Teléfono", fontSize = textSize) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Teléfono",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        supportingText = {
                            Text("${telefono.length}/15", fontSize = (textSize.value - 2).sp)
                        },
                        isError = telefono.isNotEmpty() && !telefonoValido,
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

                    // Mensajes de error compactos
                    if (!contraseñasCoinciden && contrasena2.isNotEmpty()) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = Color.Red,
                            fontSize = (textSize.value - 2).sp,
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
                            fontSize = textSize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(spacingSize))

                    Button(
                        onClick = {
                            if (todosLosCamposValidos) {
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
                        enabled = todosLosCamposValidos && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(if (isSmallScreen) 20.dp else 24.dp)
                            )
                        } else {
                            Text(
                                text = "REGISTRARSE",
                                fontSize = textSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Espaciado final para pantallas pequeñas
            Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
        }
    }
}

fun validarEmail(email: String): Boolean {
    if (!email.contains("@")) return false
    val dominiosPermitidos = listOf("gmail.com", "outlook.es")
    return dominiosPermitidos.any { dominio -> email.endsWith("@$dominio") }
}

fun validarContrasena(contrasena: String): Boolean {
    if (contrasena.length < 6) return false

    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneMinuscula = contrasena.any { it.isLowerCase() }
    val tieneEspecial = contrasena.any { !it.isLetterOrDigit() }

    return tieneMayuscula && tieneMinuscula && tieneEspecial
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

    val userData = hashMapOf(
        "nombre" to nombre,
        "email" to email,
        "contraseña" to contrasena,
        "telefono" to telefono
    )

    db.collection("usuarios")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                db.collection("usuarios")
                    .add(userData)
                    .addOnSuccessListener { documentReference ->
                        val usuarioId = documentReference.id

                        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("userId", usuarioId)
                            putString("userEmail", email)
                            putString("userName", nombre)
                            apply()
                        }

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
                actualizarMensaje("Ya existe un usuario con este correo electrónico")
                Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            actualizarMensaje("Error al verificar email: ${e.message}")
            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
        }
}