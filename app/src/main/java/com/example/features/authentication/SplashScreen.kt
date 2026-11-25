package com.example.features.authentication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WhatsChatLogoBadge
import com.example.ui.theme.WhatsChatThemeTokens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  isLoggedIn: Boolean,
  onNavigateNext: (loggedIn: Boolean) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "logo_scale",
  )

  LaunchedEffect(isLoggedIn) {
    delay(1300)
    onNavigateNext(isLoggedIn)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.backgroundPrimary),
  ) {
    // Soft radial emerald ambient glow behind logo + decorative bottom wave
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            colors.primaryGreen.copy(alpha = 0.22f),
            Color.Transparent,
          ),
          center = Offset(size.width / 2f, size.height * 0.43f),
          radius = size.minDimension * 0.52f,
        ),
        center = Offset(size.width / 2f, size.height * 0.43f),
        radius = size.minDimension * 0.52f,
      )

      val wavePath = Path().apply {
        moveTo(0f, size.height * 0.86f)
        cubicTo(
          size.width * 0.3f,
          size.height * 0.78f,
          size.width * 0.7f,
          size.height * 0.94f,
          size.width,
          size.height * 0.84f,
        )
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
      }
      drawPath(
        path = wavePath,
        brush = Brush.verticalGradient(
          colors = listOf(
            colors.primaryGreen.copy(alpha = 0.18f),
            colors.backgroundSecondary.copy(alpha = 0.7f),
          ),
          startY = size.height * 0.78f,
          endY = size.height,
        ),
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Spacer(modifier = Modifier.weight(1f))

      WhatsChatLogoBadge(
        size = 108.dp,
        modifier = Modifier.scale(pulseScale),
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "WhatsChat",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "One App. Two Ways to Connect.",
        style = MaterialTheme.typography.bodyMedium,
        color = colors.mintAccent,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Connect · Chat · Call · Nearby",
        style = MaterialTheme.typography.bodySmall,
        color = colors.textSecondary,
      )

      Spacer(modifier = Modifier.weight(1f))

      LinearProgressIndicator(
        modifier = Modifier
          .width(160.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(999.dp)),
        color = colors.mintAccent,
        trackColor = colors.card,
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Preparing local workspace...",
        style = MaterialTheme.typography.labelSmall,
        color = colors.textSecondary,
      )

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}
