package com.wgv.proyectoandroid.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Ejercicio (
    var nombreEjercicio: String,
    var duracion: Int,
    var tiempoDescanso: Int,
    var imagen: String
)