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
            binding.textDrinkType.text = "${lobby.currentDrink.name} • ${lobby.safetyMode.emoji} ${lobby.safetyMode.displayName}"


            // Show timer status
            if (lobby.isTimerActive) {
                val hours = lobby.remainingTimeSeconds / 3600
                val minutes = (lobby.remainingTimeSeconds % 3600) / 60
                val seconds = lobby.remainingTimeSeconds % 60
                binding.textTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
                binding.progressBarTimer.visibility = android.view.View.VISIBLE

                // Set progress bar
                val totalTime = lobby.getSafestWaitTime() * 60 // Convert to seconds
                val progress = if (totalTime > 0) {
                    ((totalTime - lobby.remainingTimeSeconds) * 100) / totalTime
                } else 0
                binding.progressBarTimer.progress = progress
            } else {
                binding.textTimer.text = "00:00:00"
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
            binding.root.setOnClickListener { onLobbyClick(lobby) }
            binding.buttonDeleteLobby.setOnClickListener { onLobbyDelete(lobby) }
        }
    }
}

class LobbyDiffCallback : DiffUtil.ItemCallback<Lobby>() {
    override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
        val same = oldItem.name == newItem.name &&
                oldItem.people.size == newItem.people.size &&
                oldItem.currentDrink.name == newItem.currentDrink.name &&
                oldItem.isTimerActive == newItem.isTimerActive &&
                oldItem.remainingTimeSeconds == newItem.remainingTimeSeconds

        if (!same) {
            android.util.Log.d("LobbyAdapter", "Content changed for ${oldItem.name}: " +
                    "people ${oldItem.people.size}->${newItem.people.size}, " +
                    "drink ${oldItem.currentDrink.name}->${newItem.currentDrink.name}, " +
                    "timer ${oldItem.isTimerActive}->${newItem.isTimerActive}")
        }

        return same
    }
}