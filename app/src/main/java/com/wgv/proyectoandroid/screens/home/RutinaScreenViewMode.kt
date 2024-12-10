package com.wgv.proyectoandroid.screens.home

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.wgv.proyectoandroid.model.Ejercicio
import com.wgv.proyectoandroid.model.PreferenciasUsuario
import com.wgv.proyectoandroid.model.Rutina
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RutinaScreenViewMode : ViewModel() {

    val rutinas = mutableStateOf<List<Rutina>>(emptyList())
    val rutinasPersonalizadas = mutableStateOf<List<Rutina>>(emptyList())
    val isLoading = mutableStateOf(false)
    init{
        obtenerRutinasFireBase()
    }

    fun obtenerRutinasFireBase(){
        viewModelScope.launch{
            isLoading.value = true
            try {
                val db = Firebase.firestore
                val rutinaInstantanea = db.collection("rutinas").get().await()
                val rutinaList = mutableListOf<Rutina>()

                for (doc in rutinaInstantanea.documents) {
                    val ejercicioInstantaneo = db.collection("rutinas")
                        .document(doc.id)
                        .collection("ejercicios")
                        .get().await()

                    val ejercicios = ejercicioInstantaneo.documents.map { ejercicioDoc ->
                        Ejercicio(
                            nombreEjercicio = ejercicioDoc.getString("nombre") ?: "",
                            duracion = ejercicioDoc.getLong("duracion")?.toInt() ?: 0,
                            tiempoDescanso = ejercicioDoc.getLong("tiempo descanso")?.toInt() ?: 0,
                            imagen = ejercicioDoc.getString("imagenUrl") ?: ""
                        )
                    }
                    val rutina = Rutina(
                        id = doc.id,
                        nombreRutina = doc.getString("nombreRutina") ?: "",
                        tiempo = doc.getLong("tiempo")?.toInt() ?: 0,
                        cantidadEjercicios = doc.getLong("cantidadEjercicios")?.toInt() ?: 0,
                        ejercicios = ejercicios
                    )
                    rutinaList.add(rutina)

                }
                rutinas.value = rutinaList
            }catch (e :Exception){
                e.printStackTrace()
            }finally {
                isLoading.value = false
            }
        }
    }

    fun obtenerRutinasPersonalizadas(contexto : Context){
        viewModelScope.launch{
            rutinas.value = emptyList()
            isLoading.value = true
            try {
                val usuario = PreferenciasUsuario.getId(contexto)
                val db = Firebase.firestore
                val refRutinas = db.collection("users").document(usuario.toString()).collection("rutinasPersonales").get().await()
                val rutinaList = mutableListOf<Rutina>()

                for (doc in refRutinas.documents) {

                    val ejercicioInstantaneo = db.collection("users")
                        .document(usuario.toString())
                        .collection("rutinasPersonales")
                        .document(doc.id)
                        .collection("ejercicios")
                        .get().await()

                    val ejercicios = ejercicioInstantaneo.documents.map { ejercicioDoc ->
                        Ejercicio(
                            nombreEjercicio = ejercicioDoc.getString("nombre") ?: "",
                            duracion = ejercicioDoc.getLong("duracion")?.toInt() ?: 0,
                            tiempoDescanso = ejercicioDoc.getLong("tiempo descanso")?.toInt() ?: 0,
                            imagen = ejercicioDoc.getString("imagenUrl") ?: ""
                        )
                    }
                    val rutina = Rutina(
                        id = doc.id,
                        nombreRutina = doc.getString("nombreRutina") ?: "",
                        tiempo = doc.getLong("tiempo")?.toInt() ?: 0,
                        cantidadEjercicios = doc.getLong("cantidadEjercicios")?.toInt() ?: 0,
                        ejercicios = ejercicios
                    )
                    rutinaList.add(rutina)
                }
                rutinasPersonalizadas.value = rutinaList
                isLoading.value = false

            }catch (e : Exception){
                rutinasPersonalizadas.value = emptyList()
                isLoading.value = false
            }
        }
    }

    fun eliminarRutinaPersonalizada(rutinaId: String, contexto :Context) {

        viewModelScope.launch {
            try {

                val ejercicios = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(PreferenciasUsuario.getId(contexto).toString())
                    .collection("rutinasPersonales")
                    .document(rutinaId)
                    .collection("ejercicios")
                    .get()
                    .await()

                for (doc in ejercicios){
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(PreferenciasUsuario.getId(contexto).toString())
                        .collection("rutinasPersonales")
                        .document(rutinaId)
                        .collection("ejercicios")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(PreferenciasUsuario.getId(contexto).toString())
                    .collection("rutinasPersonales")
                    .document(rutinaId)
                    .delete()
                    .await()


                obtenerRutinasPersonalizadas(contexto)
            } catch (e: Exception) {

            }
        }
    }
}