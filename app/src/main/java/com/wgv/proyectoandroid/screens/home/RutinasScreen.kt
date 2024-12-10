package com.wgv.proyectoandroid.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wgv.proyectoandroid.model.Rutina
import com.wgv.proyectoandroid.navigation.Screens
@Composable
fun RutinasScreen(navController: NavController, viewModel: RutinaScreenViewMode = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val rutinas = viewModel.rutinas.value
    val isLoading by viewModel.isLoading
    val expandido = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            IconButton(
                onClick = { expandido.value = true },
                modifier = Modifier
                    .size(50.dp)
                    .weight(0.2f)
            ) {
                Text(
                    text = "≡",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }


            Text(
                text = "BODYFORGE",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .weight(0.6f)
                    .align(Alignment.CenterVertically)
            )


            Box(modifier = Modifier.weight(0.2f))
        }

        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .weight(0.2f)
        ) {
            Button(
                onClick = { expandido.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.size(50.dp)
            ) {
                Text(
                    text = "≡",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            DropdownMenu(
                expanded = expandido.value,
                onDismissRequest = { expandido.value = false },
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                DropdownMenuItem(
                    text = { Text("Rutinas Personalizadas") },
                    onClick = {
                        expandido.value = false
                        navController.navigate(Screens.RutinasPersonalizadasScreen.name)
                    }
                )
            }
        }


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 30.sp,
                modifier = Modifier.padding(20.dp)
            )

            Box(
                modifier = Modifier
                    .size(width = 170.dp, height = 60.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.Red)
            ) {
                Text(
                    text = "RUTINAS",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 30.dp),
                    columns = GridCells.Fixed(1),
                    content = {
                        items(rutinas) { rutina ->
                            ItemRutina(navController = navController, rutina = rutina)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ItemRutina(navController: NavController, rutina: Rutina) {
    val rutinaId = rutina.id
    Card(
        elevation = CardDefaults.run {
            cardElevation(defaultElevation = 6.dp)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(9.dp)
            .clickable { navController.navigate("${Screens.EjerciciosScreen.name}/${rutinaId}/false") }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.Red)
                .height(140.dp)
                .padding(15.dp)
        ) {
            Text(
                text = rutina.nombreRutina,
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
                color = Color.White
            )
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = "Número ejercicios: ${rutina.cantidadEjercicios}",
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}