package com.example.drinkapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.drinkapp.databinding.ItemLobbyBinding
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.managers.LobbyManager

class LobbyAdapter(
    private val onLobbyClick: (Lobby) -> Unit,
    private val onLobbyDelete: (Lobby) -> Unit
) : ListAdapter<Lobby, LobbyAdapter.LobbyViewHolder>(LobbyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val binding = ItemLobbyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LobbyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LobbyViewHolder(private val binding: ItemLobbyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lobby: Lobby) {

            val freshLobby = LobbyManager.getLobby(lobby.id) ?: lobby

            binding.textLobbyName.text = freshLobby.name
            binding.textPeopleCount.text = "${freshLobby.people.size} people"
            binding.textDrinkType.text = freshLobby.currentDrink.name

            // Show timer status
            if (freshLobby.isTimerActive) {
                val minutes = freshLobby.remainingTimeSeconds / 60
                val seconds = freshLobby.remainingTimeSeconds % 60
                binding.textTimer.text = String.format("%02d:%02d", minutes, seconds)
                binding.textTimerStatus.text = "Next drink in"
                binding.textStatus.text = "Active"
                binding.textStatus.setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        com.example.drinkapp.R.color.primary
                    )
                )
                binding.viewStatusIndicator.setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        com.example.drinkapp.R.color.primary
                    )
                )
                /*
                // Set progress bar
                val totalTime = lobby.currentDrink.durationSeconds
                val progress = if (totalTime > 0) {
                    ((totalTime - lobby.remainingTimeSeconds) * 100) / totalTime
                } else 0
                binding.progressBarTimer.progress = progress
                binding.progressBarTimer.visibility = android.view.View.VISIBLE
                */
            } else {
                binding.textTimer.text = "00:00"
                binding.textTimerStatus.text = "Ready to drink"
                binding.textStatus.text = "Inactive"
                binding.textStatus.setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.darker_gray
                    )
                )
                binding.viewStatusIndicator.setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.darker_gray
                    )
                )
                binding.progressBarTimer.visibility = android.view.View.GONE
            }

            // Set click listeners
            binding.root.setOnClickListener { onLobbyClick(freshLobby) }
            binding.buttonDeleteLobby.setOnClickListener { onLobbyDelete(freshLobby) }
        }
    }
}

class LobbyDiffCallback : DiffUtil.ItemCallback<Lobby>() {
    override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        // Pobierz aktualne dane z LobbyManager dla porównania
        val freshOld = LobbyManager.getLobby(oldItem.id) ?: oldItem
        val freshNew = LobbyManager.getLobby(newItem.id) ?: newItem

        return freshOld.name == freshNew.name &&
                freshOld.people.size == freshNew.people.size &&
                freshOld.currentDrink == freshNew.currentDrink &&
                freshOld.isTimerActive == freshNew.isTimerActive &&
                freshOld.remainingTimeSeconds == freshNew.remainingTimeSeconds
    }
}
