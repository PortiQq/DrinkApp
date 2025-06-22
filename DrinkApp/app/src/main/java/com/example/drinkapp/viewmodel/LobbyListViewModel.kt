package com.example.drinkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drinkapp.managers.LobbyManager
import com.example.drinkapp.models.Lobby

class LobbyListViewModel : ViewModel() {

    val lobbies: LiveData<List<Lobby>> = LobbyManager.lobbiesLiveData

    fun addLobby(lobby: Lobby) {
        LobbyManager.addLobby(lobby)
    }

    fun removeLobby(lobbyId: String) {
        LobbyManager.removeLobby(lobbyId)
    }

    fun getLobby(lobbyId: String): Lobby? {
        return LobbyManager.getLobby(lobbyId)
    }

    override fun onCleared() {
        super.onCleared()
    }
}