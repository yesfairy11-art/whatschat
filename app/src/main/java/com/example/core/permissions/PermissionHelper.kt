package com.example.core.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class WhatsChatFeaturePermission(
  val title: String,
  val rationale: String,
) {
  BLUETOOTH_DISCOVERY(
    title = "Nearby Bluetooth Discovery",
    rationale = "WhatsChat needs Bluetooth Scan and Connect permissions to discover nearby compatible WhatsChat devices without exposing your personal profile.",
  ),
  CAMERA_CAPTURE(
    title = "Camera Access",
    rationale = "Camera permission lets you take photos or join video previews directly inside conversations.",
  ),
  MICROPHONE_AUDIO(
    title = "Microphone Access",
    rationale = "Microphone permission is required for voice messages and call audio controls.",
  ),
  NOTIFICATIONS(
    title = "Notifications",
    rationale = "Notification permission alerts you about incoming Internet and nearby Bluetooth messages.",
  ),
}

object PermissionHelper {
  fun bluetoothPermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
      )
    } else {
      listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
      )
    }
  }

  fun cameraPermission(): String = Manifest.permission.CAMERA

  fun microphonePermission(): String = Manifest.permission.RECORD_AUDIO

  fun notificationPermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
      emptyList()
    }
  }

  fun hasAllPermissions(context: Context, permissions: List<String>): Boolean {
    if (permissions.isEmpty()) return true
    return permissions.all { perm ->
      ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
  }
}
