package com.example.tfg.pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import mostrarEjerciciosConCrono
import mostrarHistorial

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            iniciar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun iniciar() {
    val navController = rememberNavController()
    val rutinaState = remember { RutinaState() }
    // Estado independiente para la rutina diaria
    val rutinaDiariaState = remember { RutinaDiariaState() }

    NavHost(navController = navController, startDestination = "LogIn") {
        composable("LogIn") { mostrarLogIn(navController) }
        composable("Home") { mostrarHome(navController) }
        composable("Registro") { mostrarRegistro(navController) }
        composable("Comidas") { mostrarComidas(navController) }
        composable("detalleComida/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
            mostrarInfoComida(navController, index)
        }
        composable("Ejercicios") { mostrarEjercicios(navController) }
        composable("detalleEjercicio/{ejercicioId}") { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")?.toIntOrNull() ?: 0
            mostrarEjercicio(navController, ejercicioId)
        }
        composable("PantallaTiempo") { mostrarPantallaTiempo(navController) }

        // Rutas para rutina semanal
        composable("rutinaSemanal") {
            mostrarRutinaSemanal(navController, rutinaState)
        }
        composable("EjerciciosSeleccion") {
            mostrarEjerciciosParaElegir(navController, rutinaState)
        }

        // Rutas para rutina diaria (nuevas)
        composable("RutinaDiaria") {
            mostrarRutinaDiaria(navController, rutinaDiariaState)
        }
        composable("EjerciciosDiariosSeleccion") {
            mostrarEjerciciosDiariosParaElegir(navController, rutinaDiariaState)
        }

        // Ruta para el cronómetro que recibe parámetros
        composable("Cronometro/{tiempoEjercicio}/{tiempoDescanso}") { backStackEntry ->
            val tiempoEjercicio = backStackEntry.arguments?.getString("tiempoEjercicio")?.toIntOrNull() ?: 30
            val tiempoDescanso = backStackEntry.arguments?.getString("tiempoDescanso")?.toIntOrNull() ?: 30

            // Verificar si viene de rutina diaria o semanal
            val rutinaTipo = navController.previousBackStackEntry?.savedStateHandle?.get<String>("rutinaTipo") ?: "semanal"

            if (rutinaTipo == "diaria") {
                mostrarEjerciciosConCronoDiario(navController, rutinaDiariaState, tiempoEjercicio, tiempoDescanso)
            } else {
                mostrarEjerciciosConCrono(navController, rutinaState, tiempoEjercicio, tiempoDescanso)
            }
        }
        composable("EjerciciosAzarCrono") {
            val tiempoEjercicio = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoEjercicio") ?: 30
            val tiempoDescanso = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("tiempoDescanso") ?: 30
            val cantidadEjercicios = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("cantidadEjercicios") ?: 5

            mostrarCronometroEjerciciosAzar(
                navController = navController,
                tiempoEjercicio = tiempoEjercicio,
                tiempoDescanso = tiempoDescanso,
                cantidadEjercicios = cantidadEjercicios
            )
        }
        composable("Historial"){ mostrarHistorial(navController) }
        composable("Perfil"){ mostrarPerfil(navController) }
    }
}