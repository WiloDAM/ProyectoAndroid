package com.wgv.proyectoandroid.model

data class User (
    val id: String?,
    val userId: String?,
    val displayName: String?

){
    fun toMap(): MutableMap<String, String?>{
        return mutableMapOf(
            "user_Id" to this.userId,
            "display_name" to this.displayName
        )
    }
}