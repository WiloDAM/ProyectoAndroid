package com.wgv.proyectoandroid.model

import android.content.Context
import android.content.SharedPreferences


object PreferenciasUsuario {

    private const val NOMBRE = "nombre_usuario"
    private const val CLAVE_USUARIO_ID  ="id_usuario"

    private fun getPreferencias(contexto: Context): SharedPreferences {
        return contexto.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE)
    }

    fun guardarId(contexto : Context, id: String){
        val pref = getPreferencias(contexto)
        pref.edit().putString(CLAVE_USUARIO_ID, id).apply()
    }

    fun getId(contexto: Context):String?{
        val pref = getPreferencias(contexto)
        return pref.getString(CLAVE_USUARIO_ID, null)
    }

    fun limpiarId(contexto: Context){
        val pref = getPreferencias(contexto)
        pref.edit().remove(CLAVE_USUARIO_ID).apply()
    }
}