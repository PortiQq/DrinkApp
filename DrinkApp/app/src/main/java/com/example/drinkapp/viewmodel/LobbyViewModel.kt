package com.example.drinkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drinkapp.managers.LobbyManager
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.SafetyMode
import com.example.drinkapp.models.TimerState

class LobbyViewModel : ViewModel() {
    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby

    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState> = _timerState

    private val _showWarning = MutableLiveData<Boolean>()
    val showWarning: LiveData<Boolean> = _showWarning

    private var lobbyId: String? = null

    init {
        _timerState.value = TimerState(0, 0)
    }

    fun initializeLobby(lobbyId: String) {

        if (this.lobbyId != null) {
            onResume()
            return
        }

        this.lobbyId = lobbyId
        val lobby = LobbyManager.getLobby(lobbyId)
        _lobby.value = lobby

        // Set up timer state observer
        lobby?.let {
            LobbyManager.setTimerStateCallback(lobbyId) { timerState ->
                _timerState.postValue(timerState)
            }
            // If lobby has active timer, sync the timer state
            if (it.isTimerActive && it.remainingTimeSeconds > 0) {
                val totalTime = it.getSafestWaitTime() * 60
                val progressPercentage = if (totalTime > 0) {
                    ((totalTime - it.remainingTimeSeconds) * 100) / totalTime
                } else 0
                _timerState.value = TimerState(it.remainingTimeSeconds, progressPercentage)
            }
        }
    }

    fun addPerson(person: Person) {
        lobbyId?.let { id ->
            LobbyManager.addPersonToLobby(id, person)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    fun removePerson(personId: String) {
        lobbyId?.let { id ->
            LobbyManager.removePersonFromLobby(id, personId)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    fun updateCurrentDrink(drink: Drink) {
        lobbyId?.let { id ->
            LobbyManager.updateLobbyDrink(id, drink)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    fun startDrinking() {
        lobbyId?.let { id ->
            val lobby = LobbyManager.getLobby(id)
            if (lobby?.isTimerActive == true && lobby.remainingTimeSeconds > 0) {
                _showWarning.value = true
                return
            }
            LobbyManager.startLobbyTimer(id)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    fun overrideTimer() {
        lobbyId?.let { id ->
            LobbyManager.startLobbyTimer(id)
            _lobby.value = LobbyManager.getLobby(id)
        }
        _showWarning.value = false
    }

    fun getRemainingTime(): Int {
        return lobbyId?.let { LobbyManager.getLobby(it)?.remainingTimeSeconds } ?: 0
    }

    fun getCurrentDrink(): Drink {
        return lobbyId?.let { LobbyManager.getLobby(it)?.currentDrink } ?: Drink.COMMON_DRINKS[0]
    }

    fun updateSafetyMode(safetyMode: SafetyMode) {
        lobbyId?.let { id ->
            LobbyManager.updateLobbySafetyMode(id, safetyMode)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    fun updateCustomDrink(drink: Drink) {
        lobbyId?.let { id ->
            LobbyManager.updateLobbyCustomDrink(id, drink)
            _lobby.value = LobbyManager.getLobby(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Remove timer callback when ViewModel is cleared
        lobbyId?.let { LobbyManager.removeTimerStateCallback(it) }
    }

    fun onResume() {
        lobbyId?.let { id ->
            val lobby = LobbyManager.getLobby(id)
            lobby?.let {
                // Re-register the callback
                LobbyManager.setTimerStateCallback(id) { timerState ->
                    _timerState.postValue(timerState)
                }

                // Sync current timer state if timer is active
                if (it.isTimerActive && it.remainingTimeSeconds > 0) {
                    val totalTime = it.getSafestWaitTime() * 60
                    val progressPercentage = if (totalTime > 0) {
                        ((totalTime - it.remainingTimeSeconds) * 100) / totalTime
                    } else 0
                    _timerState.postValue(TimerState(it.remainingTimeSeconds, progressPercentage))
                }
            }
        }
    }
}