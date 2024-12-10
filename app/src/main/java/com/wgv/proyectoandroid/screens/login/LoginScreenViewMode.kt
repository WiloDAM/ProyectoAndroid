package com.wgv.proyectoandroid.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.wgv.proyectoandroid.model.PreferenciasUsuario
import com.wgv.proyectoandroid.model.User
import kotlinx.coroutines.launch

class LoginScreenViewMode : ViewModel(){
    // OJOO!!! DAR PERMISO INTERNET EN MANIFEST
    private val auth: FirebaseAuth = com.google.firebase.Firebase.auth //la estaremos usando a lo largo del proyecto
    // impide que se creen varios usuarios accidentalmente
    private val _loading = MutableLiveData(false)

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task -> //si la tarea tuve exito escribimos mensaje en log
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")

                            home()
                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithGoogle: ${task.result.toString()}"
                            )
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
            }
        }


    fun signInWithEmailAndPassword(email: String, password: String, contexto : Context, home: () -> Unit) =
        viewModelScope.launch { //para que se ejecute en segundo plano
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task -> //si la tarea tuve exito escribimos mensaje en log
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "signInWithEmailAndPassword logueado!!!!")
                            val currentUser = auth.currentUser
                            currentUser?.let { user->
                                val db = FirebaseFirestore.getInstance()
                                db.collection("users").
                                        whereEqualTo("user_Id", user.uid)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if(!querySnapshot.isEmpty){
                                            val documentoId = querySnapshot.documents[0].id

                                            PreferenciasUsuario.guardarId(contexto,documentoId)
                                            home()
                                        }
                                    }
                            }

                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithEmailAndPassword: ${task.result.toString()}"
                            )
                        }
                    }

            } catch (ex: Exception) {
                Log.d("MyLogin", "signInWithEmailAndPassword: ${ex.message}")
            }
        }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        contexto: Context,
        home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName) { userId ->
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users")
                                .whereEqualTo("user_Id", userId)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val documentoId = querySnapshot.documents[0].id
                                        PreferenciasUsuario.guardarId(contexto, documentoId)
                                        home()
                                    }
                                }
                        }
                    } else {
                        Log.d(
                            "MyLogin",
                            "createUserWithEmailAndPassword: ${task.result.toString()}"
                        )
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?, onUserCreated: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val user = User(
                userId = userId,
                displayName = displayName.toString(),
                id = null
            ).toMap()

            FirebaseFirestore.getInstance()
                .collection("users")
                .add(user)
                .addOnSuccessListener {
                    Log.d("MyLogin", "Usuario creado en Firestore: ${it.id}")
                    onUserCreated(userId)
                }
                .addOnFailureListener { error ->
                    Log.d("MyLogin", "Error al crear usuario: $error")
                }
        }
    }

}