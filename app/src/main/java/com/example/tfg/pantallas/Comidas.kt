package com.example.tfg.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfg.R

@Composable
fun mostrarComidas(navController: NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val padding = 16.dp
    val textSize = if (screenWidth < 400) 16.sp else 20.sp
    val gradientColors = listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1))
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(gradientColors)) // Fondo gris claro
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 160.dp, top = 20.dp)
        ) {
            // Título superior "Comidas"
            Text(
                text = "COMIDAS",
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
                    .fillMaxSize()
                    .padding(horizontal = padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "DESAYUNOS",
                        fontSize = textSize,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    LazyRow {
                        itemsIndexed(ImagenesPlatos.subList(14, 21)) { index, image ->
                            val globalIndex = index + 14
                            ListaItem(image, titulosPlatos[globalIndex], screenWidth) {
                                navController.navigate("detalleComida/$globalIndex")
                            }
                        }
                    }
                }

                item {
                    Text(
                        "COMIDAS",
                        fontSize = textSize,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    LazyRow {
                        itemsIndexed(ImagenesPlatos.subList(7, 14)) { index, image ->
                            val globalIndex = index + 7
                            ListaItem(image, titulosPlatos[globalIndex], screenWidth) {
                                navController.navigate("detalleComida/$globalIndex")
                            }
                        }
                    }
                }

                item {
                    Text(
                        "CENAS",
                        fontSize = textSize,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    LazyRow {
                        itemsIndexed(ImagenesPlatos.subList(0, 7)) { index, image ->
                            ListaItem(image, titulosPlatos[index], screenWidth) {
                                navController.navigate("detalleComida/$index")
                            }
                        }
                    }
                }
            }
        }

        mostrarNavegador(navController, "Comidas")
    }
}

@Composable
fun ListaItem(image: Int, title: String, screenWidth: Int, onClick: () -> Unit) {
    val cardHeight = 200.dp
    val imageSize = if (screenWidth < 400) 100.dp else 140.dp
    val cardWidth = if (screenWidth < 400) 120.dp else 150.dp

    // Card con fondo blanco, borde redondeado y sombra
    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp), // Bordes redondeados
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Sombra aplicada
        colors = CardDefaults.cardColors(containerColor = Color.White) // Fondo blanco
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp), // Padding interno
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier
                    .size(imageSize)
                    .padding(bottom = 8.dp) // Separación con el texto
            )
            Text(
                text = title,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

val ImagenesPlatos = listOf(
    R.drawable.cena_brochetas_pollo_verduras,
    R.drawable.cena_ensalada_cesar_pollo_crujiente,
    R.drawable.cena_huevos_patatas_asadas_espinacas,
    R.drawable.cena_pollo_barbacoa_brocoli_patatas_asadas,
    R.drawable.cena_pollo_judias_verdes,
    R.drawable.cena_salmon_horno_esparragos,
    R.drawable.cena_wrap_salmon_pepino_queso_untar,
    R.drawable.comida_arroz_gambas_brocoli,
    R.drawable.comida_arroz_garbanzos_salsa_curry_pan_naan,
    R.drawable.comida_arroz_pollo_brocoli_sesamo,
    R.drawable.comida_ensalada_pasta,
    R.drawable.comida_noodles_pollo_verduras,
    R.drawable.comida_pasta_champinones_espinacas,
    R.drawable.comida_pimientos_rellenos,
    R.drawable.desayuno_tostadas_huevos_duros_guacamole_pesto,
    R.drawable.desayuno_tortilla_francesa_cherrys_tostadas_guacamole,
    R.drawable.desayuno_tortilla_espinacas_cherrys,
    R.drawable.desayuno_tostadas_guacamole_champinones,
    R.drawable.desayuno_tostadas_huevos_duros_guacamole_pesto,
    R.drawable.desayuno_wrap_tortilla_francesa_verduras,
    R.drawable.desayuno_yogur_avena_fruta
)

val titulosPlatos = listOf(
    "Brochetas de pollo y verduras",
    "Ensalada César con pollo crujiente",
    "Huevos con patatas asadas y espinacas",
    "Pollo a la barbacoa con brócoli y patatas asadas",
    "Pollo con judías verdes",
    "Salmón al horno con espárragos",
    "Wrap de salmón con pepino y queso de untar",
    "Arroz con gambas y brócoli",
    "Arroz con garbanzos y salsa curry acompañado de pan naan",
    "Arroz con pollo, brócoli y sésamo",
    "Ensalada de pasta",
    "Noodles con pollo y verduras",
    "Pasta con champiñones y espinacas",
    "Pimientos rellenos",
    "Tostadas con huevos duros, guacamole y pesto",
    "Tortilla francesa con cherrys y tostadas con guacamole",
    "Tortilla de espinacas con cherrys",
    "Tostadas con guacamole y champiñones",
    "Tostadas con huevos duros, guacamole y pesto",
    "Wrap con tortilla francesa y verduras",
    "Yogur con avena y fruta"
)

data class InfoPlato(
    val ingredientes: List<String>,
    val carbohidratos: Double, // en gramos
    val proteinas: Double,     // en gramos
    val azucar: Double,        // en gramos
    val grasa: Double          // en gramos
)


val infoPlatos = listOf(
    InfoPlato(
        listOf("Pollo", "Pimiento", "Calabacín", "Cebolla", "Aceite de oliva"),
        carbohidratos = 10.0,
        proteinas = 30.0,
        azucar = 3.0,
        grasa = 12.0
    ),
    InfoPlato(
        listOf("Lechuga", "Pollo empanado", "Queso parmesano", "Salsa César", "Pan tostado"),
        carbohidratos = 20.0,
        proteinas = 25.0,
        azucar = 2.0,
        grasa = 18.0
    ),
    InfoPlato(
        listOf("Huevos", "Patatas", "Espinacas", "Aceite de oliva"),
        carbohidratos = 25.0,
        proteinas = 20.0,
        azucar = 1.5,
        grasa = 15.0
    ),
    InfoPlato(
        listOf("Pollo", "Brócoli", "Patatas", "Salsa barbacoa"),
        carbohidratos = 30.0,
        proteinas = 28.0,
        azucar = 8.0,
        grasa = 14.0
    ),
    InfoPlato(
        listOf("Pollo", "Judías verdes", "Aceite de oliva", "Ajo"),
        carbohidratos = 12.0,
        proteinas = 26.0,
        azucar = 2.0,
        grasa = 10.0
    ),
    InfoPlato(
        listOf("Salmón", "Espárragos", "Aceite de oliva", "Limón"),
        carbohidratos = 5.0,
        proteinas = 32.0,
        azucar = 1.0,
        grasa = 18.0
    ),
    InfoPlato(
        listOf("Tortilla de trigo", "Salmón ahumado", "Pepino", "Queso crema"),
        carbohidratos = 20.0,
        proteinas = 18.0,
        azucar = 2.0,
        grasa = 14.0
    ),
    InfoPlato(
        listOf("Arroz", "Gambas", "Brócoli", "Aceite de oliva"),
        carbohidratos = 40.0,
        proteinas = 22.0,
        azucar = 1.5,
        grasa = 10.0
    ),
    InfoPlato(
        listOf("Arroz", "Garbanzos", "Salsa curry", "Pan naan"),
        carbohidratos = 60.0,
        proteinas = 18.0,
        azucar = 4.0,
        grasa = 15.0
    ),
    InfoPlato(
        listOf("Arroz", "Pollo", "Brócoli", "Sésamo", "Salsa soja"),
        carbohidratos = 35.0,
        proteinas = 28.0,
        azucar = 2.5,
        grasa = 12.0
    ),
    InfoPlato(
        listOf("Pasta", "Verduras variadas", "Queso feta o mozzarella"),
        carbohidratos = 45.0,
        proteinas = 15.0,
        azucar = 3.0,
        grasa = 10.0
    ),
    InfoPlato(
        listOf("Fideos chinos", "Pollo", "Verduras salteadas", "Salsa teriyaki"),
        carbohidratos = 50.0,
        proteinas = 26.0,
        azucar = 6.0,
        grasa = 11.0
    ),
    InfoPlato(
        listOf("Pasta", "Champiñones", "Espinacas", "Nata vegetal"),
        carbohidratos = 40.0,
        proteinas = 14.0,
        azucar = 2.5,
        grasa = 16.0
    ),
    InfoPlato(
        listOf("Pimientos", "Carne picada vegetal o normal", "Arroz", "Tomate frito"),
        carbohidratos = 30.0,
        proteinas = 20.0,
        azucar = 4.0,
        grasa = 13.0
    ),
    InfoPlato(
        listOf("Pan integral", "Huevos", "Guacamole", "Pesto"),
        carbohidratos = 25.0,
        proteinas = 18.0,
        azucar = 2.0,
        grasa = 17.0
    ),
    InfoPlato(
        listOf("Huevos", "Cherrys", "Pan", "Guacamole"),
        carbohidratos = 20.0,
        proteinas = 16.0,
        azucar = 2.5,
        grasa = 15.0
    ),
    InfoPlato(
        listOf("Huevos", "Espinacas", "Cherrys", "Aceite de oliva"),
        carbohidratos = 10.0,
        proteinas = 14.0,
        azucar = 2.0,
        grasa = 10.0
    ),
    InfoPlato(
        listOf("Pan integral", "Guacamole", "Champiñones", "Aceite de oliva"),
        carbohidratos = 22.0,
        proteinas = 10.0,
        azucar = 2.0,
        grasa = 12.0
    ),
    InfoPlato(
        listOf("Pan integral", "Huevos", "Guacamole", "Pesto"),
        carbohidratos = 25.0,
        proteinas = 18.0,
        azucar = 2.0,
        grasa = 17.0
    ),
    InfoPlato(
        listOf("Tortilla francesa", "Verduras", "Tortilla de trigo"),
        carbohidratos = 18.0,
        proteinas = 15.0,
        azucar = 2.0,
        grasa = 11.0
    ),
    InfoPlato(
        listOf("Yogur natural", "Copos de avena", "Fruta variada"),
        carbohidratos = 30.0,
        proteinas = 12.0,
        azucar = 10.0,
        grasa = 6.0
    )
)



