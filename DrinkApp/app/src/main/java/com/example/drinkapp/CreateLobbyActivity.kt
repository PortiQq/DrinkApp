package com.example.drinkapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drinkapp.databinding.ActivityCreateLobbyBinding
import com.example.drinkapp.models.Drink

class CreateLobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrinkSpinner()
        setupClickListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Create New Lobby"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupDrinkSpinner() {
        val drinkNames = Drink.COMMON_DRINKS.map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, drinkNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInitialDrink.adapter = spinnerAdapter
    }

    private fun setupClickListeners() {
        binding.buttonCreateLobby.setOnClickListener {
            if (validateInputs()) {
                createLobby()
            }
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val lobbyName = binding.editTextLobbyName.text.toString().trim()

        if (lobbyName.isEmpty()) {
            binding.editTextLobbyName.error = "Lobby name is required"
            return false
        }

        if (lobbyName.length < 3) {
            binding.editTextLobbyName.error = "Lobby name must be at least 3 characters"
            return false
        }

        return true
    }

    private fun createLobby() {
        val lobbyName = binding.editTextLobbyName.text.toString().trim()
        val selectedDrinkIndex = binding.spinnerInitialDrink.selectedItemPosition

        // Create result intent with lobby data
        val resultIntent = Intent().apply {
            putExtra("lobby_name", lobbyName)
            putExtra("selected_drink", selectedDrinkIndex)
        }

        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "Lobby \"$lobbyName\" created!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}