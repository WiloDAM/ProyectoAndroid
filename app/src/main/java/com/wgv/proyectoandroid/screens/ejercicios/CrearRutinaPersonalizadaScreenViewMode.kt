package com.wgv.proyectoandroid.screens.ejercicios

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.wgv.proyectoandroid.model.Ejercicio
import com.wgv.proyectoandroid.model.Rutina
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CrearRutinaPersonalizadaScreenViewMode: ViewModel() {

    val nombres = mutableStateOf<List<String>>(emptyList())
    val listaEjercicios = mutableStateOf<List<Ejercicio>>(emptyList())
    val nombreRutina = mutableStateOf("")
    val mensajeError = mutableStateOf("")

    fun obtenerNombreEjercicios(){

        viewModelScope.launch{
            val db = Firebase.firestore

            val referencias = db.collection("ejerciciosGenerales").get().await()

            val listaNombres = referencias.map { ejercicio ->
                ejercicio.getString("nombre") ?: ""
            }

            nombres.value = listaNombres
        }
    }

    suspend fun obtenerImagen(nombreEjercicio: String): String? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val documentos = db.collection("ejerciciosGenerales")
                .whereEqualTo("nombre", nombreEjercicio)
                .get()
                .await()
            if (!documentos.isEmpty) {
                documentos.documents[0].getString("imagenUrl")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun agregarEjercicio(ejercicio: Ejercicio) {

        if (listaEjercicios.value.any { it.nombreEjercicio == ejercicio.nombreEjercicio }) {
            mensajeError.value = "Este ejercicio ya ha sido agregado."
        } else {
            val nuevaLista = listaEjercicios.value.toMutableList()
            nuevaLista.add(ejercicio)
            listaEjercicios.value = nuevaLista
            mensajeError.value = ""
        }
    }

    fun guardarRutina(nombreRutina: String, tiempoTotal: Int, userId: String) {
        if (nombreRutina.isEmpty()) {
            mensajeError.value = "Por favor, ingrese un nombre para la rutina."
        }
        else if(listaEjercicios.value.isEmpty()){
            mensajeError.value = "No hay ejercicios en la rutina"
        }else{
            val db = FirebaseFirestore.getInstance()
            val nuevaRutina = Rutina(
                nombreRutina = nombreRutina,
                tiempo = tiempoTotal,
                cantidadEjercicios = listaEjercicios.value.size,
                ejercicios = listaEjercicios.value
            )
            viewModelScope.launch {
                try {
                    val rutinasRef =
                        db.collection("users").document(userId).collection("rutinasPersonales")
                    val rutinaDoc = rutinasRef.document(nombreRutina)
                    rutinaDoc.set(
                        mapOf(
                            "nombreRutina" to nuevaRutina.nombreRutina,
                            "tiempo" to nuevaRutina.tiempo,
                            "cantidadEjercicios" to nuevaRutina.cantidadEjercicios
                        )
                    ).await()

                    val ejerciciosRef = rutinaDoc.collection("ejercicios")
                    for (ejercicio in nuevaRutina.ejercicios) {
                        ejerciciosRef.add(
                            mapOf(
                                "nombre" to ejercicio.nombreEjercicio,
                                "duracion" to ejercicio.duracion,
                                "tiempo descanso" to ejercicio.tiempoDescanso,
                                "imagenUrl" to ejercicio.imagen
                            )
                        ).await()
                    }

                    mensajeError.value = "Rutina guardada exitosamente."
                } catch (e: Exception) {
                    e.printStackTrace()
                    mensajeError.value = "Error al guardar la rutina: ${e.message}"
                }
        }
        }
    }
}