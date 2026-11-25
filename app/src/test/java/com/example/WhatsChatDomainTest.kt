package com.example

import com.example.core.common.UsernameValidator
import com.example.data.repository.MockWhatsChatRepository
import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.DeliveryStates
import com.example.domain.models.MessageTransport
import com.example.domain.models.MessageTypes
import com.example.domain.usecases.ConnectionRouterUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsChatDomainTest {

  @Test
  fun `username validator enforces rules and normalizes case`() {
    assertTrue(UsernameValidator.validate("@Alex_Kumar99").isValid)
    assertEquals("alex_kumar99", UsernameValidator.validate("@Alex_Kumar99").normalized)
    assertFalse(UsernameValidator.validate("ab").isValid)
    assertFalse(UsernameValidator.validate("bad name").isValid)
    assertFalse(UsernameValidator.validate("bad-name!").isValid)
    assertFalse(UsernameValidator.validate("a".repeat(31)).isValid)
  }

  @Test
  fun `automatic router prefers internet then bluetooth then pending`() {
    val router = ConnectionRouterUseCase()

    val internetDecision = router.resolveRoute(
      mode = ConnectionMode.AUTOMATIC,
      isInternetReachable = true,
      isBluetoothDeviceConnected = true,
    )
    assertEquals(MessageTransport.INTERNET, internetDecision.transport)
    assertEquals(DeliveryStates.DELIVERED, internetDecision.initialDeliveryState)

    val btFallbackDecision = router.resolveRoute(
      mode = ConnectionMode.AUTOMATIC,
      isInternetReachable = false,
      isBluetoothDeviceConnected = true,
    )
    assertEquals(MessageTransport.BLUETOOTH, btFallbackDecision.transport)
    assertEquals(ConnectionStatus.BLUETOOTH_CONNECTED, btFallbackDecision.effectiveStatus)

    val pendingDecision = router.resolveRoute(
      mode = ConnectionMode.AUTOMATIC,
      isInternetReachable = false,
      isBluetoothDeviceConnected = false,
    )
    assertEquals(DeliveryStates.PENDING, pendingDecision.initialDeliveryState)
    assertEquals(ConnectionStatus.OFFLINE, pendingDecision.effectiveStatus)
  }

  @Test
  fun `unified conversation stores both internet and bluetooth messages together`() = runTest {
    val repo = MockWhatsChatRepository()
    repo.setConnectionMode(ConnectionMode.INTERNET_ONLY)
    repo.sendMessage("conv_sarah", "Sent via Internet", MessageTypes.TEXT)

    repo.setConnectionMode(ConnectionMode.BLUETOOTH_ONLY)
    repo.sendMessage("conv_sarah", "Sent via Bluetooth", MessageTypes.TEXT)

    val messages = repo.messagesByConversation.value["conv_sarah"].orEmpty()
    assertTrue(messages.any { it.content == "Sent via Internet" && it.transport == MessageTransport.INTERNET })
    assertTrue(messages.any { it.content == "Sent via Bluetooth" && it.transport == MessageTransport.BLUETOOTH })
  }

  @Test
  fun `voice notes stickers gifs and picked media captions are persisted in chat`() = runTest {
    val repo = MockWhatsChatRepository()
    repo.sendMessage(
      conversationId = "conv_sarah",
      content = "Voice message",
      messageType = MessageTypes.AUDIO,
      durationSeconds = 12,
    )
    repo.sendMessage(
      conversationId = "conv_sarah",
      content = "Cool Vibes",
      messageType = MessageTypes.STICKER,
      stickerEmoji = "😎",
    )
    repo.sendMessage(
      conversationId = "conv_sarah",
      content = "Hello There!",
      messageType = MessageTypes.GIF,
      gifLabel = "Hello There!",
      stickerEmoji = "👋",
    )
    repo.sendMessage(
      conversationId = "conv_sarah",
      content = "Sunset caption",
      messageType = MessageTypes.IMAGE,
      mediaUri = "content://media/external/images/42",
    )

    val messages = repo.messagesByConversation.value["conv_sarah"].orEmpty()
    assertTrue(messages.any { it.messageType == MessageTypes.AUDIO && it.durationSeconds == 12 })
    assertTrue(messages.any { it.messageType == MessageTypes.STICKER && it.stickerEmoji == "😎" })
    assertTrue(messages.any { it.messageType == MessageTypes.GIF && it.gifLabel == "Hello There!" })
    assertTrue(
      messages.any {
        it.messageType == MessageTypes.IMAGE &&
          it.content == "Sunset caption" &&
          it.mediaUri == "content://media/external/images/42"
      },
    )
  }

  @Test
  fun `group avatar update is restricted to admins and chat settings update cleanly`() = runTest {
    val repo = MockWhatsChatRepository()
    // user_me is admin in conv_family_group
    val adminUpdated = repo.updateGroupAvatar("conv_family_group", "content://media/group_pic.jpg")
    assertTrue(adminUpdated)
    assertEquals(
      "content://media/group_pic.jpg",
      repo.conversations.value.first { it.id == "conv_family_group" }.avatarUri,
    )

    // Demote user_me (remove from adminIds via toggleGroupAdmin) and verify non-admin cannot update
    repo.toggleGroupAdmin("conv_family_group", "user_me")
    val nonAdminUpdated = repo.updateGroupAvatar("conv_family_group", "content://media/other.jpg")
    assertFalse(nonAdminUpdated)

    // Member tag & settings update
    repo.updateGroupMemberTag("conv_family_group", "user_me", "Lead Dev")
    repo.updateConversationSettings(
      conversationId = "conv_family_group",
      chatLock = true,
      restrictedChat = true,
      translateMessages = true,
      disappearingMessages = "24 hours",
      isFavourite = true,
    )
    val conv = repo.conversations.value.first { it.id == "conv_family_group" }
    assertEquals("Lead Dev", conv.memberTags["user_me"])
    assertTrue(conv.chatLock)
    assertTrue(conv.restrictedChat)
    assertTrue(conv.translateMessages)
    assertEquals("24 hours", conv.disappearingMessages)
    assertTrue(conv.isFavourite)
  }
}
