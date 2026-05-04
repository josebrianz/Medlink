package com.example.medilink2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object UserManager {
    private val auth get() = FirebaseAuth.getInstance()
    private val database get() = FirebaseDatabase.getInstance().getReference("users")
    private val firestore get() = FirebaseFirestore.getInstance()

    fun getUserId(): String? = auth.currentUser?.uid

    fun registerUser(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = mapOf(
                            "fullName" to fullName,
                            "phoneNumber" to phoneNumber,
                            "email" to email,
                        )
                        database.child(userId).setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    updateFcmToken()
                                    onResult(true, null)
                                } else {
                                    onResult(false, dbTask.exception?.message)
                                }
                            }
                    } else {
                        onResult(true, null)
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
                    updateFcmToken()
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun updateFcmToken() {
        val userId = auth.currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestore.collection("users").document(userId)
                    .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            }
        }
    }

    fun getCurrentUserName(onResult: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).child("fullName").get().addOnSuccessListener {
                onResult((it.value as? String) ?: "User")
            }.addOnFailureListener {
                onResult("User")
            }
        } else {
            onResult("Guest")
        }
    }
}
