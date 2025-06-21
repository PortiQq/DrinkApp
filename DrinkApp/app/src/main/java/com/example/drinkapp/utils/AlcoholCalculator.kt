package com.example.drinkapp.utils

import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Drink

object AlcoholCalculator {
    private const val METABOLISM_RATE_BAC_PER_HOUR = 0.015 // Standard BAC reduction per hour
    private const val SAFETY_MARGIN_MULTIPLIER = 1.2 // 20% safety buffer

    /**
     * Calculate Blood Alcohol Content using Widmark formula
     */
    fun calculateBAC(
        alcoholGrams: Double,
        person: Person,
        hoursElapsed: Double = 0.0
    ): Double {
        val bac = (alcoholGrams / (person.weightKg * person.gender.distributionCoefficient)) -
                (METABOLISM_RATE_BAC_PER_HOUR * hoursElapsed)
        return maxOf(0.0, bac)
    }

    /**
     * Calculate safe wait time in minutes before next drink
     */
    fun calculateSafeWaitTimeMinutes(person: Person, drink: Drink): Int {
        val alcoholGrams = drink.getAlcoholGrams()
        val peakBAC = calculateBAC(alcoholGrams, person)

        // Time to metabolize to near-zero BAC (hours)
        val metabolizeTimeHours = (peakBAC / METABOLISM_RATE_BAC_PER_HOUR) * SAFETY_MARGIN_MULTIPLIER

        return (metabolizeTimeHours * 60).toInt() // Convert to minutes
    }

    /**
     * Get safety level based on current estimated BAC
     */
    fun getSafetyLevel(estimatedBAC: Double): SafetyLevel {
        return when {
            estimatedBAC <= 0.02 -> SafetyLevel.SAFE
            estimatedBAC <= 0.05 -> SafetyLevel.CAUTION
            estimatedBAC <= 0.08 -> SafetyLevel.WARNING
            else -> SafetyLevel.DANGER
        }
    }
}

enum class SafetyLevel(val color: Int, val message: String) {
    SAFE(android.graphics.Color.GREEN, "Safe to drink"),
    CAUTION(android.graphics.Color.YELLOW, "Proceed with caution"),
    WARNING(android.graphics.Color.rgb(255, 165, 0), "Not recommended"),
    DANGER(android.graphics.Color.RED, "Dangerous - do not drink")
}