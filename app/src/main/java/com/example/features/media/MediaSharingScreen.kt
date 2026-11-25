package com.example.features.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.models.MediaCategory
import com.example.domain.models.SampleMediaItem
import com.example.ui.components.WhatsChatCheckCircle
import com.example.ui.components.WhatsChatPrimaryButton
import com.example.ui.components.WhatsChatTopBar
import com.example.ui.theme.WhatsChatThemeTokens

@Composable
fun MediaSharingScreen(
  mediaItems: List<SampleMediaItem>,
  onCloseClick: () -> Unit,
  onSendSelected: (items: List<SampleMediaItem>, caption: String) -> Unit,
) {
  val colors = WhatsChatThemeTokens.colors
  var selectedCategory by remember { mutableStateOf(MediaCategory.PHOTOS) }
  var selectedIds by remember { mutableStateOf<List<String>>(emptyList()) }
  var caption by remember { mutableStateOf("") }

  val filteredItems = remember(mediaItems, selectedCategory) {
    mediaItems.filter { it.category == selectedCategory }
  }
  val selectedItems = remember(mediaItems, selectedIds) {
    mediaItems.filter { it.id in selectedIds }
  }

  Scaffold(
    containerColor = colors.backgroundPrimary,
    topBar = {
      WhatsChatTopBar(
        title = "Share Media",
        subtitle = if (selectedIds.isEmpty()) "Select local sample attachments" else "${selectedIds.size} selected",
        onBackClick = onCloseClick,
        actions = {
          IconButton(onClick = onCloseClick, modifier = Modifier.testTag("media_close_button")) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = colors.textPrimary)
          }
        },
      )
    },
    bottomBar = {
      Surface(
        color = colors.backgroundSecondary,
        border = BorderStroke(0.5.dp, colors.divider),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
          if (selectedItems.isNotEmpty()) {
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "Preview: ${selectedItems.joinToString { it.title }}",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.mintAccent,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }

          OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("Add a caption (optional)...", color = colors.textDisabled) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = colors.card,
              unfocusedContainerColor = colors.card,
              focusedBorderColor = colors.primaryGreen,
              unfocusedBorderColor = colors.divider,
              focusedTextColor = colors.textPrimary,
              unfocusedTextColor = colors.textPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
          )

          Spacer(modifier = Modifier.height(10.dp))

          WhatsChatPrimaryButton(
            text = if (selectedItems.isEmpty()) "Select Media to Send" else "Send (${selectedItems.size})",
            icon = Icons.AutoMirrored.Filled.Send,
            enabled = selectedItems.isNotEmpty(),
            onClick = { onSendSelected(selectedItems, caption) },
            testTag = "media_send_button",
          )
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      // Tabs: Photos, Videos, Documents, Audio
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(colors.backgroundSecondary)
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        MediaCategory.entries.forEach { category ->
          val active = category == selectedCategory
          Surface(
            color = if (active) colors.primaryGreen.copy(alpha = 0.2f) else colors.card,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (active) colors.primaryGreen else colors.divider),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedCategory = category }
              .testTag("media_tab_${category.name.lowercase()}"),
          ) {
            Text(
              text = category.label,
              style = MaterialTheme.typography.labelSmall,
              color = if (active) colors.mintAccent else colors.textSecondary,
              fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
              maxLines = 1,
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
          }
        }
      }

      if (selectedCategory == MediaCategory.PHOTOS || selectedCategory == MediaCategory.VIDEOS) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
          val columns = if (maxWidth >= 600.dp) 3 else 2
          LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
          ) {
            items(filteredItems, key = { it.id }) { item ->
              val isSelected = item.id in selectedIds
              val resId = when (item.previewResName) {
                "img_welcome_illustration" -> R.drawable.img_welcome_illustration
                "img_avatar_sarah" -> R.drawable.img_avatar_sarah
                else -> R.drawable.img_sample_landscape
              }
              Surface(
                color = colors.card,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) colors.primaryGreen else colors.divider,
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    selectedIds = if (isSelected) selectedIds - item.id else selectedIds + item.id
                  }
                  .testTag("media_item_${item.id}"),
              ) {
                Column {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(126.dp),
                  ) {
                    Image(
                      painter = painterResource(id = resId),
                      contentDescription = item.title,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    )
                    if (item.category == MediaCategory.VIDEOS) {
                      Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = "Video",
                        tint = colors.textPrimary,
                        modifier = Modifier
                          .align(Alignment.Center)
                          .size(38.dp),
                      )
                    }
                    Box(
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    ) {
                      WhatsChatCheckCircle(selected = isSelected)
                    }
                  }
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                      text = item.title,
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.textPrimary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = "${item.sizeText} · ${item.subtitle}",
                      style = MaterialTheme.typography.labelSmall,
                      color = colors.textSecondary,
                      maxLines = 1,
                    )
                  }
                }
              }
            }
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize(),
        ) {
          items(filteredItems, key = { it.id }) { item ->
            val isSelected = item.id in selectedIds
            Surface(
              color = colors.card,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.primaryGreen else colors.divider,
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  selectedIds = if (isSelected) selectedIds - item.id else selectedIds + item.id
                }
                .testTag("media_item_${item.id}"),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.elevated),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = if (item.category == MediaCategory.AUDIO) {
                      Icons.Filled.AudioFile
                    } else {
                      Icons.Filled.Description
                    },
                    contentDescription = item.title,
                    tint = colors.mintAccent,
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = "${item.sizeText} · ${item.subtitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                  )
                }
                WhatsChatCheckCircle(selected = isSelected)
              }
            }
          }
        }
      }
    }
  }
}
