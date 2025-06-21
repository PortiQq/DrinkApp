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
        _lobbies.value = emptyList()
    }

    fun addLobby(lobby: Lobby) {
        lobbyList.add(lobby)
        _lobbies.value = lobbyList.toList()

        // Store reference for LobbyManager
        LobbyManager.addLobby(lobby)
    }

    fun removeLobby(lobbyId: String) {
        // Stop any active timer for this lobby
        LobbyManager.removeLobby(lobbyId)

        lobbyList.removeIf { it.id == lobbyId }
        _lobbies.value = lobbyList.toList()
    }

    fun getLobby(lobbyId: String): Lobby? {
        return LobbyManager.getLobby(lobbyId)
    }

    // Update lobby in the list (for timer updates)
    fun updateLobby(lobby: Lobby) {
        val index = lobbyList.indexOfFirst { it.id == lobby.id }
        if (index != -1) {
            lobbyList[index] = lobby
            _lobbies.value = lobbyList.toList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up all timers when ViewModel is destroyed
        LobbyManager.clearAllLobbies()
    }
}