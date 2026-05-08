package com.example.medilink2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

object UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    enum class UserRole {
        PATIENT, PHARMACY_OWNER
    }

    fun registerUser(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        role: UserRole = UserRole.PATIENT,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()
                        
                        user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                            val userMap = mapOf(
                                "fullName" to fullName,
                                "phoneNumber" to phoneNumber,
                                "email" to email,
                                "role" to role.name
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

    fun getUserRole(onResult: (UserRole?) -> Unit) {
        val userId = getUserId()
        if (userId == null) {
            onResult(null)
            return
        }
        database.child(userId).child("role").get().addOnSuccessListener { snapshot ->
            val roleStr = snapshot.value as? String
            val role = roleStr?.let { UserRole.valueOf(it) } ?: UserRole.PATIENT
            onResult(role)
        }.addOnFailureListener {
            onResult(null)
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

    fun updateFcmToken(token: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child(userId).child("fcmToken").setValue(token)
    }
}
