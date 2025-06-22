package com.example.drinkapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drinkapp.adapters.LobbyAdapter
import com.example.drinkapp.databinding.ActivityLobbyListBinding
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Drink
import com.example.drinkapp.viewmodel.LobbyListViewModel

class LobbyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLobbyListBinding
    private lateinit var viewModel: LobbyListViewModel
    private lateinit var lobbyAdapter: LobbyAdapter

    private val createLobbyLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val lobbyName = data.getStringExtra("lobby_name") ?: return@let
                val selectedDrinkIndex = data.getIntExtra("selected_drink", 0)
                val selectedDrink = Drink.COMMON_DRINKS[selectedDrinkIndex]

                val newLobby = Lobby(
                    name = lobbyName,
                    currentDrink = selectedDrink
                )

                viewModel.addLobby(newLobby)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        setupToolbar()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LobbyListViewModel::class.java]
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "DrinkApp - Lobbies"
        }
    }

    private fun setupRecyclerView() {
        lobbyAdapter = LobbyAdapter(
            onLobbyClick = { lobby -> openLobby(lobby) },
            onLobbyDelete = { lobby -> confirmDeleteLobby(lobby) }
        )
        binding.recyclerViewLobbies.adapter = lobbyAdapter
        binding.recyclerViewLobbies.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.fabCreateLobby.setOnClickListener {
            val intent = Intent(this, CreateLobbyActivity::class.java)
            createLobbyLauncher.launch(intent)
        }
        binding.buttonCreateFirstLobby.setOnClickListener {
            val intent = Intent(this, CreateLobbyActivity::class.java)
            createLobbyLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        // Observe lobbies LiveData - will automatically update when any lobby changes
        viewModel.lobbies.observe(this) { lobbies ->
            android.util.Log.d("LobbyListActivity", "Received ${lobbies.size} lobbies update")
            updateUI(lobbies)
        }
    }

    private fun updateUI(lobbies: List<Lobby>) {
        lobbyAdapter.submitList(lobbies.toList()) // Create a new list to trigger DiffUtil

        binding.textLobbyCount.text = when (lobbies.size) {
            0 -> "No active lobbies"
            1 -> "1 active lobby"
            else -> "${lobbies.size} active lobbies"
        }

        binding.textEmptyState.visibility = if (lobbies.isEmpty()) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }

    private fun openLobby(lobby: Lobby) {
        val intent = Intent(this, LobbyActivity::class.java).apply {
            putExtra("lobby_id", lobby.id)
        }
        startActivity(intent)
    }

    private fun confirmDeleteLobby(lobby: Lobby) {
        AlertDialog.Builder(this)
            .setTitle("Delete Lobby")
            .setMessage("Are you sure you want to delete \"${lobby.name}\"? This will remove all people and stop any active timers.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.removeLobby(lobby.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}