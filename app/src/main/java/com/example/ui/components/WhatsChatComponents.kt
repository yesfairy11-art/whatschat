package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.UserProfile
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun WhatsChatLogoBadge(
  size: Dp = 84.dp,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Box(
    modifier = modifier
      .size(size)
      .shadow(
        elevation = 18.dp,
        shape = CircleShape,
        ambientColor = colors.mintAccent,
        spotColor = colors.primaryGreen,
      )
      .clip(CircleShape)
      .background(
        Brush.linearGradient(
          colors = listOf(colors.mintAccent, colors.primaryGreen),
        ),
      )
      .border(2.dp, colors.mintAccent.copy(alpha = 0.6f), CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = Icons.Filled.ChatBubble,
      contentDescription = "WhatsChat Logo",
      tint = colors.backgroundPrimary,
      modifier = Modifier.size(size * 0.52f),
    )
  }
}

@Composable
fun WhatsChatPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  icon: ImageVector? = null,
  testTag: String = "primary_button",
) {
  val colors = WhatsChatThemeTokens.colors
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.98f else 1f,
    animationSpec = tween(120),
    label = "btn_scale",
  )

  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    interactionSource = interactionSource,
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Transparent,
      disabledContainerColor = colors.elevated,
      contentColor = colors.backgroundPrimary,
      disabledContentColor = colors.textDisabled,
    ),
    contentPadding = PaddingValues(0.dp),
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .scale(scale)
      .testTag(testTag),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          if (enabled) {
            Brush.horizontalGradient(listOf(colors.primaryGreen, colors.mintAccent))
          } else {
            Brush.horizontalGradient(listOf(colors.elevated, colors.elevated))
          },
        ),
      contentAlignment = Alignment.Center,
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(22.dp),
          color = colors.backgroundPrimary,
          strokeWidth = 2.5.dp,
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          if (icon != null) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = if (enabled) colors.backgroundPrimary else colors.textDisabled,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) colors.backgroundPrimary else colors.textDisabled,
          )
        }
      }
    }
  }
}

@Composable
fun WhatsChatSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isDanger: Boolean = false,
  icon: ImageVector? = null,
  testTag: String = "secondary_button",
) {
  val colors = WhatsChatThemeTokens.colors
  val borderColor = if (isDanger) colors.danger else colors.primaryGreen
  val textColor = if (isDanger) colors.danger else colors.mintAccent

  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.2.dp, if (enabled) borderColor else colors.divider),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = Color.Transparent,
      contentColor = textColor,
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag(testTag),
  ) {
    if (icon != null) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
    )
  }
}

@Composable
fun WhatsChatTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String = "",
  leadingIcon: ImageVector? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  errorText: String? = null,
  supportingText: String? = null,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  testTag: String = "text_field",
) {
  val colors = WhatsChatThemeTokens.colors
  Column(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(label, style = MaterialTheme.typography.bodySmall) },
      placeholder = if (placeholder.isNotEmpty()) {
        { Text(placeholder, color = colors.textDisabled) }
      } else {
        null
      },
      leadingIcon = leadingIcon?.let {
        {
          Icon(
            imageVector = it,
            contentDescription = null,
            tint = if (isError) colors.danger else colors.textSecondary,
          )
        }
      },
      trailingIcon = trailingContent,
      isError = isError,
      singleLine = singleLine,
      maxLines = maxLines,
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colors.card,
        unfocusedContainerColor = colors.backgroundSecondary,
        errorContainerColor = colors.backgroundSecondary,
        focusedBorderColor = colors.primaryGreen,
        unfocusedBorderColor = colors.divider,
        errorBorderColor = colors.danger,
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        cursorColor = colors.mintAccent,
        focusedLabelColor = colors.mintAccent,
        unfocusedLabelColor = colors.textSecondary,
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag),
    )
    if (isError && !errorText.isNullOrBlank()) {
      Text(
        text = errorText,
        color = colors.danger,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
      )
    } else if (!supportingText.isNullOrBlank()) {
      Text(
        text = supportingText,
        color = colors.textSecondary,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsChatTopBar(
  title: String,
  onBackClick: (() -> Unit)? = null,
  subtitle: String? = null,
  centered: Boolean = false,
  actions: @Composable RowScope.() -> Unit = {},
) {
  val colors = WhatsChatThemeTokens.colors
  if (centered) {
    CenterAlignedTopAppBar(
      title = {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          color = colors.textPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      },
      navigationIcon = {
        if (onBackClick != null) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("top_bar_back"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.textPrimary,
            )
          }
        }
      },
      actions = actions,
      colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = colors.backgroundSecondary,
      ),
    )
  } else {
    TopAppBar(
      title = {
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          if (!subtitle.isNullOrBlank()) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = colors.textSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      },
      navigationIcon = {
        if (onBackClick != null) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("top_bar_back"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.textPrimary,
            )
          }
        }
      },
      actions = actions,
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colors.backgroundSecondary,
      ),
    )
  }
}

@Composable
fun WhatsChatAvatar(
  displayName: String,
  size: Dp = 52.dp,
  avatarResName: String? = null,
  avatarUri: String? = null,
  isGroup: Boolean = false,
  isOnline: Boolean = false,
  showOnlineIndicator: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  val resId = when (avatarResName) {
    "img_avatar_alex" -> R.drawable.img_avatar_alex
    "img_avatar_sarah" -> R.drawable.img_avatar_sarah
    else -> null
  }
  val initials = remember(displayName) {
    displayName.trim()
      .split(" ")
      .filter { it.isNotBlank() }
      .take(2)
      .joinToString("") { it.first().uppercase() }
      .ifEmpty { "W" }
  }

  Box(modifier = modifier.size(size)) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(colors.elevated, colors.card),
          ),
        )
        .border(1.2.dp, colors.primaryGreen.copy(alpha = 0.35f), CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      when {
        !avatarUri.isNullOrBlank() -> {
          AsyncImage(
            model = avatarUri,
            contentDescription = "$displayName avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        }
        resId != null -> {
          Image(
            painter = painterResource(id = resId),
            contentDescription = "$displayName avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        }
        isGroup -> {
          Icon(
            imageVector = Icons.Filled.Group,
            contentDescription = "$displayName group",
            tint = colors.mintAccent,
            modifier = Modifier.size(size * 0.52f),
          )
        }
        else -> {
          Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = colors.mintAccent,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }

    if (showOnlineIndicator && isOnline && !isGroup) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size((size * 0.24f).coerceIn(10.dp, 16.dp))
          .clip(CircleShape)
          .background(colors.online)
          .border(2.dp, colors.backgroundSecondary, CircleShape),
      )
    }
  }
}

@Composable
fun ConnectionStatusPill(
  status: ConnectionStatus,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  val (dotColor, icon, label) = when (status) {
    ConnectionStatus.AUTOMATIC -> Triple(colors.mintAccent, Icons.Filled.Wifi, "Automatic")
    ConnectionStatus.INTERNET_CONNECTED -> Triple(colors.online, Icons.Filled.Wifi, "Internet Connected")
    ConnectionStatus.BLUETOOTH_CONNECTED -> Triple(colors.bluetoothAccent, Icons.Filled.Bluetooth, "Bluetooth Connected")
    ConnectionStatus.PENDING -> Triple(colors.warning, Icons.Filled.CloudOff, "Message Pending")
    ConnectionStatus.OFFLINE -> Triple(colors.danger, Icons.Filled.CloudOff, "Offline")
  }

  Surface(
    color = colors.card,
    shape = RoundedCornerShape(999.dp),
    border = BorderStroke(1.dp, dotColor.copy(alpha = 0.4f)),
    modifier = modifier
      .then(
        if (onClick != null) {
          Modifier.clickable(onClick = onClick)
        } else {
          Modifier
        },
      )
      .testTag("connection_status_pill"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(dotColor),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = dotColor,
        modifier = Modifier.size(14.dp),
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colors.textPrimary,
        fontWeight = FontWeight.Medium,
      )
    }
  }
}

enum class MainNavDestination(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
) {
  CHATS("Chats", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_chats"),
  CALLS("Calls", Icons.Filled.Call, Icons.Outlined.Call, "nav_calls"),
  CONTACTS("Contacts", Icons.Filled.People, Icons.Outlined.PeopleOutline, "nav_contacts"),
  ACCOUNT("Account", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, "nav_account"),
}

@Composable
fun WhatsChatBottomBar(
  currentDestination: MainNavDestination,
  currentUser: UserProfile? = null,
  onNavigate: (MainNavDestination) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.backgroundSecondary,
    tonalElevation = 0.dp,
    border = BorderStroke(0.5.dp, colors.divider),
  ) {
    NavigationBar(
      containerColor = colors.backgroundSecondary,
      modifier = Modifier.navigationBarsPadding(),
      tonalElevation = 0.dp,
    ) {
      MainNavDestination.entries.forEach { dest ->
        val selected = dest == currentDestination
        NavigationBarItem(
          selected = selected,
          onClick = { onNavigate(dest) },
          icon = {
            if (dest == MainNavDestination.ACCOUNT && currentUser != null) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) colors.mintAccent else colors.textSecondary.copy(alpha = 0.5f),
                    shape = CircleShape,
                  ),
                contentAlignment = Alignment.Center,
              ) {
                WhatsChatAvatar(
                  displayName = currentUser.displayName,
                  size = 24.dp,
                  avatarResName = currentUser.avatarResName,
                  avatarUri = currentUser.avatarUri,
                  showOnlineIndicator = false,
                )
              }
            } else {
              Icon(
                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                contentDescription = dest.label,
                modifier = Modifier.size(24.dp),
              )
            }
          },
          label = {
            Text(
              text = dest.label,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
              maxLines = 1,
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colors.mintAccent,
            selectedTextColor = colors.mintAccent,
            unselectedIconColor = colors.textSecondary,
            unselectedTextColor = colors.textSecondary,
            indicatorColor = colors.primaryGreen.copy(alpha = 0.16f),
          ),
          modifier = Modifier.testTag(dest.testTag),
        )
      }
    }
  }
}

@Composable
fun SettingsRowItem(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  iconTint: Color = WhatsChatThemeTokens.colors.mintAccent,
  trailingValue: String? = null,
  showChevron: Boolean = true,
  onClick: () -> Unit,
  testTag: String = "settings_row",
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(testTag),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(iconTint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp),
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          color = colors.textPrimary,
          fontWeight = FontWeight.Medium,
        )
        if (!subtitle.isNullOrBlank()) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
      if (!trailingValue.isNullOrBlank()) {
        Text(
          text = trailingValue,
          style = MaterialTheme.typography.bodySmall,
          color = colors.mintAccent,
          modifier = Modifier.padding(end = 6.dp),
        )
      }
      if (showChevron) {
        Icon(
          imageVector = Icons.Filled.ChevronRight,
          contentDescription = null,
          tint = colors.textSecondary,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
fun SettingsSwitchRow(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String = "settings_switch_row",
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .testTag(testTag),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(colors.primaryGreen.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = colors.mintAccent,
          modifier = Modifier.size(20.dp),
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          color = colors.textPrimary,
          fontWeight = FontWeight.Medium,
        )
        if (!subtitle.isNullOrBlank()) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
          )
        }
      }
      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = colors.backgroundPrimary,
          checkedTrackColor = colors.primaryGreen,
          uncheckedThumbColor = colors.textSecondary,
          uncheckedTrackColor = colors.elevated,
        ),
      )
    }
  }
}

@Composable
fun WhatsChatEmptyState(
  icon: ImageVector,
  title: String,
  description: String,
  actionLabel: String? = null,
  onAction: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(colors.card)
        .border(1.dp, colors.divider, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = colors.mintAccent,
        modifier = Modifier.size(38.dp),
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = colors.textPrimary,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = colors.textSecondary,
      textAlign = TextAlign.Center,
    )
    if (!actionLabel.isNullOrBlank() && onAction != null) {
      Spacer(modifier = Modifier.height(20.dp))
      WhatsChatPrimaryButton(
        text = actionLabel,
        onClick = onAction,
        modifier = Modifier.fillMaxWidth(0.75f),
      )
    }
  }
}

@Composable
fun WhatsChatConfirmDialog(
  title: String,
  message: String,
  confirmLabel: String,
  isDestructive: Boolean = false,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.card,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = colors.textPrimary,
      )
    },
    text = {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textSecondary,
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDestructive) colors.danger else colors.primaryGreen,
          contentColor = if (isDestructive) colors.textPrimary else colors.backgroundPrimary,
        ),
        shape = RoundedCornerShape(12.dp),
      ) {
        Text(confirmLabel)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = colors.textSecondary)
      }
    },
  )
}

@Composable
fun MockPreviewNoticeBanner(
  text: String,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.elevated.copy(alpha = 0.85f),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, colors.divider),
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Filled.Info,
        contentDescription = null,
        tint = colors.mintAccent,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = colors.textSecondary,
      )
    }
  }
}

@Composable
fun WhatsChatCheckCircle(selected: Boolean) {
  val colors = WhatsChatThemeTokens.colors
  Box(
    modifier = Modifier
      .size(24.dp)
      .clip(CircleShape)
      .background(if (selected) colors.primaryGreen else Color.Transparent)
      .border(
        width = 1.6.dp,
        color = if (selected) colors.primaryGreen else colors.textSecondary,
        shape = CircleShape,
      ),
    contentAlignment = Alignment.Center,
  ) {
    if (selected) {
      Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = "Selected",
        tint = colors.backgroundPrimary,
        modifier = Modifier.size(16.dp),
      )
    }
  }
}
