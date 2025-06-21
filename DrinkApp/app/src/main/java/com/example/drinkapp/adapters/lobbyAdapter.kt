package com.example.drinkapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.drinkapp.databinding.ItemLobbyBinding
import com.example.drinkapp.models.Lobby

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
            binding.textLobbyName.text = lobby.name
            binding.textPeopleCount.text = "${lobby.people.size} people"
            binding.textCurrentDrink.text = "Current drink: ${lobby.currentDrink.name}"

            // Show timer status
            if (lobby.isTimerActive) {
                val minutes = lobby.remainingTimeSeconds / 60
                val seconds = lobby.remainingTimeSeconds % 60
                binding.textTimerStatus.text = "⏱️ Timer: ${String.format("%02d:%02d", minutes, seconds)}"
                binding.textTimerStatus.visibility = android.view.View.VISIBLE
            } else {
                binding.textTimerStatus.visibility = android.view.View.GONE
            }

            // Set click listeners
            binding.root.setOnClickListener { onLobbyClick(lobby) }
            binding.buttonDeleteLobby.setOnClickListener { onLobbyDelete(lobby) }

            // Set background based on timer status
            if (lobby.isTimerActive) {
                binding.cardLobby.setCardBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.holo_orange_light
                    )
                )
            } else {
                binding.cardLobby.setCardBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.white
                    )
                )
            }
        }
    }
}

class LobbyDiffCallback : DiffUtil.ItemCallback<Lobby>() {
    override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem == newItem
    }
}