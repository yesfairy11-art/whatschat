package com.example.features.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.WhatsChatLogoBadge
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatSecondaryButton
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun WelcomeScreen(
  onCreateAccountClick: () -> Unit,
  onLoginClick: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
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
        .widthIn(max = 560.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        WhatsChatLogoBadge(size = 40.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "WhatsChat",
          style = MaterialTheme.typography.titleLarge,
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Image(
        painter = painterResource(id = R.drawable.img_welcome_illustration),
        contentDescription = "WhatsChat dual connection illustration",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
          .clip(RoundedCornerShape(20.dp)),
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Welcome to WhatsChat",
        style = MaterialTheme.typography.displaySmall,
        color = colors.textPrimary,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "One App. Two Ways to Connect. Chat over the Internet or directly via nearby Bluetooth in one unified timeline.",
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textSecondary,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Internet & Bluetooth feature highlights
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        FeatureHighlightCard(
          icon = Icons.Filled.Wifi,
          title = "Internet",
          subtitle = "Cloud & WebSockets",
          accentColor = colors.mintAccent,
          modifier = Modifier.weight(1f),
        )
        FeatureHighlightCard(
          icon = Icons.Filled.Bluetooth,
          title = "Bluetooth",
          subtitle = "Direct Nearby Link",
          accentColor = colors.bluetoothAccent,
          modifier = Modifier.weight(1f),
        )
      }

      Spacer(modifier = Modifier.weight(1f))
      Spacer(modifier = Modifier.height(28.dp))

      WhatsChatPrimaryButton(
        text = "Create Account",
        onClick = onCreateAccountClick,
        testTag = "welcome_create_account_button",
      )

      Spacer(modifier = Modifier.height(12.dp))

      WhatsChatSecondaryButton(
        text = "Log In",
        onClick = onLoginClick,
        testTag = "welcome_login_button",
      )

      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
private fun FeatureHighlightCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  accentColor: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, colors.divider),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(accentColor.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(20.dp),
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = colors.textPrimary,
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = colors.textSecondary,
        )
      }
    }
  }
}
