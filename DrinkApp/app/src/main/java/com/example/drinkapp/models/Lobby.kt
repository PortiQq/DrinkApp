package com.example.drinkapp.models

import com.example.drinkapp.utils.AlcoholCalculator

data class Lobby(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val people: MutableList<Person> = mutableListOf(),
    var currentDrink: Drink,
    var isTimerActive: Boolean = false,
    var remainingTimeSeconds: Int = 0
) {
    fun addPerson(person: Person) {
        people.add(person)
    }

    fun removePerson(personId: String) {
        people.removeIf { it.id == personId }
    }

    fun getSafestWaitTime(): Int {
        if (people.isEmpty()) return 60

        return people.maxOf { person ->
            AlcoholCalculator.calculateSafeWaitTimeMinutes(person, currentDrink)
        }
    }
}