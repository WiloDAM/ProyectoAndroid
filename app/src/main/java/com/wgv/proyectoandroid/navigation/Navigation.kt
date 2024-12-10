package com.wgv.proyectoandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wgv.proyectoandroid.model.PreferenciasUsuario
import com.wgv.proyectoandroid.screens.ejercicios.CrearRutinaPersonalizadaScreen
import com.wgv.proyectoandroid.screens.ejercicios.EjercicioDetallesScreen
import com.wgv.proyectoandroid.screens.ejercicios.EjercicioScreenViewMode
import com.wgv.proyectoandroid.screens.ejercicios.EjerciciosScreen
import com.wgv.proyectoandroid.screens.home.RutinasScreen
import com.wgv.proyectoandroid.screens.home.RutinasPersonalizadasScreen
import com.wgv.proyectoandroid.screens.login.LoginScreen
import com.wgv.proyectoandroid.screens.splash.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewMode: EjercicioScreenViewMode = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(Screens.RutinasScreen.name) {
            RutinasScreen(navController = navController)
        }
        composable(route = "${Screens.EjerciciosScreen.name}/{rutinaId}/{isPersonalizado}"){  backStackEntry ->
            val rutinaActual = backStackEntry.arguments?.getString("rutinaId") ?: ""
            val isPersonalizadoString = backStackEntry.arguments?.getString("isPersonalizado") ?: "false"
            val isPersonalizado = isPersonalizadoString.toBoolean()
            val contexto = LocalContext.current
            val usuarioId = PreferenciasUsuario.getId(contexto).toString()
            if(isPersonalizado){
                viewMode.obtenerNombreRutinaPersonal(rutinaActual, usuarioId)
                viewMode.obtenerListaEjerciciosPersonal(rutinaActual, usuarioId)
            }else {
                viewMode.obtenerListaEjercicios(rutinaActual)
                viewMode.obtenerNombreRutina(rutinaActual)
            }
            EjerciciosScreen(navController = navController, viewMode = viewMode)

        }
        composable(route = "${Screens.EjercicioDetallesScreen.name}/{ejercicioIndex}") { backStackEntry ->
            val ejercicioIndex = backStackEntry.arguments?.getString("ejercicioIndex")?.toIntOrNull() ?: 0
            EjercicioDetallesScreen(navController = navController, ejercicioIndex = ejercicioIndex, viewModel = viewMode)
        }
        composable(Screens.RutinasPersonalizadasScreen.name){
            RutinasPersonalizadasScreen(navController = navController)
        }
        composable(Screens.CrearRutinaPersonalizadaScreen.name){
            CrearRutinaPersonalizadaScreen(navController = navController)
        }


    }

}