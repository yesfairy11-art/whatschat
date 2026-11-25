package com.example.features.nearby

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.permissions.PermissionHelper
import com.example.core.permissions.WhatsChatFeaturePermission
import com.example.domain.models.BluetoothConnectionState
import com.example.domain.models.NearbyDeviceItem
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatConfirmDialog
import com.example.ui.components.WhatsChatEmptyState
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatSecondaryButton
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun NearbyDevicesScreen(
  isBluetoothEnabled: Boolean,
  isScanning: Boolean,
  devices: List<NearbyDeviceItem>,
  onBackClick: () -> Unit,
  onToggleBluetooth: (Boolean) -> Unit,
  onStartScan: () -> Unit,
  onStopScan: () -> Unit,
  onConnectDevice: (String) -> Unit,
  onManageDevicesClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val context = LocalContext.current
  val requiredPerms = remember { PermissionHelper.bluetoothPermissions() }
  var hasPermission by remember {
    mutableStateOf(PermissionHelper.hasAllPermissions(context, requiredPerms))
  }
  var showPermissionRationale by remember { mutableStateOf(false) }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions(),
  ) { result ->
    val granted = result.values.all { it }
    hasPermission = granted
    if (granted) {
      onStartScan()
    } else {
      Toast.makeText(
        context,
        "Bluetooth scan permission is needed to discover nearby WhatsChat devices.",
        Toast.LENGTH_SHORT,
      ).show()
    }
  }

  fun triggerScanWithPermissionCheck() {
    if (!isBluetoothEnabled) {
      onToggleBluetooth(true)
    }
    if (PermissionHelper.hasAllPermissions(context, requiredPerms)) {
      hasPermission = true
      if (isScanning) onStopScan() else onStartScan()
    } else {
      showPermissionRationale = true
    }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Nearby Devices",
        subtitle = "Compatible WhatsChat Bluetooth nodes",
        onBackClick = onBackClick,
        actions = {
          IconButton(
            onClick = {
              if (!isScanning) triggerScanWithPermissionCheck() else onStartScan()
            },
            modifier = Modifier.testTag("nearby_refresh_button"),
          ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh scan", tint = colors.mintAccent)
          }
          IconButton(
            onClick = onManageDevicesClick,
            modifier = Modifier.testTag("nearby_manage_devices_button"),
          ) {
            Icon(Icons.Filled.Devices, contentDescription = "Manage Bluetooth devices", tint = colors.textPrimary)
          }
        },
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
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 640.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // Bluetooth Status Card
        item {
          Surface(
            color = colors.card,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.divider),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(
                    if (isBluetoothEnabled) {
                      colors.bluetoothAccent.copy(alpha = 0.18f)
                    } else {
                      colors.elevated
                    },
                  ),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = if (isBluetoothEnabled) {
                    Icons.Filled.Bluetooth
                  } else {
                    Icons.Filled.BluetoothDisabled
                  },
                  contentDescription = null,
                  tint = if (isBluetoothEnabled) colors.bluetoothAccent else colors.textSecondary,
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isBluetoothEnabled) "Bluetooth Ready" else "Bluetooth Disabled",
                  style = MaterialTheme.typography.titleSmall,
                  color = colors.textPrimary,
                )
                Text(
                  text = if (isScanning) {
                    "Scanning for WhatsChat RFCOMM devices..."
                  } else if (isBluetoothEnabled) {
                    "Tap Start Scanning to discover nearby devices"
                  } else {
                    "Enable Bluetooth to scan nearby devices"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                )
              }
              Switch(
                checked = isBluetoothEnabled,
                onCheckedChange = onToggleBluetooth,
                colors = SwitchDefaults.colors(
                  checkedThumbColor = colors.backgroundPrimary,
                  checkedTrackColor = colors.primaryGreen,
                ),
                modifier = Modifier.testTag("nearby_bluetooth_toggle"),
              )
            }
          }
        }

        // Radar animation + Scan button
        item {
          Surface(
            color = colors.backgroundSecondary,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.divider),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              RadarPulseIndicator(isScanning = isScanning && isBluetoothEnabled)
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = if (isScanning && isBluetoothEnabled) {
                  "Scanning for WhatsChat devices..."
                } else {
                  "Nearby Direct Connection"
                },
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Personal profile info stays hidden during discovery until you authorize connection.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
              )
              Spacer(modifier = Modifier.height(16.dp))
              if (!isBluetoothEnabled) {
                WhatsChatPrimaryButton(
                  text = "Enable Bluetooth",
                  icon = Icons.Filled.Bluetooth,
                  onClick = { onToggleBluetooth(true) },
                  testTag = "nearby_enable_bt_button",
                )
              } else if (isScanning) {
                WhatsChatSecondaryButton(
                  text = "Stop Scan",
                  isDanger = true,
                  onClick = onStopScan,
                  testTag = "nearby_stop_scan_button",
                )
              } else {
                WhatsChatPrimaryButton(
                  text = "Start Scanning",
                  icon = Icons.Filled.BluetoothSearching,
                  onClick = { triggerScanWithPermissionCheck() },
                  testTag = "nearby_start_scan_button",
                )
              }
            }
          }
        }

        if (!hasPermission) {
          item {
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, colors.warning.copy(alpha = 0.5f)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showPermissionRationale = true },
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.Security,
                  contentDescription = null,
                  tint = colors.warning,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Bluetooth permission not yet granted. Tap to review why WhatsChat needs Nearby Scan access.",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textPrimary,
                )
              }
            }
          }
        }

        item {
          MockPreviewNoticeBanner(
            text = "Frontend phase: Displaying sample compatible WhatsChat devices. Live RFCOMM/BLE transport will be wired in the hardware integration phase.",
          )
        }

        if (!isBluetoothEnabled || devices.isEmpty()) {
          item {
            WhatsChatEmptyState(
              icon = Icons.Filled.BluetoothDisabled,
              title = if (!isBluetoothEnabled) "Bluetooth is Off" else "No Compatible Devices Nearby",
              description = "Turn on Bluetooth and scan to discover nearby WhatsChat-compatible devices.",
              actionLabel = if (!isBluetoothEnabled) "Enable Bluetooth" else "Scan Again",
              onAction = { triggerScanWithPermissionCheck() },
            )
          }
        } else {
          item {
            Text(
              text = "Compatible WhatsChat Devices (${devices.size})",
              style = MaterialTheme.typography.titleSmall,
              color = colors.textSecondary,
            )
          }
          items(devices, key = { it.id }) { device ->
            NearbyDeviceRow(
              device = device,
              onConnectClick = { onConnectDevice(device.id) },
            )
          }
        }
      }
    }
  }

  if (showPermissionRationale) {
    val permInfo = WhatsChatFeaturePermission.BLUETOOTH_DISCOVERY
    WhatsChatConfirmDialog(
      title = permInfo.title,
      message = permInfo.rationale,
      confirmLabel = "Allow Bluetooth Scan",
      onConfirm = {
        showPermissionRationale = false
        permissionLauncher.launch(requiredPerms.toTypedArray())
      },
      onDismiss = { showPermissionRationale = false },
    )
  }
}

@Composable
private fun RadarPulseIndicator(isScanning: Boolean) {
  val colors = WhatsChatThemeTokens.colors
  val transition = rememberInfiniteTransition(label = "radar")
  val ring1 by transition.animateFloat(
    initialValue = 0.25f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "ring1",
  )

  Box(
    modifier = Modifier.size(116.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val maxRadius = size.minDimension / 2f
      drawCircle(
        color = colors.divider.copy(alpha = 0.6f),
        radius = maxRadius * 0.9f,
        style = Stroke(width = 2f),
      )
      drawCircle(
        color = colors.divider.copy(alpha = 0.5f),
        radius = maxRadius * 0.6f,
        style = Stroke(width = 2f),
      )
      if (isScanning) {
        drawCircle(
          color = colors.mintAccent.copy(alpha = (1f - ring1) * 0.7f),
          radius = maxRadius * ring1,
          style = Stroke(width = 4f),
        )
      }
    }
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .background(colors.primaryGreen.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = Icons.Filled.BluetoothSearching,
        contentDescription = "Bluetooth Radar",
        tint = colors.mintAccent,
        modifier = Modifier.size(28.dp),
      )
    }
  }
}

@Composable
private fun NearbyDeviceRow(
  device: NearbyDeviceItem,
  onConnectClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val isConnected = device.connectionState == BluetoothConnectionState.CONNECTED
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(
      1.dp,
      if (isConnected) colors.primaryGreen else colors.divider,
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("nearby_device_${device.id}"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(colors.bluetoothAccent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Filled.Bluetooth,
          contentDescription = null,
          tint = colors.bluetoothAccent,
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
          text = device.compatibilityLabel,
          style = MaterialTheme.typography.labelSmall,
          color = colors.mintAccent,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.SignalCellularAlt,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(13.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${device.signalStrengthDbm} dBm · ${device.connectionState.label}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
          )
        }
      }
      Button(
        onClick = onConnectClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isConnected) colors.elevated else colors.primaryGreen,
          contentColor = if (isConnected) colors.mintAccent else colors.backgroundPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.testTag("nearby_connect_${device.id}"),
      ) {
        Text(
          text = if (isConnected) "Manage" else "Connect",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}
