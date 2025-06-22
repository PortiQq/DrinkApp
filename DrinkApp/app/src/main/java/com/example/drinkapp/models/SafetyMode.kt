package com.example.drinkapp.models

enum class SafetyMode(
    val displayName: String,
    val multiplier: Double,
    val emoji: String,
    val description: String
) {
    SAFE(
        displayName = "Ultra Safe",
        multiplier = 1.0,
        emoji = "🛡️",
        description = "100% of calculated time"
    ),
    BALANCED(
        displayName = "Safe & Fun",
        multiplier = 0.5,
        emoji = "⚖️",
        description = "50% of calculated time"
    ),
    PARTY(
        displayName = "Party Mode",
        multiplier = 0.25,
        emoji = "🎉",
        description = "25% of calculated time"
    )
}