package com.wgv.proyectoandroid.screens.ejercicios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.wgv.proyectoandroid.model.Ejercicio
import com.wgv.proyectoandroid.model.PreferenciasUsuario
import com.wgv.proyectoandroid.model.Rutina
import com.wgv.proyectoandroid.navigation.Screens
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CrearRutinaPersonalizadaScreen(navController: NavController, viewMode: CrearRutinaPersonalizadaScreenViewMode = viewModel()) {
    val nombres = viewMode.nombres.value
    var expandido by remember { mutableStateOf(false) }
    var ejercicioSeleccionado by remember { mutableStateOf("") }
    var tiempoDuracion by remember { mutableStateOf("") }
    var tiempoDescanso by remember { mutableStateOf("") }
    var listaEjercicios by viewMode.listaEjercicios
    val mensajeError by viewMode.mensajeError
    val nombreRutina by viewMode.nombreRutina
    val contexto = LocalContext.current

    LaunchedEffect(Unit) {
        viewMode.obtenerNombreEjercicios()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(
                onClick = { navController.navigate(Screens.RutinasPersonalizadasScreen.name) },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Atrás")
            }


            if (mensajeError.isNotEmpty()) {
                Text(
                    text = mensajeError,
                    color = if (mensajeError.contains(
                            "exitosamente",
                            true
                        )
                    ) Color.Green else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    val tiempoTotal = listaEjercicios.sumOf { it.duracion + it.tiempoDescanso }
                    PreferenciasUsuario.getId(contexto)?.let { userId ->
                        viewMode.guardarRutina(nombreRutina, tiempoTotal, userId)
                    }
                    if (mensajeError.isEmpty()) {
                        navController.navigate(Screens.RutinasPersonalizadasScreen.name)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Guardar Rutina")
            }

            OutlinedTextField(
                value = nombreRutina,
                onValueChange = { viewMode.nombreRutina.value = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = tiempoDuracion,
                onValueChange = { if (it.all { char -> char.isDigit() }) tiempoDuracion = it },
                label = { Text("Tiempo de duración (segundos)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Ingrese el tiempo") }
            )

            OutlinedTextField(
                value = tiempoDescanso,
                onValueChange = { if (it.all { char -> char.isDigit()  }) tiempoDescanso = it },
                label = { Text("Tiempo de descanso (segundos)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Ingrese el tiempo") }
            )

            Text(
                "Selecciona un ejercicio",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = ejercicioSeleccionado,
                onValueChange = {},
                label = { Text("Ejercicios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                readOnly = false
            )

            if (expandido) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    columns = GridCells.Fixed(1),
                    content = {
                        items(nombres) { ejercicio ->
                            Text(
                                text = ejercicio,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        ejercicioSeleccionado = ejercicio
                                        expandido = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
                    }
                )
            }

            Button(
                onClick = {
                    if (ejercicioSeleccionado.isNotEmpty() && tiempoDuracion.isNotEmpty() && tiempoDescanso.isNotEmpty()) {
                        viewMode.viewModelScope.launch {
                            val url = viewMode.obtenerImagen(ejercicioSeleccionado) ?: ""
                            val nuevoEjercicio = Ejercicio(
                                nombreEjercicio = ejercicioSeleccionado,
                                duracion = tiempoDuracion.toInt(),
                                tiempoDescanso = tiempoDescanso.toInt(),
                                imagen = url
                            )
                            viewMode.agregarEjercicio(nuevoEjercicio)
                            ejercicioSeleccionado = ""
                            tiempoDuracion = ""
                            tiempoDescanso = ""
                        }
                    } else {
                        viewMode.mensajeError.value =
                            "Complete todos los campos para añadir un ejercicio."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Añadir")
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(listaEjercicios) { ejercicio ->
                    ItemEjercicioPersonal(
                        ejercicio = ejercicio,
                        onDelete = { ejercicioAEliminar ->
                            listaEjercicios = listaEjercicios.filter { it != ejercicioAEliminar }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemEjercicioPersonal(ejercicio: Ejercicio, onDelete: (Ejercicio) -> Unit) {
    Card(colors = CardDefaults.cardColors(Color.LightGray), modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ejercicio.imagen)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                Text(text = ejercicio.nombreEjercicio, style = MaterialTheme.typography.bodyMedium)
            }

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = "Duración\n${ejercicio.duracion}s",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = { onDelete(ejercicio) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}

