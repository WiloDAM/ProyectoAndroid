package com.wgv.proyectoandroid.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wgv.proyectoandroid.R
import com.wgv.proyectoandroid.model.PreferenciasUsuario
import com.wgv.proyectoandroid.model.Rutina
import com.wgv.proyectoandroid.navigation.Screens
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RutinasPersonalizadasScreen(
    navController: NavController,
    viewModel: RutinaScreenViewMode = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val rutinas = viewModel.rutinasPersonalizadas.value
    val isLoading = viewModel.isLoading.value
    val contexto = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.obtenerRutinasPersonalizadas(contexto)
    }

    Scaffold(
        floatingActionButton = {
            BotonFlotante {
                navController.navigate(Screens.CrearRutinaPersonalizadaScreen.name)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate(Screens.RutinasScreen.name) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Rutinas Personalizadas",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.Red
                    )
                    Text(
                        text = "Cargando...",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else if (rutinas.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    columns = GridCells.Fixed(1),
                    content = {
                        items(rutinas) { rutina ->
                            ItemRutinaPersonal(
                                navController = navController,
                                rutina = rutina,
                                onDeleteClick = { rutinaId ->
                                    viewModel.eliminarRutinaPersonalizada(rutinaId, contexto)
                                }
                            )
                        }
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No hay rutinas todavía", fontSize = 18.sp, color = Color.Gray)
                }
            }
        }
    }
}
@Composable
fun ItemRutinaPersonal(navController: NavController, rutina: Rutina, onDeleteClick: (String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        EliminarRutinaDialog(
            onConfirm = {
                onDeleteClick(rutina.id)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false }
        )
    }

    Card(
        elevation = CardDefaults.run {
            cardElevation(
                defaultElevation = 6.dp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(9.dp)
            .clickable { navController.navigate("${Screens.EjerciciosScreen.name}/${rutina.id}/true") }
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
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp)
            )
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text(
                    text = "Número ejercicios: ${rutina.cantidadEjercicios}",
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
            IconButton(onClick = { showDialog.value = true }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Eliminar rutina",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EliminarRutinaDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirmar eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar esta rutina?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
@Composable
fun BotonFlotante(onClic: () -> Unit){
    FloatingActionButton(
        onClick = onClic,
        containerColor = Color.Red,
        contentColor = Color.White,
        modifier = Modifier.size(56.dp)
    ) {
        Text(text = "+",
            fontSize = 24.sp,
            color = Color.White
        )
    }
}
