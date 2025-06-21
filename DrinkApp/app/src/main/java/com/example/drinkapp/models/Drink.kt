package com.example.drinkapp.models

data class Drink(
    val name: String,
    val alcoholPercentage: Double,
    val volumeMl: Int,
    val category: DrinkCategory = DrinkCategory.OTHER
)  {
    fun getAlcoholGrams(): Double {
        // Calculate pure alcohol volume in ml
        val alcoholVolumeMl = (alcoholPercentage / 100.0) * volumeMl

        // Convert to grams using ethanol density (0.789 g/ml)
        return alcoholVolumeMl * 0.789
    }

    companion object {
        val COMMON_DRINKS = listOf(
            Drink("Beer", 5.0, 500, DrinkCategory.BEER),
            Drink("Wine", 12.0, 150, DrinkCategory.WINE),
            Drink("Vodka Shot", 40.0, 40, DrinkCategory.SPIRIT),
            Drink("Whiskey Shot", 40.0, 40, DrinkCategory.SPIRIT),
            Drink("Cocktail", 15.0, 200, DrinkCategory.COCKTAIL)
        )
    }
}

enum class DrinkCategory {
    BEER, WINE, SPIRIT, COCKTAIL, OTHER
}