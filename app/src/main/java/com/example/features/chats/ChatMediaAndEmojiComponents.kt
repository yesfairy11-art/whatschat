package com.example.features.chats

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.models.ChatMessage
import com.example.domain.models.MessageTypes
import com.example.ui.components.WhatsChatAvatar
import com.example.ui.theme.WhatsChatThemeTokens
import kotlinx.coroutines.delay

enum class EmojiPanelTab {
  EMOJIS,
  GIF,
  STICKERS,
}

data class PendingPickedMedia(
  val uri: String,
  val isVideo: Boolean,
)

data class GifOption(
  val id: String,
  val label: String,
  val emoji: String,
  val gradientStart: Color,
  val gradientEnd: Color,
)

data class StickerOption(
  val id: String,
  val emoji: String,
  val label: String,
)

private val defaultEmojiList = listOf(
  "😀", "😃", "😄", "😁", "😆", "🥹", "😅", "😂",
  "🤣", "🥲", "😊", "😇", "🙂", "🙃", "😉", "😌",
  "😍", "🥰", "😘", "😎", "🤩", "🥳", "🤔", "🫡",
  "👍", "👎", "👏", "🙌", "🤝", "🙏", "✌️", "🤞",
  "❤️", "💚", "🩵", "🔥", "✨", "🎉", "🌿", "💯",
  "🚀", "📱", "🎧", "☕", "🌙", "⚡", "🛡️", "💬",
)

private val gifCatalog = listOf(
  GifOption("g1", "Hello There!", "👋", Color(0xFF0A6C58), Color(0xFF16E6A0)),
  GifOption("g2", "Awesome Work", "🔥", Color(0xFF1A3A5C), Color(0xFF38BDF8)),
  GifOption("g3", "Haha LOL!", "😂", Color(0xFF4A2545), Color(0xFFEC4899)),
  GifOption("g4", "On My Way!", "🚀", Color(0xFF134E4A), Color(0xFF2DD4BF)),
  GifOption("g5", "100% Agreed", "💯", Color(0xFF065F46), Color(0xFF34D399)),
  GifOption("g6", "Party Time", "🥳", Color(0xFF3B2F63), Color(0xFFA78BFA)),
  GifOption("g7", "Bluetooth Sync", "📶", Color(0xFF1E3A8A), Color(0xFF60A5FA)),
  GifOption("g8", "Sending Love", "💚", Color(0xFF064E3B), Color(0xFF00C982)),
)

private val stickerCatalog = listOf(
  StickerOption("s1", "😎", "Cool Vibes"),
  StickerOption("s2", "🤝", "Deal!"),
  StickerOption("s3", "🥳", "Celebrate"),
  StickerOption("s4", "❤️", "Big Love"),
  StickerOption("s5", "🔥", "Fire!"),
  StickerOption("s6", "😂", "Cracking Up"),
  StickerOption("s7", "☕", "Coffee Break"),
  StickerOption("s8", "⚡", "Fast Reply"),
  StickerOption("s9", "🌿", "WhatsChat"),
  StickerOption("s10", "🙏", "Thank You"),
  StickerOption("s11", "🫡", "Aye Sir!"),
  StickerOption("s12", "🎉", "Congrats!"),
)

@Composable
fun ChatWallpaperPattern(modifier: Modifier = Modifier) {
  val colors = WhatsChatThemeTokens.colors
  val tint = colors.textSecondary.copy(alpha = 0.045f)
  Canvas(modifier = modifier.fillMaxSize()) {
    val stepX = 110f
    val stepY = 110f
    var row = 0
    var y = 30f
    while (y < size.height + stepY) {
      var col = 0
      var x = if (row % 2 == 0) 24f else 74f
      while (x < size.width + stepX) {
        when ((row + col) % 4) {
          0 -> drawCircle(color = tint, radius = 16f, center = Offset(x, y), style = Stroke(2.2f))
          1 -> drawRoundRect(
            color = tint,
            topLeft = Offset(x - 14f, y - 10f),
            size = androidx.compose.ui.geometry.Size(28f, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
            style = Stroke(2.2f),
          )
          2 -> {
            drawLine(tint, Offset(x - 10f, y), Offset(x + 10f, y), strokeWidth = 2.2f)
            drawLine(tint, Offset(x, y - 10f), Offset(x, y + 10f), strokeWidth = 2.2f)
          }
          else -> drawCircle(color = tint, radius = 6f, center = Offset(x, y))
        }
        x += stepX
        col++
      }
      y += stepY
      row++
    }
  }
}

@Composable
fun EmojiGifStickerDrawer(
  recentEmojis: List<String>,
  onEmojiSelected: (String) -> Unit,
  onBackspace: () -> Unit,
  onGifSelected: (GifOption) -> Unit,
  onStickerSelected: (StickerOption) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var activeTab by remember { mutableStateOf(EmojiPanelTab.EMOJIS) }
  var showSearch by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }

  Surface(
    color = colors.backgroundSecondary,
    border = BorderStroke(0.5.dp, colors.divider),
    modifier = Modifier
      .fillMaxWidth()
      .height(300.dp)
      .testTag("emoji_gif_sticker_drawer"),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top segmented bar matching reference: Search | [Emoji | GIF | Stickers] | Backspace
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        IconButton(
          onClick = {
            showSearch = !showSearch
            if (!showSearch) searchQuery = ""
          },
          modifier = Modifier.size(38.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search emojis, GIFs, or stickers",
            tint = if (showSearch) colors.mintAccent else colors.textSecondary,
          )
        }

        Surface(
          color = colors.card,
          shape = RoundedCornerShape(999.dp),
          border = BorderStroke(1.dp, colors.divider),
        ) {
          Row(modifier = Modifier.padding(3.dp)) {
            EmojiTabSegment(
              selected = activeTab == EmojiPanelTab.EMOJIS,
              onClick = { activeTab = EmojiPanelTab.EMOJIS },
              testTag = "drawer_tab_emojis",
            ) {
              Icon(
                imageVector = Icons.Filled.EmojiEmotions,
                contentDescription = "Emojis",
                tint = if (activeTab == EmojiPanelTab.EMOJIS) colors.textPrimary else colors.textSecondary,
                modifier = Modifier.size(18.dp),
              )
            }
            EmojiTabSegment(
              selected = activeTab == EmojiPanelTab.GIF,
              onClick = { activeTab = EmojiPanelTab.GIF },
              testTag = "drawer_tab_gif",
            ) {
              Text(
                text = "GIF",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (activeTab == EmojiPanelTab.GIF) colors.textPrimary else colors.textSecondary,
              )
            }
            EmojiTabSegment(
              selected = activeTab == EmojiPanelTab.STICKERS,
              onClick = { activeTab = EmojiPanelTab.STICKERS },
              testTag = "drawer_tab_stickers",
            ) {
              Icon(
                imageVector = Icons.Filled.StickyNote2,
                contentDescription = "Stickers",
                tint = if (activeTab == EmojiPanelTab.STICKERS) colors.textPrimary else colors.textSecondary,
                modifier = Modifier.size(18.dp),
              )
            }
          }
        }

        IconButton(
          onClick = onBackspace,
          modifier = Modifier
            .size(38.dp)
            .testTag("drawer_backspace"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace",
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp),
          )
        }
      }

      if (showSearch) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = when (activeTab) {
                EmojiPanelTab.EMOJIS -> "Filter emojis..."
                EmojiPanelTab.GIF -> "Search WhatsChat GIFs..."
                EmojiPanelTab.STICKERS -> "Search Stickers..."
              },
              color = colors.textDisabled,
            )
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.card,
            unfocusedContainerColor = colors.card,
            focusedBorderColor = colors.primaryGreen,
            unfocusedBorderColor = colors.divider,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        )
      }

      when (activeTab) {
        EmojiPanelTab.EMOJIS -> {
          Column(modifier = Modifier.weight(1f)) {
            if (recentEmojis.isNotEmpty() && searchQuery.isBlank()) {
              Text(
                text = "Recents",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                recentEmojis.take(8).forEach { emoji ->
                  Text(
                    text = emoji,
                    fontSize = 26.sp,
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { onEmojiSelected(emoji) }
                      .padding(4.dp),
                  )
                }
              }
            }

            Text(
              text = "Smileys & People",
              style = MaterialTheme.typography.labelMedium,
              color = colors.textSecondary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            )

            LazyVerticalGrid(
              columns = GridCells.Fixed(8),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.weight(1f),
            ) {
              items(defaultEmojiList) { emoji ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEmojiSelected(emoji) }
                    .padding(vertical = 4.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  Text(text = emoji, fontSize = 25.sp)
                }
              }
            }

            // Bottom emoji category strip matching reference
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(colors.backgroundPrimary.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              listOf(
                Icons.Filled.Schedule,
                Icons.Filled.EmojiEmotions,
                Icons.Filled.Pets,
                Icons.Filled.Favorite,
                Icons.Filled.SportsSoccer,
                Icons.Filled.DirectionsCar,
                Icons.Filled.Lightbulb,
                Icons.Filled.Flag,
              ).forEachIndexed { index, icon ->
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = if (index == 1) colors.mintAccent else colors.textSecondary,
                  modifier = Modifier.size(18.dp),
                )
              }
            }
          }
        }

        EmojiPanelTab.GIF -> {
          val filteredGifs = remember(searchQuery) {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) gifCatalog else gifCatalog.filter { it.label.lowercase().contains(q) }
          }
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
          ) {
            items(filteredGifs, key = { it.id }) { gif ->
              Surface(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(92.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .clickable { onGifSelected(gif) }
                  .testTag("gif_option_${gif.id}"),
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(gif.gradientStart, gif.gradientEnd)))
                    .padding(10.dp),
                ) {
                  Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopStart),
                  ) {
                    Text(
                      text = "GIF",
                      style = MaterialTheme.typography.labelSmall,
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                  }
                  Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                  ) {
                    Text(text = gif.emoji, fontSize = 26.sp)
                    Text(
                      text = gif.label,
                      style = MaterialTheme.typography.labelMedium,
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                    )
                  }
                }
              }
            }
          }
        }

        EmojiPanelTab.STICKERS -> {
          val filteredStickers = remember(searchQuery) {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) stickerCatalog else stickerCatalog.filter { it.label.lowercase().contains(q) }
          }
          LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
          ) {
            items(filteredStickers, key = { it.id }) { sticker ->
              Surface(
                color = colors.card,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { onStickerSelected(sticker) }
                  .testTag("sticker_option_${sticker.id}"),
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                ) {
                  Text(text = sticker.emoji, fontSize = 32.sp)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = sticker.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmojiTabSegment(
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String,
  content: @Composable () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(999.dp))
      .background(if (selected) colors.elevated else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}

@Composable
fun VoiceRecordingComposerBar(
  elapsedSeconds: Int,
  onCancel: () -> Unit,
  onSend: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val pulseTransition = rememberInfiniteTransition(label = "rec_pulse")
  val dotAlpha by pulseTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
    label = "rec_dot_alpha",
  )
  val formatted = "%d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      color = colors.card,
      shape = RoundedCornerShape(26.dp),
      border = BorderStroke(1.dp, colors.primaryGreen.copy(alpha = 0.45f)),
      modifier = Modifier.weight(1f),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = onCancel,
          modifier = Modifier
            .size(32.dp)
            .testTag("voice_record_cancel"),
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Discard voice recording",
            tint = colors.danger,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(colors.danger.copy(alpha = dotAlpha)),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = formatted,
          style = MaterialTheme.typography.titleSmall,
          color = colors.textPrimary,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Recording voice note...",
          style = MaterialTheme.typography.bodySmall,
          color = colors.textSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
    Spacer(modifier = Modifier.width(8.dp))
    Surface(
      color = colors.primaryGreen,
      shape = CircleShape,
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .clickable(onClick = onSend)
        .testTag("voice_record_send"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send voice message",
          tint = colors.backgroundPrimary,
          modifier = Modifier.size(22.dp),
        )
      }
    }
  }
}

@Composable
fun VoiceNotePlayerContent(
  message: ChatMessage,
  isOutgoing: Boolean,
) {
  val colors = WhatsChatThemeTokens.colors
  var isPlaying by remember(message.messageId) { mutableStateOf(false) }
  var progress by remember(message.messageId) { mutableFloatStateOf(0f) }

  val totalSeconds = remember(message) {
    if (message.durationSeconds > 0) message.durationSeconds else 8
  }

  LaunchedEffect(isPlaying, message.messageId) {
    if (isPlaying) {
      val stepMs = 100L
      val stepFraction = 0.1f / totalSeconds.coerceAtLeast(1)
      while (isPlaying && progress < 1f) {
        delay(stepMs)
        progress = (progress + stepFraction).coerceAtMost(1f)
      }
      if (progress >= 1f) {
        isPlaying = false
        progress = 0f
      }
    }
  }

  val displaySeconds = if (isPlaying || progress > 0f) {
    (progress * totalSeconds).toInt().coerceAtMost(totalSeconds)
  } else {
    totalSeconds
  }
  val formattedTime = "%d:%02d".format(displaySeconds / 60, displaySeconds % 60)

  val bars = remember(message.messageId) {
    listOf(6, 12, 18, 10, 22, 14, 24, 16, 10, 20, 14, 24, 18, 12, 20, 16, 8, 18, 14, 22, 12, 16, 10, 8)
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (!isOutgoing) {
      VoiceAvatarWithMicBadge(
        name = message.senderName.ifBlank { "User" },
        avatarResName = message.senderAvatarResName,
      )
      Spacer(modifier = Modifier.width(8.dp))
    }

    IconButton(
      onClick = { isPlaying = !isPlaying },
      modifier = Modifier
        .size(36.dp)
        .testTag("voice_play_${message.messageId}"),
    ) {
      Icon(
        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
        contentDescription = if (isPlaying) "Pause voice note" else "Play voice note",
        tint = colors.textPrimary,
        modifier = Modifier.size(28.dp),
      )
    }

    Spacer(modifier = Modifier.width(4.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Box(
          modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(Color(0xFF38BDF8)),
        )
        bars.forEachIndexed { idx, h ->
          val barFraction = idx.toFloat() / bars.size.toFloat()
          val played = barFraction <= progress && (isPlaying || progress > 0f)
          Box(
            modifier = Modifier
              .width(3.dp)
              .height(h.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(
                if (played) Color(0xFF38BDF8) else colors.textSecondary.copy(alpha = 0.65f),
              ),
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = formattedTime,
        style = MaterialTheme.typography.labelSmall,
        color = colors.textSecondary,
      )
    }

    if (isOutgoing) {
      Spacer(modifier = Modifier.width(8.dp))
      VoiceAvatarWithMicBadge(
        name = message.senderName.ifBlank { "You" },
        avatarResName = message.senderAvatarResName,
      )
    }
  }
}

@Composable
private fun VoiceAvatarWithMicBadge(
  name: String,
  avatarResName: String?,
) {
  val colors = WhatsChatThemeTokens.colors
  Box(modifier = Modifier.size(40.dp)) {
    WhatsChatAvatar(
      displayName = name,
      size = 38.dp,
      avatarResName = avatarResName,
      showOnlineIndicator = false,
    )
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .size(16.dp)
        .clip(CircleShape)
        .background(colors.backgroundSecondary),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = Icons.Filled.Mic,
        contentDescription = null,
        tint = Color(0xFF38BDF8),
        modifier = Modifier.size(11.dp),
      )
    }
  }
}

@Composable
fun PickedMediaCaptionDialog(
  media: PendingPickedMedia,
  onDismiss: () -> Unit,
  onSend: (caption: String) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var caption by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      color = Color.Black,
      modifier = Modifier.fillMaxSize(),
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .imePadding(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close preview", tint = Color.White)
          }
          Text(
            text = if (media.isVideo) "Video Preview" else "Photo Preview",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
          )
          Spacer(modifier = Modifier.width(48.dp))
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          contentAlignment = Alignment.Center,
        ) {
          AsyncImage(
            model = media.uri,
            contentDescription = "Selected media preview",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(16.dp)),
          )
          if (media.isVideo) {
            Surface(
              color = Color.Black.copy(alpha = 0.65f),
              shape = CircleShape,
              border = BorderStroke(1.5.dp, colors.mintAccent),
              modifier = Modifier.size(64.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Videocam,
                  contentDescription = "Selected video",
                  tint = colors.mintAccent,
                  modifier = Modifier.size(32.dp),
                )
              }
            }
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.78f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            color = colors.card,
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, colors.divider),
            modifier = Modifier.weight(1f),
          ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
              if (caption.isEmpty()) {
                Text(
                  text = "Add a caption...",
                  style = MaterialTheme.typography.bodyLarge,
                  color = colors.textDisabled,
                )
              }
              BasicTextField(
                value = caption,
                onValueChange = { caption = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.mintAccent),
                maxLines = 3,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("media_caption_input"),
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Surface(
            color = colors.primaryGreen,
            shape = CircleShape,
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .clickable { onSend(caption) }
              .testTag("media_caption_send"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send media",
                tint = colors.backgroundPrimary,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun FullScreenMediaPreviewDialog(
  message: ChatMessage,
  onDismiss: () -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  val isVideo = message.messageType == MessageTypes.VIDEO
  var isPlaying by remember(message.messageId) { mutableStateOf(isVideo) }
  var progress by remember(message.messageId) { mutableFloatStateOf(0f) }
  val totalSeconds = if (message.durationSeconds > 0) message.durationSeconds else 18

  LaunchedEffect(isPlaying, message.messageId) {
    if (isVideo && isPlaying) {
      while (isPlaying && progress < 1f) {
        delay(150L)
        progress = (progress + 0.15f / totalSeconds).coerceAtMost(1f)
      }
      if (progress >= 1f) {
        isPlaying = false
        progress = 0f
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      color = Color.Black,
      modifier = Modifier
        .fillMaxSize()
        .testTag("fullscreen_media_preview"),
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close preview", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = message.senderName.ifBlank { "Shared Media" },
              style = MaterialTheme.typography.titleSmall,
              color = Color.White,
            )
            Text(
              text = message.formattedTime.ifBlank { "Just now" },
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center,
        ) {
          if (!message.mediaUri.isNullOrBlank()) {
            AsyncImage(
              model = message.mediaUri,
              contentDescription = message.content.ifBlank { "Media preview" },
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxSize(),
            )
          } else {
            val resId = when (message.mediaResName) {
              "img_welcome_illustration" -> R.drawable.img_welcome_illustration
              "img_avatar_sarah" -> R.drawable.img_avatar_sarah
              "img_avatar_alex" -> R.drawable.img_avatar_alex
              else -> R.drawable.img_sample_landscape
            }
            Image(
              painter = painterResource(id = resId),
              contentDescription = message.content.ifBlank { "Media preview" },
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxSize(),
            )
          }

          if (isVideo) {
            Surface(
              color = Color.Black.copy(alpha = 0.68f),
              shape = CircleShape,
              border = BorderStroke(1.5.dp, colors.mintAccent),
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .clickable { isPlaying = !isPlaying },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                  contentDescription = if (isPlaying) "Pause video" else "Play video",
                  tint = colors.mintAccent,
                  modifier = Modifier.size(36.dp),
                )
              }
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
          if (isVideo) {
            val elapsed = (progress * totalSeconds).toInt()
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "%d:%02d".format(elapsed / 60, elapsed % 60),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
              )
              Slider(
                value = progress,
                onValueChange = { progress = it },
                colors = SliderDefaults.colors(
                  thumbColor = colors.mintAccent,
                  activeTrackColor = colors.mintAccent,
                  inactiveTrackColor = colors.divider,
                ),
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 8.dp),
              )
              Text(
                text = "%d:%02d".format(totalSeconds / 60, totalSeconds % 60),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
              )
            }
          }

          if (message.content.isNotBlank()) {
            Text(
              text = message.content,
              style = MaterialTheme.typography.bodyLarge,
              color = Color.White,
              textAlign = TextAlign.Start,
            )
          }
        }
      }
    }
  }
}
