package com.example.drinkapp.managers

import android.os.CountDownTimer
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Drink

object LobbyManager {
    private val lobbies = mutableMapOf<String, Lobby>()
    private val timers = mutableMapOf<String, CountDownTimer>()
    private val timerCallbacks = mutableMapOf<String, (TimerState) -> Unit>()

    fun addLobby(lobby: Lobby) {
        lobbies[lobby.id] = lobby
    }

    fun removeLobby(lobbyId: String) {
        // Cancel and remove timer
        timers[lobbyId]?.cancel()
        timers.remove(lobbyId)

        // Remove callback
        timerCallbacks.remove(lobbyId)

        // Remove lobby
        lobbies.remove(lobbyId)
    }

    fun getLobby(lobbyId: String): Lobby? {
        return lobbies[lobbyId]
    }

    fun addPersonToLobby(lobbyId: String, person: Person) {
        lobbies[lobbyId]?.addPerson(person)
    }

    fun removePersonFromLobby(lobbyId: String, personId: String) {
        lobbies[lobbyId]?.removePerson(personId)
    }

    fun updateLobbyDrink(lobbyId: String, drink: Drink) {
        lobbies[lobbyId]?.let { lobby ->
            lobbies[lobbyId] = lobby.copy(currentDrink = drink)
        }
    }

    fun startLobbyTimer(lobbyId: String) {
        val lobby = lobbies[lobbyId] ?: return

        // Cancel existing timer
        timers[lobbyId]?.cancel()

        val durationSeconds = lobby.getSafestWaitTime() * 60 // Convert minutes to seconds

        // Update lobby state
        lobby.isTimerActive = true
        lobby.remainingTimeSeconds = durationSeconds

        // Create new timer
        timers[lobbyId] = object : CountDownTimer(durationSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                val progressPercentage = ((durationSeconds - secondsLeft) * 100) / durationSeconds

                // Update lobby state
                lobby.remainingTimeSeconds = secondsLeft

                // Notify callback
                timerCallbacks[lobbyId]?.invoke(TimerState(secondsLeft, progressPercentage))
            }

            override fun onFinish() {
                // Update lobby state
                lobby.isTimerActive = false
                lobby.remainingTimeSeconds = 0

                // Notify callback
                timerCallbacks[lobbyId]?.invoke(TimerState(0, 100))

                // Remove timer
                timers.remove(lobbyId)
            }
        }.start()
    }

    fun setTimerStateCallback(lobbyId: String, callback: (TimerState) -> Unit) {
        timerCallbacks[lobbyId] = callback
    }

    fun removeTimerStateCallback(lobbyId: String) {
        timerCallbacks.remove(lobbyId)
    }

    fun getAllLobbies(): List<Lobby> {
        return lobbies.values.toList()
    }

    fun clearAllLobbies() {
        // Cancel all timers
        timers.values.forEach { it.cancel() }
        timers.clear()

        // Clear callbacks
        timerCallbacks.clear()

        // Clear lobbies
        lobbies.clear()
    }
}