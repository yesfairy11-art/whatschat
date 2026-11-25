package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Core WhatsChat Dark Teal & Emerald Palette
val WhatsChatBgPrimary = Color(0xFF031B1D)
val WhatsChatBgSecondary = Color(0xFF071F22)
val WhatsChatCard = Color(0xFF102D31)
val WhatsChatElevated = Color(0xFF183A3E)

val WhatsChatGreen = Color(0xFF00C982)
val WhatsChatMint = Color(0xFF16E6A0)
val WhatsChatGreenDark = Color(0xFF009B63)

val WhatsChatTextPrimary = Color(0xFFF5FAF9)
val WhatsChatTextSecondary = Color(0xFFA4B9B8)
val WhatsChatTextDisabled = Color(0xFF536A6D)

val WhatsChatIncomingBubble = Color(0xFF19383D)
val WhatsChatOutgoingBubble = Color(0xFF087D56)

val WhatsChatError = Color(0xFFF04452)
val WhatsChatWarning = Color(0xFFFFB547)
val WhatsChatOnline = Color(0xFF00D68F)
val WhatsChatDivider = Color(0xFF214044)
val WhatsChatBluetoothBlue = Color(0xFF2FA8FF)

@Immutable
data class WhatsChatExtendedColors(
  val backgroundPrimary: Color = WhatsChatBgPrimary,
  val backgroundSecondary: Color = WhatsChatBgSecondary,
  val card: Color = WhatsChatCard,
  val elevated: Color = WhatsChatElevated,
  val primaryGreen: Color = WhatsChatGreen,
  val mintAccent: Color = WhatsChatMint,
  val textPrimary: Color = WhatsChatTextPrimary,
  val textSecondary: Color = WhatsChatTextSecondary,
  val textDisabled: Color = WhatsChatTextDisabled,
  val incomingBubble: Color = WhatsChatIncomingBubble,
  val outgoingBubble: Color = WhatsChatOutgoingBubble,
  val danger: Color = WhatsChatError,
  val warning: Color = WhatsChatWarning,
  val online: Color = WhatsChatOnline,
  val divider: Color = WhatsChatDivider,
  val bluetoothAccent: Color = WhatsChatBluetoothBlue,
)

val LocalWhatsChatColors = staticCompositionLocalOf { WhatsChatExtendedColors() }
