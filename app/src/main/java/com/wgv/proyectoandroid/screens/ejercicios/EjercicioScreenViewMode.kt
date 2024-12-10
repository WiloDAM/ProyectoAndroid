package com.wgv.proyectoandroid.screens.ejercicios

import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.wgv.proyectoandroid.model.Ejercicio
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EjercicioScreenViewMode: ViewModel() {

    val nombreRutina = mutableStateOf("")
    val listaEjercicios = mutableStateOf<List<Ejercicio>>(emptyList())
    val ejercicioActual = mutableStateOf<Ejercicio?>(null)
    val isLoading = mutableStateOf(true)
    private var indiceActual = 0

    fun obtenerNombreRutina(id : String){

        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                val rutina = db.collection("rutinas").document(id).get().await()
                val nombre = rutina.getString("nombreRutina") ?: ""
                nombreRutina.value = nombre
            }catch (e:Exception){

            }
        }
    }

    fun obtenerNombreRutinaPersonal(id : String, idUsuario: String){
        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                val rutina = db.collection("users").document(idUsuario).collection("rutinasPersonales").document(id).get().await()
                val nombre = rutina.getString("nombreRutina") ?: ""
                nombreRutina.value = nombre
            }catch (e:Exception){

            }
        }
    }

    fun obtenerListaEjercicios(id: String){
        viewModelScope.launch {
            try{
                val db = Firebase.firestore

                val ejercicios = db.collection("rutinas")
                    .document(id)
                    .collection("ejercicios")
                    .get()
                    .await()

                val lista = ejercicios.map { ejercicio ->
                    Ejercicio(
                        nombreEjercicio = ejercicio.getString("nombre") ?: "",
                        duracion = ejercicio.getLong("duracion")?.toInt() ?: 0,
                        tiempoDescanso =  ejercicio.getLong("tiempo descanso")?.toInt() ?: 0,
                        imagen = ejercicio.getString("imagenUrl") ?: ""
                    )
                }

                listaEjercicios.value = lista

                if(lista.isNotEmpty()){
                    indiceActual = 0
                    ejercicioActual.value = lista[indiceActual]
                }

            }catch (e:Exception){

            }finally{
                isLoading.value = false
            }
        }
    }

    fun obtenerListaEjerciciosPersonal(id: String, idUsuario: String){
        viewModelScope.launch {
            try{
                val db = Firebase.firestore

                    val ejercicios = db.collection("users")
                        .document(idUsuario)
                        .collection("rutinasPersonales")
                        .document(id)
                        .collection("ejercicios")
                        .get()
                        .await()

                    val lista = ejercicios.map { ejercicio ->
                        Ejercicio(
                            nombreEjercicio = ejercicio.getString("nombre") ?: "",
                            duracion = ejercicio.getLong("duracion")?.toInt() ?: 0,
                            tiempoDescanso =  ejercicio.getLong("tiempo descanso")?.toInt() ?: 0,
                            imagen = ejercicio.getString("imagenUrl") ?: ""
                        )
                    }
                listaEjercicios.value = lista

                if(lista.isNotEmpty()){
                    indiceActual = 0
                    ejercicioActual.value = lista[indiceActual]
                }

            }catch (e:Exception){

            }finally{
                isLoading.value = false
            }
        }
    }


}