package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val WhatsChatDarkColorScheme = darkColorScheme(
  primary = WhatsChatGreen,
  onPrimary = WhatsChatBgPrimary,
  primaryContainer = WhatsChatOutgoingBubble,
  onPrimaryContainer = WhatsChatTextPrimary,
  secondary = WhatsChatMint,
  onSecondary = WhatsChatBgPrimary,
  secondaryContainer = WhatsChatElevated,
  onSecondaryContainer = WhatsChatTextPrimary,
  tertiary = WhatsChatBluetoothBlue,
  onTertiary = WhatsChatBgPrimary,
  background = WhatsChatBgPrimary,
  onBackground = WhatsChatTextPrimary,
  surface = WhatsChatBgSecondary,
  onSurface = WhatsChatTextPrimary,
  surfaceVariant = WhatsChatCard,
  onSurfaceVariant = WhatsChatTextSecondary,
  surfaceContainerLowest = WhatsChatBgPrimary,
  surfaceContainerLow = WhatsChatBgSecondary,
  surfaceContainer = WhatsChatCard,
  surfaceContainerHigh = WhatsChatElevated,
  outline = WhatsChatDivider,
  outlineVariant = WhatsChatDivider,
  error = WhatsChatError,
  onError = WhatsChatTextPrimary,
)

object WhatsChatThemeTokens {
  val colors: WhatsChatExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalWhatsChatColors.current
}

@Composable
fun WhatsChatTheme(
  content: @Composable () -> Unit,
) {
  val extendedColors = WhatsChatExtendedColors()
  CompositionLocalProvider(LocalWhatsChatColors provides extendedColors) {
    MaterialTheme(
      colorScheme = WhatsChatDarkColorScheme,
      typography = WhatsChatTypography,
      shapes = WhatsChatShapes,
      content = content,
    )
  }
}
