package com.example.drinkapp.utils

import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Drink

object AlcoholCalculator {
    private const val METABOLISM_RATE_BAC_PER_HOUR = 0.015 // Standard
    private const val SAFETY_MARGIN_MULTIPLIER = 1.0 // buffer

    /**
     * Calculate Blood Alcohol Content using Widmark formula
     * BAC = (grams of alcohol) / (body weight in kg × gender coefficient)
     * Result is in g/100ml
     */
    private fun calculateBAC(
        alcoholGrams: Double,
        person: Person,
    ): Double {
        val bloodAlcoholContent = alcoholGrams / (person.weightKg * person.gender.distributionCoefficient * 10.0)
        return maxOf(0.0, bloodAlcoholContent)
    }

    /**
     * Calculate time to drink
     */
    fun calculateSafeWaitTimeMinutes(person: Person, drink: Drink): Int {
        val alcoholGrams = drink.getAlcoholGrams()
        val peakBAC = calculateBAC(alcoholGrams, person)

        // Time to metabolize alcohol
        val metabolizeTimeHours = (peakBAC / METABOLISM_RATE_BAC_PER_HOUR) * SAFETY_MARGIN_MULTIPLIER

        return (metabolizeTimeHours * 60).toInt() // minutes
    }
}