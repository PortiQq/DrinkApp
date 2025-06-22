package com.example.drinkapp.managers

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.TimerState

object LobbyManager {
    private val lobbies = mutableMapOf<String, Lobby>()
    private val timers = mutableMapOf<String, CountDownTimer>()
    private val timerCallbacks = mutableMapOf<String, (TimerState) -> Unit>()

    //LiveData for lobbies
    private val _lobbiesLiveData = MutableLiveData<List<Lobby>>()
    val lobbiesLiveData: LiveData<List<Lobby>> = _lobbiesLiveData

    init {
        _lobbiesLiveData.postValue(emptyList())
    }

    private fun notifyLobbiesChanged() {
        // copy of lobbies for LiveData detecting changes
        val lobbiesCopy = lobbies.values.map { lobby ->
            lobby.copy(
                people = lobby.people.toMutableList()
            )
        }
        _lobbiesLiveData.postValue(lobbiesCopy)
    }

    fun addLobby(lobby: Lobby) {
        lobbies[lobby.id] = lobby
        notifyLobbiesChanged()
    }

    fun removeLobby(lobbyId: String) {
        timers[lobbyId]?.cancel()
        timers.remove(lobbyId)
        timerCallbacks.remove(lobbyId)
        lobbies.remove(lobbyId)
        notifyLobbiesChanged()
    }

    fun getLobby(lobbyId: String): Lobby? {
        return lobbies[lobbyId]
    }

    fun addPersonToLobby(lobbyId: String, person: Person) {
        lobbies[lobbyId]?.let { lobby ->
            lobby.addPerson(person)
            notifyLobbiesChanged()
        }
    }

    fun removePersonFromLobby(lobbyId: String, personId: String) {
        lobbies[lobbyId]?.let { lobby ->
            lobby.removePerson(personId)
            notifyLobbiesChanged()
        }
    }

    fun updateLobbyDrink(lobbyId: String, drink: Drink) {
        lobbies[lobbyId]?.let { lobby ->
            lobby.currentDrink = drink
            notifyLobbiesChanged()
        }
    }

    fun startLobbyTimer(lobbyId: String) {
        val lobby = lobbies[lobbyId] ?: return

        timers[lobbyId]?.cancel()

        val durationSeconds = lobby.getSafestWaitTime() * 60 // min -> s

        lobby.isTimerActive = true
        lobby.remainingTimeSeconds = durationSeconds
        notifyLobbiesChanged()

        // Create a new timer
        timers[lobbyId] = object : CountDownTimer(durationSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                val progressPercentage = ((durationSeconds - secondsLeft) * 100) / durationSeconds

                lobby.remainingTimeSeconds = secondsLeft

                timerCallbacks[lobbyId]?.invoke(TimerState(secondsLeft, progressPercentage))

                // Notify changes every 5 seconds to update the list
                if (secondsLeft % 5 == 0) {
                    notifyLobbiesChanged()
                }
            }

            override fun onFinish() {
                lobby.isTimerActive = false
                lobby.remainingTimeSeconds = 0

                timerCallbacks[lobbyId]?.invoke(TimerState(0, 100))

                timers.remove(lobbyId)
                notifyLobbiesChanged()
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
        timers.values.forEach { it.cancel() }
        timers.clear()

        timerCallbacks.clear()

        lobbies.clear()
        notifyLobbiesChanged()
    }
}