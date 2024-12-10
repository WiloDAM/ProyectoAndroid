package com.wgv.proyectoandroid.screens.ejercicios

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wgv.proyectoandroid.navigation.Screens

@SuppressLint("SuspiciousIndentation")
@Composable
fun EjercicioDetallesScreen(navController: NavController, ejercicioIndex: Int, viewModel: EjercicioScreenViewMode) {
    val index = if (ejercicioIndex < viewModel.listaEjercicios.value.size) ejercicioIndex else viewModel.listaEjercicios.value.size - 1
    val ejercicio = viewModel.listaEjercicios.value.get(index)
    val showCounter = remember { mutableStateOf(true) }
    val counter = remember { mutableStateOf(3) }
    val showBarraProgreso = remember { mutableStateOf(false) }
    val progreso = remember { mutableStateOf(0f) }
    val showDescanso = remember { mutableStateOf(false) }
    val progresoDescanso = remember { mutableStateOf(0f) }
    val tiempoDescansoTranscurrido = remember { mutableStateOf(0) }
    val showBoton = remember { mutableStateOf(false) }
    val tiempoTranscurrido = remember { mutableStateOf(0) }

    LaunchedEffect(showCounter.value) {
        if (showCounter.value) {
            for (i in 3 downTo 1) {
                counter.value = i
                kotlinx.coroutines.delay(1000L)
            }
            showCounter.value = false
            showBarraProgreso.value = true
        }
    }

    LaunchedEffect(showBarraProgreso.value) {
        if (showBarraProgreso.value && ejercicio != null) {
            val totalDuracion = ejercicio.duracion
            for (i in 1..totalDuracion) {
                progreso.value = i / totalDuracion.toFloat()
                tiempoTranscurrido.value = i
                kotlinx.coroutines.delay(1000L)
            }
            showBarraProgreso.value = false
            showDescanso.value = true
        }
    }

    LaunchedEffect(showDescanso.value) {
        if (showDescanso.value && ejercicio != null) {
            val totalDescanso = ejercicio.tiempoDescanso
            for (i in 1..totalDescanso) {
                progresoDescanso.value = i / totalDescanso.toFloat()
                tiempoDescansoTranscurrido.value = i
                kotlinx.coroutines.delay(1000L)
            }
            showDescanso.value = false
            showBoton.value = true
        }
    }

    if (showCounter.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = counter.value.toString(),
                fontSize = 100.sp,
                color = Color.Black
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {

            Text(
                text = "Ejercicio: ${ejercicio.nombreEjercicio}",
                fontSize = 30.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Duración: ${ejercicio.duracion} segundos", fontSize = 20.sp)
            Text(
                text = "Tiempo de descanso: ${ejercicio.tiempoDescanso} segundos",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ejercicio.imagen)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del ejercicio",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )


            if (showBarraProgreso.value) {
                Text(
                    text = "Progreso del ejercicio",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CustomProgressBar(progress = progreso.value, color = Color.Green)
                Text(
                    text = "Tiempo: ${tiempoTranscurrido.value} / ${ejercicio.duracion} segundos",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            if (showDescanso.value) {
                Text(
                    text = "Descansando...",
                    fontSize = 24.sp,
                    color = Color.Blue,
                    modifier = Modifier.padding(10.dp)
                )
                CustomProgressBar(progress = progresoDescanso.value, color = Color.Blue)
                Text(
                    text = "Tiempo de descanso: ${tiempoDescansoTranscurrido.value} / ${ejercicio.tiempoDescanso} segundos",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }


            if (showBoton.value) {
                Button(
                    onClick = {
                        if (index + 1 < viewModel.listaEjercicios.value.size) {
                            navController.navigate("${Screens.EjercicioDetallesScreen.name}/${ejercicioIndex + 1}")
                        } else {
                            navController.navigate(Screens.RutinasScreen.name) {
                                popUpTo(Screens.EjercicioDetallesScreen.name) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Continuar", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun CustomProgressBar(progress: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(20.dp)
            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(20.dp)
                .background(color, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
        )
    }
}