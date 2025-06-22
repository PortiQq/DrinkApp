package com.example.drinkapp.models

enum class SafetyMode(
    val displayName: String,
    val multiplier: Double,
    val emoji: String,
) {
    SAFE(
        displayName = "Bezpieczeństwo",
        multiplier = 1.0,
        emoji = "🛡️",
    ),
    BALANCED(
        displayName = "Balans",
        multiplier = 0.5,
        emoji = "⚖️",
    ),
    PARTY(
        displayName = "Impreza!",
        multiplier = 0.25,
        emoji = "🎉",
    )
}