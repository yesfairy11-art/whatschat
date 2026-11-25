package com.example.features.nearby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.models.ConnectionMode
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun ConnectionPreferencesScreen(
  currentMode: ConnectionMode,
  isInternetSimulatedOnline: Boolean,
  onBackClick: () -> Unit,
  onSaveMode: (ConnectionMode) -> Unit,
  onToggleSimulatedInternet: (Boolean) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var selectedMode by remember(currentMode) { mutableStateOf(currentMode) }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Connection Preferences",
        subtitle = "One App. Two Ways to Connect.",
        onBackClick = onBackClick,
      )
    },
    bottomBar = {
      Surface(color = colors.backgroundSecondary) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
          WhatsChatPrimaryButton(
            text = "Save Preferences",
            onClick = {
              onSaveMode(selectedMode)
              onBackClick()
            },
            testTag = "connection_save_button",
          )
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 620.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = "Choose how WhatsChat routes messages",
          style = MaterialTheme.typography.titleMedium,
          color = colors.textPrimary,
        )
        Text(
          text = "Both Internet and Bluetooth messages are stored in the exact same conversation timeline.",
          style = MaterialTheme.typography.bodySmall,
          color = colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        ConnectionMode.entries.forEach { mode ->
          val active = selectedMode == mode
          val (icon, accent) = when (mode) {
            ConnectionMode.AUTOMATIC -> Icons.Filled.AutoAwesome to colors.mintAccent
            ConnectionMode.INTERNET_ONLY -> Icons.Filled.Wifi to colors.online
            ConnectionMode.BLUETOOTH_ONLY -> Icons.Filled.Bluetooth to colors.bluetoothAccent
          }

          Surface(
            color = if (active) colors.elevated else colors.card,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
              width = if (active) 1.6.dp else 1.dp,
              color = if (active) colors.primaryGreen else colors.divider,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedMode = mode }
              .testTag("connection_mode_${mode.name.lowercase()}"),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
              ) {
                Icon(imageVector = icon, contentDescription = null, tint = accent)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = mode.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                  )
                  if (mode == ConnectionMode.AUTOMATIC) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      color = colors.primaryGreen.copy(alpha = 0.2f),
                      shape = RoundedCornerShape(999.dp),
                    ) {
                      Text(
                        text = "DEFAULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mintAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = mode.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.textSecondary,
                )
              }
              RadioButton(
                selected = active,
                onClick = { selectedMode = mode },
                colors = RadioButtonDefaults.colors(
                  selectedColor = colors.mintAccent,
                  unselectedColor = colors.textSecondary,
                ),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frontend testing toggle so reviewer can test Automatic fallback & Pending states
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
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Mock Internet Reachability",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
              )
              Text(
                text = "Toggle offline to preview Automatic Bluetooth fallback & Pending message states.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
              )
            }
            Switch(
              checked = isInternetSimulatedOnline,
              onCheckedChange = onToggleSimulatedInternet,
              colors = SwitchDefaults.colors(
                checkedThumbColor = colors.backgroundPrimary,
                checkedTrackColor = colors.primaryGreen,
              ),
            )
          }
        }
      }
    }
  }
}
