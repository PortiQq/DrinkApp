package com.example.drinkapp.models

data class TimerState(
    val remainingSeconds: Int,
    val progressPercentage: Int
) {
    /**
     *  HH:MM:SS string
     */
    fun getFormattedTime(): String {
        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     *  true if timer = 0
     */
    fun isFinished(): Boolean = remainingSeconds <= 0

    /**
     * true if timer still  active
     */
    fun isActive(): Boolean = remainingSeconds > 0

    /**
     * Returns the progress as a float between 0.0 and 1.0 for progress bars
     */
    fun getProgressFloat(): Float = progressPercentage / 100.0f

    /**
     *  status message based on timer state
     */
    fun getStatusMessage(): String {
        return when {
            isFinished() -> "Ready for next drink! 🍻"
            remainingSeconds < 300 -> "Almost ready... ⏰" // Less than 5 minutes
            remainingSeconds < 900 -> "Getting close! ⏳" // Less than 15 minutes
            else -> "Please wait... ⏰"
        }
    }

    /**
     * short format
     */
    fun getShortFormattedTime(): String {
        val totalMinutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format("%02d:%02d", totalMinutes, seconds)
    }

    companion object {
        /**
         * finished timer
         */
        fun finished(): TimerState = TimerState(0, 100)

        /**
         * inactive timer
         */
        fun inactive(): TimerState = TimerState(0, 0)
    }
}