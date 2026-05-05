package com.example.medilink2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        // Update Firebase Auth Display Name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()
                        
                        user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
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
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getUserId(): String? = auth.currentUser?.uid

    fun updateFcmToken(token: String? = null) {
        val userId = auth.currentUser?.uid ?: return
        
        if (token != null) {
            database.child(userId).child("fcmToken").setValue(token)
        } else {
            // If token is not provided, we might want to fetch it and update, 
            // but usually this is called from onNewToken or after login.
        }
    }
}
