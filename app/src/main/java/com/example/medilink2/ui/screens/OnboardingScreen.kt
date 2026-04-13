package com.example.medilink2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.theme.Medilink2Theme
import com.example.medilink2.ui.theme.TealPrimary
import com.example.medilink2.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for the Pharmacy illustration
        Box(
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.Center
        ) {
             // In a real app, use an actual Image resource
             Text("Pharmacy Illustration", color = TealPrimary)
        }

        Text(
            text = "Find Your Medicine",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Instantly",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Search drug availability across pharmacies near you. Save time, save money, get treated faster.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Page Indicator placeholder
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(24.dp, 8.dp).background(TealPrimary, RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Get Started", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("I already have an account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    Medilink2Theme {
        OnboardingScreen()
    }
}