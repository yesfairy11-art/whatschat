package com.example.core.connection

import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import kotlinx.coroutines.flow.StateFlow

interface ConnectionManager {
  val mode: StateFlow<ConnectionMode>
  val status: StateFlow<ConnectionStatus>
  val isInternetOnline: StateFlow<Boolean>
  val isBluetoothConnected: StateFlow<Boolean>

  fun getConnectionBadgeText(): String
}
