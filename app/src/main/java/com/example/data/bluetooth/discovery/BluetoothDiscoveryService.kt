package com.example.data.bluetooth.discovery

import com.example.domain.models.NearbyDeviceItem
import kotlinx.coroutines.flow.Flow

/**
 * Bluetooth discovery contract prepared for Android BLE/Classic discovery
 * filtering strictly for WhatsChat-compatible nearby devices.
 */
interface BluetoothDiscoveryService {
  val discoveredDevices: Flow<List<NearbyDeviceItem>>
  val isScanning: Flow<Boolean>

  suspend fun startDiscovery()
  suspend fun stopDiscovery()
}
