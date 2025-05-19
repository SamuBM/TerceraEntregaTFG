package com.example.tfg.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FoodBank
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfg.R

@Composable
fun mostrarHome(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con imagen de pantalla completa
        Image(
            painter = painterResource(id = R.drawable.fondomovil), // Asegúrate de tener esta imagen en res/drawable
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Contenido existente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 15.dp, bottom = 95.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("PantallaTiempo") },
                modifier = Modifier.padding(80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text(text = "Empezar")
            }
        }

        mostrarNavegador(navController, "Home")
    }
}

@Composable
fun mostrarNavegador(navController: NavController, selectedScreen: String) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val iconSize = if (screenWidth < 400) 24.dp else 28.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    label = "Comidas",
                    selectedIcon = Icons.Filled.FoodBank,
                    unselectedIcon = Icons.Outlined.FoodBank,
                    navController = navController,
                    selectedScreen = selectedScreen,
                    iconSize = iconSize
                )
                NavigationItem(
                    label = "Ejercicios",
                    selectedIcon = Icons.Filled.DirectionsRun,
                    unselectedIcon = Icons.Outlined.DirectionsRun,
                    navController = navController,
                    selectedScreen = selectedScreen,
                    iconSize = iconSize
                )
                NavigationItem(
                    label = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    navController = navController,
                    selectedScreen = selectedScreen,
                    iconSize = iconSize,
                    isHomeButton = true
                )
                NavigationItem(
                    label = "Historial",
                    selectedIcon = Icons.Filled.DateRange,
                    unselectedIcon = Icons.Outlined.DateRange,
                    navController = navController,
                    selectedScreen = selectedScreen,
                    iconSize = iconSize
                )
                NavigationItem(
                    label = "Perfil",
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    navController = navController,
                    selectedScreen = selectedScreen,
                    iconSize = iconSize
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    navController: NavController,
    selectedScreen: String,
    iconSize: Dp,
    isHomeButton: Boolean = false
) {
    val isSelected = label == selectedScreen
    val primaryColor = Color(0xFF1976D2)
    val itemColor = if (isSelected) primaryColor else Color.Gray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { navController.navigate(label) }
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .width(55.dp)
    ) {
        if (isHomeButton) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = itemColor,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = itemColor,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
