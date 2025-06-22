package com.example.drinkapp.models
import kotlin.math.*


data class Person(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val gender: Gender,
    val age: Int,
    val weightKg: Double,
    val heightCm: Int
){
    fun getBMI(): Double = weightKg / ((heightCm / 100.0).pow(2))
}

enum class Gender(val distributionCoefficient: Double) {
    MALE(0.68),
    FEMALE(0.55)
}

    fun Gender.displayName(): String {
        return when (this) {
            Gender.MALE -> "Mężczyzna"
            Gender.FEMALE -> "Kobieta"
    }
}