package com.example.pharmai.presentation.screen.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val blur = remember { Animatable(20f) }

    // Background gradient
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    LaunchedEffect(Unit) {
        // Initial animations
        blur.animateTo(0f, animationSpec = tween(800))
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alpha.animateTo(1f, animationSpec = tween(1000))
        rotation.animateTo(360f, animationSpec = tween(1500))

        delay(500) // Hold at peak

        // Final animations
        scale.animateTo(1f, animationSpec = tween(500))
        delay(800) // Show final state

        // Navigate to login
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        // Animated background elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .scale(scale.value)
                .alpha(alpha.value * 0.5f)
                .blur(blur.value.dp)
        )

        // Main content
        Box(
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated icon
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = "PharmAI Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value),
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(16.dp))

                // App name with gradient text
                Text(
                    text = "PharmAI",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 48.sp,
                    modifier = Modifier.alpha(alpha.value)
                )

                Spacer(modifier = Modifier.size(8.dp))

                // Tagline
                Text(
                    text = "Your Smart Pharmacy Companion",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    modifier = Modifier.alpha(alpha.value * 0.8f)
                )
            }
        }

        // Loading indicator at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(alpha.value)
        ) {
            // Custom loading dots
            Row {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.7f - (index * 0.2f)
                                ),
                                shape = MaterialTheme.shapes.small
                            )
                            .scale(scale.value * (0.8f + (index * 0.1f)))
                    )
                    if (index < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}