package com.example.domain.usecases

import com.example.domain.models.ConnectionMode
import com.example.domain.models.ConnectionStatus
import com.example.domain.models.DeliveryStates
import com.example.domain.models.MessageTransport

data class TransportRouteDecision(
  val transport: String,
  val initialDeliveryState: String,
  val effectiveStatus: ConnectionStatus,
)

/**
 * Encapsulates WhatsChat's unified connection routing policy:
 * 1. Automatic: Prefer Internet when reachable; fallback to authorized nearby Bluetooth;
 *    otherwise save locally as PENDING for idempotent sync/retry.
 * 2. Internet Only: Deliver over Internet or mark PENDING.
 * 3. Bluetooth Only: Deliver over connected nearby Bluetooth or mark PENDING.
 */
class ConnectionRouterUseCase {
  fun resolveRoute(
    mode: ConnectionMode,
    isInternetReachable: Boolean,
    isBluetoothDeviceConnected: Boolean,
  ): TransportRouteDecision {
    return when (mode) {
      ConnectionMode.AUTOMATIC -> {
        when {
          isInternetReachable -> TransportRouteDecision(
            transport = MessageTransport.INTERNET,
            initialDeliveryState = DeliveryStates.DELIVERED,
            effectiveStatus = ConnectionStatus.INTERNET_CONNECTED,
          )
          isBluetoothDeviceConnected -> TransportRouteDecision(
            transport = MessageTransport.BLUETOOTH,
            initialDeliveryState = DeliveryStates.DELIVERED,
            effectiveStatus = ConnectionStatus.BLUETOOTH_CONNECTED,
          )
          else -> TransportRouteDecision(
            transport = MessageTransport.INTERNET,
            initialDeliveryState = DeliveryStates.PENDING,
            effectiveStatus = ConnectionStatus.OFFLINE,
          )
        }
      }

      ConnectionMode.INTERNET_ONLY -> {
        if (isInternetReachable) {
          TransportRouteDecision(
            transport = MessageTransport.INTERNET,
            initialDeliveryState = DeliveryStates.DELIVERED,
            effectiveStatus = ConnectionStatus.INTERNET_CONNECTED,
          )
        } else {
          TransportRouteDecision(
            transport = MessageTransport.INTERNET,
            initialDeliveryState = DeliveryStates.PENDING,
            effectiveStatus = ConnectionStatus.OFFLINE,
          )
        }
      }

      ConnectionMode.BLUETOOTH_ONLY -> {
        if (isBluetoothDeviceConnected) {
          TransportRouteDecision(
            transport = MessageTransport.BLUETOOTH,
            initialDeliveryState = DeliveryStates.DELIVERED,
            effectiveStatus = ConnectionStatus.BLUETOOTH_CONNECTED,
          )
        } else {
          TransportRouteDecision(
            transport = MessageTransport.BLUETOOTH,
            initialDeliveryState = DeliveryStates.PENDING,
            effectiveStatus = ConnectionStatus.OFFLINE,
          )
        }
      }
    }
  }
}
