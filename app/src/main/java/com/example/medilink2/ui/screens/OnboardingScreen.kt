package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.theme.Medilink2Theme
import com.example.medilink2.ui.theme.TealPrimary
import com.example.medilink2.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val highlightedTitle: String,
    val description: String,
    val backgroundColor: Color
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {},
    onLogin: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingPage(
            title = "Find Your Medicine",
            highlightedTitle = "Instantly",
            description = "Search drug availability across pharmacies near you. Save time, save money, get treated faster.",
            backgroundColor = Color(0xFFE0F2F1)
        ),
        OnboardingPage(
            title = "Healthcare Needs",
            highlightedTitle = "At Your Fingertips",
            description = "Access a wide range of medical supplies and information from the comfort of your home.",
            backgroundColor = Color(0xFFFFF8E1)
        ),
        OnboardingPage(
            title = "Locate Nearby",
            highlightedTitle = "Pharmacies",
            description = "Find the nearest open pharmacy and get directions in real-time.",
            backgroundColor = Color(0xFFFFEBEE)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Illustration Section
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .background(page.backgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when (pageIndex) {
                        0 -> {
                            // Find Your Medicine Instantly - Medicine bottle icon
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = TealPrimary
                            )
                        }
                        1 -> {
                            // Healthcare Needs - Realistic Pills & Tablets
                            Box(modifier = Modifier.size(160.dp)) {
                                // Capsule 1 (Red/White)
                                PillCapsule(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .rotate(-45f),
                                    topColor = Color(0xFFFF5252)
                                )
                                // Capsule 2 (Blue/White)
                                PillCapsule(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .rotate(30f),
                                    topColor = Color(0xFF448AFF)
                                )
                                // Tablet 1 (Round white)
                                PillTablet(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                // Tablet 2 (Small round blue)
                                PillTablet(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(40.dp),
                                    color = Color(0xFFB3E5FC)
                                )
                            }
                        }
                        2 -> {
                            // Locate Pharmacies - Professional Hospital/Pharmacy Icon
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    modifier = Modifier.size(140.dp),
                                    tint = Color(0xFFE53935)
                                )
                                // Adding a small "pharmacy" cross overlay or just keep it clean
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = page.highlightedTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Page Indicator
        Row(
            modifier = Modifier
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) TealPrimary else Color.LightGray
                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .background(color, RoundedCornerShape(4.dp))
                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onGetStarted()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
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
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PillCapsule(modifier: Modifier = Modifier, topColor: Color = Color.Red) {
    Column(
        modifier = modifier
            .size(30.dp, 70.dp)
            .clip(RoundedCornerShape(15.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(15.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(topColor))
        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.White))
    }
}

@Composable
fun PillTablet(modifier: Modifier = Modifier, color: Color = Color.White) {
    Box(
        modifier = modifier
            .size(60.dp)
            .background(color, CircleShape)
            .border(1.dp, Color(0xFFE0E0E0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    Medilink2Theme {
        OnboardingScreen()
    }
}
