package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.theme.TealPrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    onProfileUpdated: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userId = user?.uid
    
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isFetching by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users").child(userId)
            database.get().addOnSuccessListener { snapshot ->
                fullName = snapshot.child("fullName").getValue(String::class.java) ?: ""
                phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                isFetching = false
            }.addOnFailureListener {
                isFetching = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isFetching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                InputField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Enter your full name",
                    icon = Icons.Outlined.Person
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                InputField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "Enter your phone number",
                    icon = Icons.Outlined.Call
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        if (userId != null && fullName.isNotBlank()) {
                            isLoading = true
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()
                            
                            user.updateProfile(profileUpdates).addOnCompleteListener {
                                val database = FirebaseDatabase.getInstance().getReference("users").child(userId)
                                val updates = mapOf(
                                    "fullName" to fullName,
                                    "phoneNumber" to phoneNumber
                                )
                                database.updateChildren(updates).addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onProfileUpdated()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
