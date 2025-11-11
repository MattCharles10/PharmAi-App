package com.example.pharmai.presentation.component.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun FloatingParticles(
    particleCount: Int = 20,
    modifier: Modifier = Modifier
) {
    val particles = remember { List(particleCount) { createParticle() } }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentTime = System.currentTimeMillis() - particle.startTime
            val progress = (currentTime % particle.duration).toFloat() / particle.duration

            // Calculate position with floating animation
            val x = particle.startX + progress * (particle.endX - particle.startX)
            val y = particle.startY + progress * (particle.endY - particle.startY)

            // Calculate alpha with fade in/out
            val alpha = when {
                progress < 0.2 -> progress / 0.2f
                progress > 0.8 -> (1 - progress) / 0.2f
                else -> 1f
            }

            // Convert dp to pixels using the density
            val radiusPx = particle.size * density

            rotate(degrees = progress * 360f) {
                drawCircle(
                    color = particle.color.copy(alpha = alpha * 0.3f),
                    radius = radiusPx,
                    center = Offset(
                        x = x * size.width / 1000f, // Convert from 0-1000 range to actual screen width
                        y = y * size.height / 1000f // Convert from 0-1000 range to actual screen height
                    )
                )
            }
        }
    }
}

private data class Particle(
    val startX: Float,    // 0-1000 range
    val startY: Float,    // 0-1000 range
    val endX: Float,      // 0-1000 range
    val endY: Float,      // 0-1000 range
    val size: Float,      // in dp
    val color: Color,
    val duration: Long,
    val startTime: Long
)

private fun createParticle(): Particle {
    val random = Random.Default
    return Particle(
        startX = random.nextFloat() * 1000f,
        startY = random.nextFloat() * 1000f,
        endX = random.nextFloat() * 1000f,
        endY = random.nextFloat() * 1000f,
        size = random.nextFloat() * 8f + 4f, // 4-12 dp
        color = when (random.nextInt(3)) {
            0 -> Color(0xFF4FC3F7)  // Light Blue
            1 -> Color(0xFF4DB6AC)  // Teal
            else -> Color(0xFF7986CB) // Light Purple
        },
        duration = (3000L + random.nextLong(5000L)),
        startTime = System.currentTimeMillis() + random.nextLong(2000L)
    )
}