package com.example.core.common

data class UsernameValidationResult(
  val isValid: Boolean,
  val normalized: String,
  val errorMessage: String? = null,
)

enum class PasswordStrength(val label: String, val fraction: Float) {
  EMPTY("", 0f),
  WEAK("Weak", 0.33f),
  MEDIUM("Medium", 0.66f),
  STRONG("Strong", 1f),
}

object UsernameValidator {
  private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]{3,30}$")

  fun normalize(input: String): String {
    return input.trim().removePrefix("@").lowercase()
  }

  fun validate(rawInput: String): UsernameValidationResult {
    val trimmed = rawInput.trim().removePrefix("@")
    if (trimmed.isEmpty()) {
      return UsernameValidationResult(false, "", "Username is required")
    }
    if (trimmed.contains(" ")) {
      return UsernameValidationResult(false, trimmed.lowercase(), "Username cannot contain spaces")
    }
    if (trimmed.length < 3) {
      return UsernameValidationResult(false, trimmed.lowercase(), "Minimum 3 characters required")
    }
    if (trimmed.length > 30) {
      return UsernameValidationResult(false, trimmed.lowercase(), "Maximum 30 characters allowed")
    }
    if (!USERNAME_REGEX.matches(trimmed)) {
      return UsernameValidationResult(
        false,
        trimmed.lowercase(),
        "Only letters, numbers, and underscores (_) allowed",
      )
    }
    return UsernameValidationResult(true, trimmed.lowercase(), null)
  }

  fun evaluatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.EMPTY
    var score = 0
    if (password.length >= 6) score++
    if (password.length >= 10) score++
    if (password.any { it.isDigit() } && password.any { it.isLetter() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return when {
      score <= 1 -> PasswordStrength.WEAK
      score <= 2 -> PasswordStrength.MEDIUM
      else -> PasswordStrength.STRONG
    }
  }
}
