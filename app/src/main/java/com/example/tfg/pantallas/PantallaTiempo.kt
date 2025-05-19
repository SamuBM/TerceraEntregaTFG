package com.example.tfg.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign

@Composable
fun mostrarPantallaTiempo(navController: NavController) {
    var expandedEjercicio by remember { mutableStateOf(false) }
    var selectedEjercicio by remember { mutableStateOf(30) }
    val opcionesEjercicio = (30..180 step 15).toList()

    var expandedDescanso by remember { mutableStateOf(false) }
    var selectedDescanso by remember { mutableStateOf(30) }
    val opcionesDescanso = (30..180 step 15).toList()

    var aleatorio by remember { mutableStateOf(false) }
    var cantidadEjercicios by remember { mutableStateOf(5) }
    val opcionesCantidad = (1..20).toList()

    // Guardar los valores seleccionados en el NavController para que estén disponibles en otras pantallas
    LaunchedEffect(selectedEjercicio, selectedDescanso) {
        navController.currentBackStackEntry?.savedStateHandle?.set(
            "tiempoEjercicio",
            selectedEjercicio
        )
        navController.currentBackStackEntry?.savedStateHandle?.set(
            "tiempoDescanso",
            selectedDescanso
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 15.dp, bottom = 95.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Entrenar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 60.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    DropDownRow(
                        label = "Duración Ejercicios",
                        selected = "$selectedEjercicio s",
                        expanded = expandedEjercicio,
                        onExpand = { expandedEjercicio = true },
                        onDismiss = { expandedEjercicio = false },
                        opciones = opcionesEjercicio.map { "$it s" },
                        onSelect = {
                            selectedEjercicio = it.filter { c -> c.isDigit() }.toInt()
                            expandedEjercicio = false
                        }
                    )

                    DropDownRow(
                        label = "Duración Descansos",
                        selected = "$selectedDescanso s",
                        expanded = expandedDescanso,
                        onExpand = { expandedDescanso = true },
                        onDismiss = { expandedDescanso = false },
                        opciones = opcionesDescanso.map { "$it s" },
                        onSelect = {
                            selectedDescanso = it.filter { c -> c.isDigit() }.toInt()
                            expandedDescanso = false
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ejercicios en orden aleatorio", fontSize = 16.sp)
                        Switch(
                            checked = aleatorio,
                            onCheckedChange = { aleatorio = it }
                        )
                    }
                }
            }

            if (aleatorio) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Cantidad de Ejercicios Aleatorios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        DropdownMenuCantidad(
                            selected = cantidadEjercicios,
                            opciones = opcionesCantidad,
                            onSelect = { cantidadEjercicios = it }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "tiempoEjercicio",
                                selectedEjercicio
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "tiempoDescanso",
                                selectedDescanso
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "cantidadEjercicios",
                                cantidadEjercicios
                            )
                            navController.navigate("EjerciciosAzarCrono")
                        })
                        {
                            Text("Empezar")
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = 15.dp, bottom = 95.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Mantenemos solo los botones RS y RD (sin el botón Empezar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("RutinaSemanal") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("RS")
                    }

                    // Espacio en medio donde estaba el botón "Empezar"
                    Spacer(modifier = Modifier.width(120.dp))

                    Button(
                        onClick = { navController.navigate("RutinaDiaria") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("RD")
                    }
                }
            }
        }
    }
    mostrarNavegador(navController, "Home")
}

@Composable
fun DropdownMenuCantidad(
    selected: Int,
    opciones: List<Int>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {
            Text("$selected ejercicios", color = Color.White)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text("$opcion") },
                    onClick = {
                        onSelect(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DropDownRow(
    label: String,
    selected: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    opciones: List<String>,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )

        Box {
            TextButton(onClick = { onExpand() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {

                Text(selected)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onDismiss() },
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { onSelect(opcion) }
                    )
                }
            }
        }
    }
}