package com.example.core.security

/**
 * Security contracts prepared for future end-to-end encrypted Internet & Bluetooth transport.
 * No credentials, tokens, or keys are hardcoded in the client.
 */
interface MessageCipherContract {
  suspend fun encryptPayload(plainBytes: ByteArray, peerPublicKeyId: String): Result<ByteArray>
  suspend fun decryptPayload(cipherBytes: ByteArray, senderPublicKeyId: String): Result<ByteArray>
}

interface DeviceTrustContract {
  suspend fun isDeviceAuthorized(deviceId: String): Boolean
  suspend fun authorizeDeviceSession(deviceId: String, verificationCode: String): Boolean
  suspend fun revokeDeviceAuthorization(deviceId: String)
}
