package com.example.features.nearby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.NearbyDeviceItem
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun BluetoothDeviceManagementScreen(
  devices: List<NearbyDeviceItem>,
  onBackClick: () -> Unit,
  onConnectDevice: (String) -> Unit,
  onDisconnectDevice: (String) -> Unit,
  onForgetDevice: (String) -> Unit,
  onScanNearbyClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var disconnectConfirmTarget by remember { mutableStateOf<NearbyDeviceItem?>(null) }
  var forgetConfirmTarget by remember { mutableStateOf<NearbyDeviceItem?>(null) }

  val pairedDevices = remember(devices) { devices.filter { it.isPaired } }
  val previousDevices = remember(devices) { devices.filterNot { it.isPaired } }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Bluetooth Devices",
        subtitle = "Paired & previously connected",
        onBackClick = onBackClick,
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      if (devices.isEmpty()) {
        WhatsChatEmptyState(
          icon = Icons.Filled.Bluetooth,
          title = "No Managed Devices",
          description = "Scan nearby to authorize and connect compatible WhatsChat devices.",
          actionLabel = "Scan Nearby",
          onAction = onScanNearbyClick,
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          item {
            Text(
              text = "Paired & Authorized (${pairedDevices.size})",
              style = MaterialTheme.typography.titleSmall,
              color = colors.mintAccent,
            )
          }
          items(pairedDevices, key = { it.id }) { device ->
            ManagedDeviceCard(
              device = device,
              onConnect = { onConnectDevice(device.id) },
              onDisconnect = { disconnectConfirmTarget = device },
              onForget = { forgetConfirmTarget = device },
            )
          }

          if (previousDevices.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Available & Previously Seen (${previousDevices.size})",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textSecondary,
              )
            }
            items(previousDevices, key = { it.id }) { device ->
              ManagedDeviceCard(
                device = device,
                onConnect = { onConnectDevice(device.id) },
                onDisconnect = { disconnectConfirmTarget = device },
                onForget = { forgetConfirmTarget = device },
              )
            }
          }
        }
      }
    }
  }

  disconnectConfirmTarget?.let { target ->
    WhatsChatConfirmDialog(
      title = "Disconnect Device?",
      message = "Messages queued while disconnected will stay PENDING until Internet or Bluetooth reconnects.",
      confirmLabel = "Disconnect",
      isDestructive = true,
      onConfirm = {
        onDisconnectDevice(target.id)
        disconnectConfirmTarget = null
      },
      onDismiss = { disconnectConfirmTarget = null },
    )
  }

  forgetConfirmTarget?.let { target ->
    WhatsChatConfirmDialog(
      title = "Forget ${target.deviceName}?",
      message = "This revokes Bluetooth authorization for this device. You can pair again later.",
      confirmLabel = "Forget Device",
      isDestructive = true,
      onConfirm = {
        onForgetDevice(target.id)
        forgetConfirmTarget = null
      },
      onDismiss = { forgetConfirmTarget = null },
    )
  }
}

@Composable
private fun ManagedDeviceCard(
  device: NearbyDeviceItem,
  onConnect: () -> Unit,
  onDisconnect: () -> Unit,
  onForget: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val isConnected = device.connectionState == BluetoothConnectionState.CONNECTED

  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, if (isConnected) colors.primaryGreen else colors.divider),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(
            if (isConnected) {
              colors.primaryGreen.copy(alpha = 0.2f)
            } else {
              colors.bluetoothAccent.copy(alpha = 0.14f)
            },
          ),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = if (isConnected) Icons.Filled.BluetoothConnected else Icons.Filled.Bluetooth,
          contentDescription = null,
          tint = if (isConnected) colors.mintAccent else colors.bluetoothAccent,
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = device.deviceName,
          style = MaterialTheme.typography.titleSmall,
          color = colors.textPrimary,
        )
        Text(
          text = if (isConnected) "Connected" else "Last seen: ${device.lastConnectedText}",
          style = MaterialTheme.typography.bodySmall,
          color = if (isConnected) colors.mintAccent else colors.textSecondary,
        )
      }

      Button(
        onClick = if (isConnected) onDisconnect else onConnect,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isConnected) colors.elevated else colors.primaryGreen,
          contentColor = if (isConnected) colors.textPrimary else colors.backgroundPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("bt_manage_action_${device.id}"),
      ) {
        Text(
          text = if (isConnected) "Disconnect" else "Connect",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
        )
      }

      if (device.isPaired) {
        IconButton(
          onClick = onForget,
          modifier = Modifier.testTag("bt_forget_${device.id}"),
        ) {
          Icon(
            imageVector = Icons.Filled.DeleteOutline,
            contentDescription = "Forget device",
            tint = colors.danger,
          )
        }
      }
    }
  }
}
