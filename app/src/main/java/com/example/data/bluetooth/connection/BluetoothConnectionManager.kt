package com.example.data.bluetooth.connection

import com.example.domain.models.BluetoothConnectionState
import kotlinx.coroutines.flow.Flow

/**
 * Bluetooth connection management contract prepared for Bluetooth Classic RFCOMM
 * session establishment, user-consented pairing/authorization, and disconnect/forget controls.
 * Multi-hop or mesh forwarding is explicitly excluded.
 */
interface BluetoothConnectionManager {
  fun observeConnectionState(deviceId: String): Flow<BluetoothConnectionState>
  suspend fun requestConnection(deviceId: String): Result<Unit>
  suspend fun authorizeSession(deviceId: String): Boolean
  suspend fun disconnect(deviceId: String)
  suspend fun forgetDevice(deviceId: String)
}
