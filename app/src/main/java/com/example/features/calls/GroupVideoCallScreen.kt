package com.example.features.calls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.models.VideoCallParticipant
import com.example.ui.components.MockPreviewNoticeBanner
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.theme.WhatsChatThemeTokens
import kotlinx.coroutines.delay

@Composable
fun GroupVideoCallScreen(
  callTitle: String,
  participants: List<VideoCallParticipant>,
  onEndCall: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  // Enforce max 4 participants including caller
  val cappedParticipants = remember(participants) { participants.take(4) }

  var isMicMuted by remember { mutableStateOf(false) }
  var isCameraOff by remember { mutableStateOf(false) }
  var isSpeakerOn by remember { mutableStateOf(true) }
  var isFrontCamera by remember { mutableStateOf(true) }
  var elapsedSeconds by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      elapsedSeconds++
    }
  }

  val formattedTimer = remember(elapsedSeconds) {
    val mins = elapsedSeconds / 60
    val secs = elapsedSeconds % 60
    "%02d:%02d".format(mins, secs)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.backgroundPrimary)
      .statusBarsPadding()
      .navigationBarsPadding(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
      // Top Call Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onEndCall, modifier = Modifier.testTag("video_call_back")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Leave call",
            tint = colors.textPrimary,
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = callTitle,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
          )
          Text(
            text = "${cappedParticipants.size}/4 participants · $formattedTimer · Mock Preview",
            style = MaterialTheme.typography.labelSmall,
            color = colors.mintAccent,
          )
        }
        Surface(
          color = colors.card,
          shape = RoundedCornerShape(999.dp),
          border = BorderStroke(1.dp, colors.divider),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Filled.SignalCellularAlt,
              contentDescription = "Connection quality",
              tint = colors.online,
              modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "HD Mock",
              style = MaterialTheme.typography.labelSmall,
              color = colors.textPrimary,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      MockPreviewNoticeBanner(
        text = "Mock Video Call UI (max 4 participants). Live WebRTC / LiveKit media streams will be integrated in the backend phase.",
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Adaptive 2x2 or 4-across grid based on orientation/width
      BoxWithConstraints(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
      ) {
        val isWide = maxWidth > maxHeight
        if (isWide) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            cappedParticipants.forEachIndexed { idx, p ->
              val mutedState = if (idx == 0) isMicMuted else p.isMuted
              val camOffState = if (idx == 0) isCameraOff else p.isCameraOff
              ParticipantVideoTile(
                participant = p.copy(isMuted = mutedState, isCameraOff = camOffState),
                isSelf = idx == 0,
                isFrontCamera = isFrontCamera,
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight(),
              )
            }
          }
        } else {
          val topRow = cappedParticipants.take(2)
          val bottomRow = cappedParticipants.drop(2)
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              topRow.forEachIndexed { idx, p ->
                val mutedState = if (idx == 0) isMicMuted else p.isMuted
                val camOffState = if (idx == 0) isCameraOff else p.isCameraOff
                ParticipantVideoTile(
                  participant = p.copy(isMuted = mutedState, isCameraOff = camOffState),
                  isSelf = idx == 0,
                  isFrontCamera = isFrontCamera,
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                )
              }
            }
            if (bottomRow.isNotEmpty()) {
              Row(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                bottomRow.forEach { p ->
                  ParticipantVideoTile(
                    participant = p,
                    isSelf = false,
                    isFrontCamera = true,
                    modifier = Modifier
                      .weight(1f)
                      .fillMaxHeight(),
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom Control Bar
      Surface(
        color = colors.card,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, colors.divider),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CallControlCircle(
            icon = if (isMicMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
            label = if (isMicMuted) "Unmute" else "Mute",
            active = !isMicMuted,
            onClick = { isMicMuted = !isMicMuted },
            testTag = "video_call_mic",
          )
          CallControlCircle(
            icon = if (isCameraOff) Icons.Filled.VideocamOff else Icons.Filled.Videocam,
            label = if (isCameraOff) "Cam Off" else "Camera",
            active = !isCameraOff,
            onClick = { isCameraOff = !isCameraOff },
            testTag = "video_call_camera",
          )
          CallControlCircle(
            icon = Icons.Filled.Cameraswitch,
            label = if (isFrontCamera) "Front" else "Rear",
            active = true,
            onClick = { isFrontCamera = !isFrontCamera },
            testTag = "video_call_switch_cam",
          )
          CallControlCircle(
            icon = Icons.Filled.VolumeUp,
            label = if (isSpeakerOn) "Speaker" else "Earpiece",
            active = isSpeakerOn,
            onClick = { isSpeakerOn = !isSpeakerOn },
            testTag = "video_call_speaker",
          )
          Surface(
            color = colors.danger,
            shape = CircleShape,
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .clickable(onClick = onEndCall)
              .testTag("video_call_end"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.CallEnd,
                contentDescription = "End Video Call",
                tint = colors.textPrimary,
                modifier = Modifier.size(26.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ParticipantVideoTile(
  participant: VideoCallParticipant,
  isSelf: Boolean,
  isFrontCamera: Boolean,
  modifier: Modifier = Modifier,
) {
  val colors = WhatsChatThemeTokens.colors
  Surface(
    color = colors.card,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(
      width = if (participant.isSpeaking) 2.dp else 1.dp,
      color = if (participant.isSpeaking) colors.mintAccent else colors.divider,
    ),
    modifier = modifier,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(colors.elevated.copy(alpha = 0.7f), colors.backgroundSecondary),
          ),
        ),
    ) {
      Column(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        WhatsChatAvatar(
          displayName = participant.name,
          size = 68.dp,
          avatarResName = participant.avatarResName,
          showOnlineIndicator = false,
        )
        if (participant.isCameraOff) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Camera Off",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
          )
        } else if (isSelf) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (isFrontCamera) "Front Cam Mock" else "Rear Cam Mock",
            style = MaterialTheme.typography.labelSmall,
            color = colors.mintAccent,
          )
        }
      }

      // Name & Mic badge overlay at bottom
      Row(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .fillMaxWidth()
          .background(colors.backgroundPrimary.copy(alpha = 0.72f))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (isSelf) "${participant.name} (You)" else participant.name,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
          )
          Text(
            text = "@${participant.username}",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
          )
        }
        Icon(
          imageVector = if (participant.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
          contentDescription = null,
          tint = if (participant.isMuted) colors.danger else colors.mintAccent,
          modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
private fun CallControlCircle(
  icon: ImageVector,
  label: String,
  active: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  val colors = WhatsChatThemeTokens.colors
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 6.dp, vertical = 4.dp)
      .testTag(testTag),
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(if (active) colors.elevated else colors.backgroundSecondary)
        .border(1.dp, if (active) colors.primaryGreen.copy(alpha = 0.5f) else colors.divider, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (active) colors.textPrimary else colors.danger,
        modifier = Modifier.size(22.dp),
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
