package com.example.drinkapp.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drinkapp.managers.LobbyManager
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Drink

class LobbyViewModel : ViewModel() {
    private val _lobby = MutableLiveData<Lobby>()
    val lobby: LiveData<Lobby> = _lobby

    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState> = _timerState

    private val _showWarning = MutableLiveData<Boolean>()
    val showWarning: LiveData<Boolean> = _showWarning

    private var lobbyId: String? = null

    //private var countDownTimer: CountDownTimer? = null

    init {
        _timerState.value = TimerState(0, 0)
    }

    fun initializeLobby(lobbyId: String) {
        this.lobbyId = lobbyId
        val lobby = LobbyManager.getLobby(lobbyId)
        _lobby.value = lobby

        // Set up timer state observer
        lobby?.let {
            LobbyManager.setTimerStateCallback(lobbyId) { timerState ->
                _timerState.postValue(timerState)
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

    fun saveLobbyState() {
        // This method can be used to persist lobby state when activity is destroyed
        // For now, the state is maintained in LobbyManager
    }

    override fun onCleared() {
        super.onCleared()
        // Remove timer callback when ViewModel is cleared
        lobbyId?.let { LobbyManager.removeTimerStateCallback(it) }
    }
}

    /*
    private fun startTimer(durationSeconds: Int) {
        countDownTimer?.cancel()

        _lobby.value?.let { currentLobby ->
            currentLobby.isTimerActive = true
            currentLobby.remainingTimeSeconds = durationSeconds
            _lobby.value = currentLobby.copy()
        }

        countDownTimer = object : CountDownTimer(durationSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                val progressPercentage = ((durationSeconds - secondsLeft) * 100) / durationSeconds

                _timerState.value = TimerState(secondsLeft, progressPercentage)

                _lobby.value?.let { currentLobby ->
                    currentLobby.remainingTimeSeconds = secondsLeft
                    _lobby.value = currentLobby.copy()
                }
            }

            override fun onFinish() {
                _timerState.value = TimerState(0, 100)
                _lobby.value?.let { currentLobby ->
                    currentLobby.isTimerActive = false
                    currentLobby.remainingTimeSeconds = 0
                    _lobby.value = currentLobby.copy()
                }

                // Show completion notification
                showTimerCompleteNotification()
            }
        }.start()
    }

    private fun showTimerCompleteNotification() {
        // Implement notification or callback to MainActivity
    }

    fun getRemainingTime(): Int = _lobby.value?.remainingTimeSeconds ?: 0
    fun getCurrentDrink(): Drink = _lobby.value?.currentDrink ?: Drink.COMMON_DRINKS[0]

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

data class TimerState(
    val remainingSeconds: Int,
    val progressPercentage: Int
)*/