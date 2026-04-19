package com.example.medilink2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun registerUser(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = mapOf(
                            "fullName" to fullName,
                            "phoneNumber" to phoneNumber,
                            "email" to email
                        )
                        database.child(userId).setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, dbTask.exception?.message)
                                }
                            }
                    } else {
                        onResult(true, null) // Auth success but UID null? unlikely
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getCurrentUserName(onResult: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).child("fullName").get().addOnSuccessListener {
                onResult(it.value as? String ?: "User")
            }.addOnFailureListener {
                onResult("User")
            }
        } else {
            onResult("Guest")
        }
    }
}
