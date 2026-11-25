package com.example.features.calls

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BluetoothAudio
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.models.CallState
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.theme.WhatsChatThemeTokens
import kotlinx.coroutines.delay

@Composable
fun VoiceCallScreen(
  contactName: String,
  username: String,
  avatarResName: String?,
  isGroup: Boolean,
  onEndCall: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var callState by remember { mutableStateOf(CallState.CALLING) }
  var isMuted by remember { mutableStateOf(false) }
  var isSpeakerOn by remember { mutableStateOf(false) }
  var isBluetoothAudio by remember { mutableStateOf(false) }
  var elapsedSeconds by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    delay(900)
    callState = CallState.CONNECTING
    delay(1100)
    callState = CallState.CONNECTED
    while (true) {
      delay(1000)
      elapsedSeconds++
    }
  }

  val formattedDuration = remember(elapsedSeconds, callState) {
    if (callState != CallState.CONNECTED) {
      "00:00"
    } else {
      "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)
    }
  }

  val waveTransition = rememberInfiniteTransition(label = "voice_wave")
  val ringScale by waveTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.12f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "avatar_ring",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.backgroundPrimary)
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 520.dp)
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onEndCall, modifier = Modifier.testTag("voice_call_back")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = colors.textPrimary,
          )
        }
        Text(
          text = if (isGroup) "Group Voice Call (Max 4)" else "Voice Call",
          style = MaterialTheme.typography.titleMedium,
          color = colors.textPrimary,
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      MockPreviewNoticeBanner(
        text = "Mock Voice Call UI. Actual voice calling is not active until WebRTC / LiveKit integration.",
      )

      Spacer(modifier = Modifier.weight(1f))

      Box(
        modifier = Modifier
          .size(164.dp)
          .scale(ringScale)
          .clip(CircleShape)
          .background(colors.primaryGreen.copy(alpha = 0.14f))
          .border(2.dp, colors.mintAccent.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        WhatsChatAvatar(
          displayName = contactName,
          size = 128.dp,
          avatarResName = avatarResName,
          isGroup = isGroup,
          showOnlineIndicator = false,
        )
      }

      Spacer(modifier = Modifier.height(22.dp))

      Text(
        text = contactName,
        style = MaterialTheme.typography.displaySmall,
        color = colors.textPrimary,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "@$username",
        style = MaterialTheme.typography.bodyMedium,
        color = colors.mintAccent,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "${callState.label} · $formattedDuration",
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textSecondary,
      )

      Spacer(modifier = Modifier.height(22.dp))

      // Animated waveform bars
      VoiceWaveformBars(active = callState == CallState.CONNECTED && !isMuted)

      Spacer(modifier = Modifier.weight(1f))

      Surface(
        color = colors.card,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.divider),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 12.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          VoiceActionButton(
            icon = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
            label = if (isMuted) "Muted" else "Mute",
            highlighted = !isMuted,
            onClick = { isMuted = !isMuted },
            testTag = "voice_call_mute",
          )
          VoiceActionButton(
            icon = Icons.Filled.VolumeUp,
            label = "Speaker",
            highlighted = isSpeakerOn,
            onClick = { isSpeakerOn = !isSpeakerOn },
            testTag = "voice_call_speaker",
          )
          VoiceActionButton(
            icon = Icons.Filled.BluetoothAudio,
            label = "Bluetooth",
            highlighted = isBluetoothAudio,
            onClick = { isBluetoothAudio = !isBluetoothAudio },
            testTag = "voice_call_bt_audio",
          )
          Surface(
            color = colors.danger,
            shape = CircleShape,
            modifier = Modifier
              .size(58.dp)
              .clip(CircleShape)
              .clickable(onClick = onEndCall)
              .testTag("voice_call_end"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.CallEnd,
                contentDescription = "End Call",
                tint = colors.textPrimary,
                modifier = Modifier.size(28.dp),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}

@Composable
private fun VoiceWaveformBars(active: Boolean) {
  val colors = WhatsChatThemeTokens.colors
  val transition = rememberInfiniteTransition(label = "waveform")
  val phase by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(700),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "wave_phase",
  )

  val baseHeights = listOf(12f, 24f, 16f, 32f, 20f, 28f, 14f, 22f, 12f)
  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(36.dp),
  ) {
    baseHeights.forEachIndexed { idx, base ->
      val factor = if (active) {
        if (idx % 2 == 0) 0.55f + phase * 0.55f else 1.1f - phase * 0.5f
      } else {
        0.35f
      }
      Box(
        modifier = Modifier
          .width(4.dp)
          .height((base * factor).dp)
          .clip(RoundedCornerShape(4.dp))
          .background(if (active) colors.mintAccent else colors.divider),
      )
    }
  }
}

@Composable
private fun VoiceActionButton(
  icon: ImageVector,
  label: String,
  highlighted: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  val colors = WhatsChatThemeTokens.colors
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(6.dp)
      .testTag(testTag),
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(if (highlighted) colors.primaryGreen.copy(alpha = 0.22f) else colors.elevated),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (highlighted) colors.mintAccent else colors.textSecondary,
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = colors.textSecondary,
    )
  }
}
