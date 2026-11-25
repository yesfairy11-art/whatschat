package com.example.features.nearby

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.NearbyDeviceItem
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatSecondaryButton
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun BluetoothConnectionScreen(
  device: NearbyDeviceItem,
  onBackClick: () -> Unit,
  onStartConnect: () -> Unit,
  onCancelConnect: () -> Unit,
  onDisconnect: () -> Unit,
  onOpenChatWithLinkedUser: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val isConnected = device.connectionState == BluetoothConnectionState.CONNECTED
  val isConnecting = device.connectionState == BluetoothConnectionState.CONNECTING ||
    device.connectionState == BluetoothConnectionState.PAIRING

  LaunchedEffect(device.id) {
    if (device.connectionState == BluetoothConnectionState.DISCONNECTED) {
      onStartConnect()
    }
  }

  val pulseTransition = rememberInfiniteTransition(label = "bt_connect")
  val ringScale by pulseTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(850, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "bt_ring_scale",
  )

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Bluetooth Connection",
        onBackClick = onBackClick,
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding(),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 520.dp)
          .fillMaxSize()
          .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
          modifier = Modifier
            .size(124.dp)
            .scale(if (isConnecting) ringScale else 1f)
            .clip(CircleShape)
            .background(
              if (isConnected) {
                colors.primaryGreen.copy(alpha = 0.2f)
              } else {
                colors.bluetoothAccent.copy(alpha = 0.2f)
              },
            )
            .border(
              2.dp,
              if (isConnected) colors.mintAccent else colors.bluetoothAccent,
              CircleShape,
            ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = if (isConnected) Icons.Filled.BluetoothConnected else Icons.Filled.Bluetooth,
            contentDescription = "Bluetooth Device",
            tint = if (isConnected) colors.mintAccent else colors.bluetoothAccent,
            modifier = Modifier.size(56.dp),
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = device.deviceName,
          style = MaterialTheme.typography.headlineSmall,
          color = colors.textPrimary,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = when (device.connectionState) {
            BluetoothConnectionState.CONNECTING -> "Establishing RFCOMM channel..."
            BluetoothConnectionState.PAIRING -> "Verifying WhatsChat device authorization..."
            BluetoothConnectionState.CONNECTED -> "Connected · Direct nearby link ready"
            else -> "Ready to connect"
          },
          style = MaterialTheme.typography.bodyMedium,
          color = if (isConnected) colors.mintAccent else colors.textSecondary,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (isConnecting) {
          LinearProgressIndicator(
            color = colors.bluetoothAccent,
            trackColor = colors.card,
            modifier = Modifier
              .fillMaxWidth(0.7f)
              .height(6.dp)
              .clip(RoundedCornerShape(999.dp)),
          )
        } else {
          Surface(
            color = colors.card,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.divider),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(
                text = "Compatibility: ${device.compatibilityLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textPrimary,
              )
              Text(
                text = "Signal: ${device.signalStrengthDbm} dBm · Direct peer-to-peer (no mesh)",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        MockPreviewNoticeBanner(
          text = "Mock Bluetooth handshake for frontend preview. Real RFCOMM socket transport will be wired in the next phase.",
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isConnected) {
          WhatsChatPrimaryButton(
            text = "Open Unified Chat",
            icon = Icons.Filled.ChatBubble,
            onClick = onOpenChatWithLinkedUser,
            testTag = "bt_connected_open_chat",
          )
          Spacer(modifier = Modifier.height(12.dp))
          WhatsChatSecondaryButton(
            text = "Disconnect",
            isDanger = true,
            onClick = onDisconnect,
            testTag = "bt_connected_disconnect",
          )
        } else {
          WhatsChatSecondaryButton(
            text = "Cancel",
            onClick = {
              onCancelConnect()
              onBackClick()
            },
            testTag = "bt_connecting_cancel",
          )
        }
      }
    }
  }
}
