package com.example.drinkapp.models

/**
 * Data class representing the current state of a drinking timer
 * Used to communicate timer updates between LobbyManager and ViewModels
 */
data class TimerState(
    val remainingSeconds: Int,
    val progressPercentage: Int
) {
    /**
     * Formats the remaining time as MM:SS string
     */
    fun getFormattedTime(): String {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Returns true if the timer has finished (no time remaining)
     */
    fun isFinished(): Boolean = remainingSeconds <= 0

    /**
     * Returns true if the timer is currently active (has time remaining)
     */
    fun isActive(): Boolean = remainingSeconds > 0

    /**
     * Returns the progress as a float between 0.0 and 1.0 for progress bars
     */
    fun getProgressFloat(): Float = progressPercentage / 100.0f

    /**
     * Returns a user-friendly status message based on the timer state
     */
    fun getStatusMessage(): String {
        return when {
            isFinished() -> "Ready for next drink! 🍻"
            remainingSeconds < 300 -> "Almost ready... ⏰" // Less than 5 minutes
            remainingSeconds < 900 -> "Getting close! ⏳" // Less than 15 minutes
            else -> "Please wait... ⏰"
        }
    }

    companion object {
        /**
         * Creates a TimerState representing a finished/inactive timer
         */
        fun finished(): TimerState = TimerState(0, 100)

        /**
         * Creates a TimerState representing an inactive timer (not started)
         */
        fun inactive(): TimerState = TimerState(0, 0)
    }
}