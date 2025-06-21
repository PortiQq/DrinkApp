package com.example.drinkapp.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Drink

class DrinkAppViewModel : ViewModel() {
    private val _lobby = MutableLiveData<Lobby>()
    val lobby: LiveData<Lobby> = _lobby

    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState> = _timerState

    private val _showWarning = MutableLiveData<Boolean>()
    val showWarning: LiveData<Boolean> = _showWarning

    private var countDownTimer: CountDownTimer? = null

    init {
        _lobby.value = Lobby(
            "Main Party", currentDrink = Drink.COMMON_DRINKS[0],
            name = ""
        )
        _timerState.value = TimerState(0, 0)
    }

    fun addPerson(person: Person) {
        _lobby.value?.let { currentLobby ->
            currentLobby.addPerson(person)
            _lobby.value = currentLobby.copy()
        }
    }

    fun removePerson(personId: String) {
        _lobby.value?.let { currentLobby ->
            currentLobby.removePerson(personId)
            _lobby.value = currentLobby.copy()
        }
    }

    fun updateCurrentDrink(drink: Drink) {
        _lobby.value?.let { currentLobby ->
            _lobby.value = currentLobby.copy(currentDrink = drink)
        }
    }

    fun startDrinking() {
        _lobby.value?.let { currentLobby ->
            if (currentLobby.isTimerActive && currentLobby.remainingTimeSeconds > 0) {
                _showWarning.value = true
                return
            }

            startTimer(currentLobby.getSafestWaitTime() * 60) // Convert minutes to seconds
        }
    }

    fun overrideTimer() {
        _lobby.value?.let { currentLobby ->
            startTimer(currentLobby.getSafestWaitTime() * 60)
        }
        _showWarning.value = false
    }

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
)