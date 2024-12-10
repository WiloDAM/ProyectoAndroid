
package com.wgv.proyectoandroid.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
data class Rutina (
    var id : String =  "",
    var nombreRutina: String,
    var tiempo: Int,
    var cantidadEjercicios: Int,
    var ejercicios : List<Ejercicio>
)

