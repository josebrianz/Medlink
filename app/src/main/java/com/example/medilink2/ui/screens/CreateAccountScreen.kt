package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.UserManager
import com.example.medilink2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackToLogin: () -> Unit = {},
    onAccountCreated: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Create Account ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("🏥", fontSize = 24.sp)
        }
        
        Text(
            text = "Join MedFind to locate medicines near you",
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        InputField(
            label = "Full Name",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "John Doe",
            icon = Icons.Outlined.Person
        )
        Spacer(modifier = Modifier.height(24.dp))
        InputField(
            label = "Phone Number",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = "+256 700 000 000",
            icon = Icons.Outlined.Call
        )
        Spacer(modifier = Modifier.height(24.dp))
        InputField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "you@example.com",
            icon = Icons.Outlined.Email
        )
        Spacer(modifier = Modifier.height(24.dp))
        InputField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "••••••••",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                if (fullName.isNotBlank() && phoneNumber.isNotBlank() && trimmedEmail.isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    errorMessage = null
                    UserManager.registerUser(fullName, phoneNumber, trimmedEmail, password) { success, message ->
                        isLoading = false
                        if (success) {
                            onAccountCreated()
                        } else {
                            errorMessage = message ?: "Registration failed"
                        }
                    }
                } else {
                    errorMessage = "Please fill in all fields"
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
                Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !isLoading
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(color = TealPrimary, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Sign In")
                    }
                },
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedIndicatorColor = TealPrimary,
                unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountPreview() {
    Medilink2Theme {
        CreateAccountScreen()
    }
}
