package com.example.drinkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drinkapp.managers.LobbyManager
import com.example.drinkapp.models.Lobby

class LobbyListViewModel : ViewModel() {
    private val _lobbies = MutableLiveData<List<Lobby>>()
    val lobbies: LiveData<List<Lobby>> = _lobbies

    private val lobbyList = mutableListOf<Lobby>()

    init {
        refreshLobbies()
    }

    private fun refreshLobbies() {
        val currentLobbies = LobbyManager.getAllLobbies()
        _lobbies.value = currentLobbies
    }

    fun forceRefresh() {
        val currentLobbies = LobbyManager.getAllLobbies()
        val refreshedLobbies = currentLobbies.map { lobby ->
            LobbyManager.getLobby(lobby.id) ?: lobby
        }
        _lobbies.value = refreshedLobbies
    }

    fun addLobby(lobby: Lobby) {

        LobbyManager.addLobby(lobby)
        refreshLobbies()
    }

    fun removeLobby(lobbyId: String) {
        LobbyManager.removeLobby(lobbyId)
        refreshLobbies()
    }

    fun getLobby(lobbyId: String): Lobby? {
        return LobbyManager.getLobby(lobbyId)
    }

    // Update lobby in the list (for timer updates)
    fun updateLobby(lobby: Lobby) {
        refreshLobbies()
    }

    // Add method to manually refresh lobbies (useful when returning from LobbyActivity)
    fun onResume() {
        refreshLobbies()
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up all timers when ViewModel is destroyed
        LobbyManager.clearAllLobbies()
    }
}