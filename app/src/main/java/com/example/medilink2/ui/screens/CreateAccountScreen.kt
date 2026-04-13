package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackToLogin: () -> Unit = {}
) {
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

        InputField(label = "Full Name", placeholder = "John Doe", icon = Icons.Outlined.Person)
        Spacer(modifier = Modifier.height(24.dp))
        InputField(label = "Phone Number", placeholder = "+256 700 000 000", icon = Icons.Outlined.Call)
        Spacer(modifier = Modifier.height(24.dp))
        InputField(label = "Email", placeholder = "you@example.com", icon = Icons.Outlined.Email)
        Spacer(modifier = Modifier.height(24.dp))
        InputField(label = "Password", placeholder = "••••••••", icon = Icons.Outlined.Lock, isPassword = true)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Column {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
            trailingIcon = if (isPassword) {
                { Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Gray) }
            } else null,
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
