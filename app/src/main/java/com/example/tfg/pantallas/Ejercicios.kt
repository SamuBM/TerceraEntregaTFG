package com.example.tfg.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfg.R
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun mostrarEjercicios(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))

        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(gradientColors))) {
            Column(modifier = Modifier.fillMaxSize().padding(bottom = 150.dp, top = 20.dp)) {

                // 🏷 TÍTULO DE LA PANTALLA
                Text(
                    text = "EJERCICIOS",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color(0xFF1976D2),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(ejerciciosList) { ejercicio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("detalleEjercicio/${ejercicio.id}")
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = ejercicio.imagenRes),
                                    contentDescription = ejercicio.titulo,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = ejercicio.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

            }
            // ⛵ Navegador flotante sobre el contenido
            mostrarNavegador(navController, "Ejercicios")
        }

}


// Datos del ejercicio
data class Ejercicio(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagenRes: Int,
    val videoRes: Int,
    val documentId: String? = null // ID del documento en Firestore
)

// Lista de ejercicios
val ejerciciosList = listOf(
    Ejercicio(0, "Air Bike", "Ejercicio para abdominales donde alternas codo con rodilla contraria. Mantén la zona lumbar pegada al suelo y controla el ritmo para máxima efectividad.", R.drawable.i_air_bike, R.raw.v_air_bike),
    Ejercicio(1, "Burpee", "Burpee tradicional para cuerpo completo. Mantén el core activado y realiza los movimientos de forma fluida.", R.drawable.i_burpee, R.raw.v_burpee),
    Ejercicio(2, "Burpee Long Jump con Push Up", "Burpee modificado con salto largo al frente y push-up. Asegúrate de amortiguar con las piernas y mantener el control en el salto.", R.drawable.i_burpee_long_jump_with_push_up, R.raw.v_burpee_long_jump_with_push_up),
    Ejercicio(3, "Burpee con Push Up y Mountain Climber", "Ejercicio completo que une burpee, push-up y escaladores. Trabaja fuerza, cardio y coordinación. No olvides mantener la cadera alineada.", R.drawable.i_burpee_push_up_mountain_climber, R.raw.v_burpee_push_up_mountain_climber),
    Ejercicio(4, "Butt Kicks", "Corre en el lugar llevando los talones hacia los glúteos. Mantén el pecho erguido y activa los brazos para mayor intensidad.", R.drawable.i_butt_kicks, R.raw.v_butt_kicks),
    Ejercicio(5, "Close Grip Push Up", "Flexiones con manos juntas, enfocadas en tríceps. Mantén los codos pegados al cuerpo y baja el pecho lentamente.", R.drawable.i_close_grip_push_up, R.raw.v_close_grip_push_up),
    Ejercicio(6, "Cossack Squats", "Sentadilla lateral profunda. Asegúrate de mantener el pie opuesto en el suelo y la espalda recta al descender.", R.drawable.i_cossack_squats, R.raw.v_cossack_squats),
    Ejercicio(7, "Degree Heel Touch", "Tocando los talones alternos con flexión lateral del tronco. Trabaja los oblicuos. Mantén la cabeza y cuello relajados.", R.drawable.i_degree_heel_touch, R.raw.v_degree_heel_touch),
    Ejercicio(8, "Double Jump Squat", "Sentadilla con doble salto. Aterriza suavemente y mantén las rodillas alineadas con los pies.", R.drawable.i_double_jump_squat, R.raw.v_double_jump_squat),
    Ejercicio(9, "Dynamic Plank", "Plancha en movimiento: de codos a manos. Mantén el abdomen contraído y evita mover la cadera.", R.drawable.i_dynamic_plank, R.raw.v_dynamic_plank),
    Ejercicio(10, "Forward Lunge", "Zancadas hacia adelante. Mantén la espalda recta y la rodilla delantera en 90°. Alterna piernas.", R.drawable.i_forward_lunge, R.raw.v_forward_lunge),
    Ejercicio(11, "Front Leg Lift Under Knee Tap", "Elevación de pierna con toque bajo la rodilla. Mejora la coordinación y activa el core.", R.drawable.i_front_leg_lift_under_knee_tap, R.raw.v_front_leg_lift_under_knee_tap),
    Ejercicio(12, "High Knee Squat", "Combina rodillas altas con sentadilla. Mejora resistencia y fuerza en piernas. Mantén el ritmo constante.", R.drawable.i_high_knee_squat, R.raw.v_high_knee_squat),
    Ejercicio(13, "Jack Burpee con Push Up", "Burpee con movimiento de jumping jack y push-up. Trabaja fuerza y cardio al mismo tiempo.", R.drawable.i_jack_burpee_with_push_up, R.raw.v_jack_burpee_with_push_up),
    Ejercicio(14, "Jump Skip Rope", "Simulación de salto a la cuerda. Mantente ligero sobre los pies y usa los brazos para coordinar.", R.drawable.i_jump_skip_rope, R.raw.v_jump_skip_rope),
    Ejercicio(15, "Jump Squat", "Sentadilla con salto. Desciende en sentadilla y salta lo más alto posible. Amortigua con control.", R.drawable.i_jump_squat, R.raw.v_jump_squat),
    Ejercicio(16, "Jumping Jacks", "Clásico ejercicio cardiovascular. Mantén los brazos estirados y salta con ritmo sostenido.", R.drawable.i_jumping_jacks, R.raw.v_jumping_jacks),
    Ejercicio(17, "Leg Raises", "Acostado boca arriba, eleva las piernas rectas. No arquees la espalda baja. Trabaja abdomen inferior.", R.drawable.i_leg_raises, R.raw.v_leg_raises),
    Ejercicio(18, "Lying Open Close", "Movimiento de piernas abiertas y cerradas en posición supina. Controla el core y respira correctamente.", R.drawable.i_lying_open_close, R.raw.v_lying_open_close),
    Ejercicio(19, "Lying Toe Touch", "Toques de punta de pie acostado. Mantén la zona lumbar en el suelo y lleva el pecho hacia las piernas.", R.drawable.i_lying_toe_touch, R.raw.v_lying_toe_touch),
    Ejercicio(20, "Mountain Climber", "Ejercicio dinámico en posición de plancha. Alterna rodillas al pecho rápidamente sin levantar la cadera.", R.drawable.i_mountain_climber, R.raw.v_mountain_climber),
    Ejercicio(21, "Pike Push Up", "Flexiones en forma de V invertida. Trabajan los hombros. Baja la cabeza hacia el suelo con control.", R.drawable.i_pike_push_up, R.raw.v_pike_push_up),
    Ejercicio(22, "Push Up", "Flexión clásica. Baja el pecho hasta casi tocar el suelo. Mantén el cuerpo alineado todo el tiempo.", R.drawable.i_push_up, R.raw.v_push_up),
    Ejercicio(23, "Rear Lunge", "Zancada hacia atrás. Mantén el equilibrio y baja hasta formar ángulo recto con la pierna delantera.", R.drawable.i_rear_lunge, R.raw.v_rear_lunge),
    Ejercicio(24, "Shoulder Tap", "Plancha con toques de hombros alternos. Mantén el core firme y evita balanceo lateral.", R.drawable.i_shoulder_tap, R.raw.v_shoulder_tap),
    Ejercicio(25, "Sit Up", "Abdominal completo desde el suelo. No fuerces el cuello, sube usando el abdomen. Pies pueden estar apoyados.", R.drawable.i_sit_up, R.raw.v_sit_up),
    Ejercicio(26, "Squat Tip Toe", "Sentadilla seguida de elevación en puntas. Mejora fuerza y equilibrio en piernas.", R.drawable.i_squat_tip_toe, R.raw.v_squat_tip_toe),
    Ejercicio(27, "Sumo Squat", "Sentadilla con piernas abiertas y pies hacia afuera. Trabaja aductores y glúteos.", R.drawable.i_sumo_squat, R.raw.v_sumo_squat),
    Ejercicio(28, "Superman", "Acostado boca abajo, levanta brazos y piernas a la vez. Fortalece la zona lumbar.", R.drawable.i_superman, R.raw.v_superman),
    Ejercicio(29, "Twisting Crunch", "Crunch con giro. Llega con el codo a la rodilla contraria. Trabaja oblicuos. Mantén el control.", R.drawable.i_twisting_crunch, R.raw.v_twisting_crunch),
    Ejercicio(30, "Wide Hand Push Up", "Flexión con manos más separadas. Enfoca el trabajo en el pecho. Mantén el abdomen firme.", R.drawable.i_wide_hand_push_up, R.raw.v_wide_hand_push_up),
    Ejercicio(31, "Wide to Narrow Push Up", "Flexión donde cambias la posición de las manos entre repeticiones. Mejora movilidad y trabaja pecho y tríceps.", R.drawable.i_wide_to_narrow_push_up, R.raw.v_wide_to_narrow_push_up)
)