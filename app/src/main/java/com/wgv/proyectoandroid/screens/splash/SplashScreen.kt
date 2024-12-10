package com.wgv.proyectoandroid.screens.splash

import android.view.animation.OvershootInterpolator
import android.widget.Space
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.wgv.proyectoandroid.R
import com.wgv.proyectoandroid.navigation.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    //ANIMACION
    var scale = remember {
        androidx.compose.animation.core.Animatable(0f)

    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.8f,
            //comportamiento de la animacion - efecto rebote
            animationSpec = tween(durationMillis = 2000,
                easing = {
                    OvershootInterpolator(8f).getInterpolation(it)
                })

        )
        delay(2000)

        //ir a la siguiente pantalla
        navController.navigate(Screens.LoginScreen.name)
        // si ya está logueado el usuario no necesita autenticarse de nuevo
        if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            navController.navigate(Screens.LoginScreen.name)
        } else {
            navController.navigate(Screens.LoginScreen.name) {
                //al pulsara boton atras vuelve a splash, para evitar esto
                //sacamos el splash de la lista de pantallas recorridas
                popUpTo(Screens.SplashScreen.name) {
                    inclusive = true
                }
            }
        }
    }

    val color = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .padding(15.dp)
            .size(250.dp)
            .scale(scale.value),
        shape = CircleShape,
        border = BorderStroke(width = 2.dp, color = color)
    ) {
        Column(
            modifier = Modifier.padding(1.dp),
            verticalArrangement = Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_app),
                contentDescription = "Logo"
            )
        }
    }
}