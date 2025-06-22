package com.example.drinkapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drinkapp.databinding.ActivityCreateLobbyBinding
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.SafetyMode

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
            title = "Stwórz nowy pokój"
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
            binding.editTextLobbyName.error = "Nazwa pokoju jest wymagana"
            return false
        }

        if (lobbyName.length < 3) {
            binding.editTextLobbyName.error = "Nazwa musi składać się co najmniej z 3 znaków"
            return false
        }

        return true
    }

    private fun createLobby() {
        val lobbyName = binding.editTextLobbyName.text.toString().trim()
        val selectedDrinkIndex = binding.spinnerInitialDrink.selectedItemPosition

        // Get selected safety mode
        val selectedSafetyMode = when (binding.radioGroupSafetyMode.checkedRadioButtonId) {
            binding.radioUltraSafe.id -> SafetyMode.SAFE
            binding.radioBalanced.id -> SafetyMode.BALANCED
            binding.radioParty.id -> SafetyMode.PARTY
            else -> SafetyMode.SAFE
        }

        // Create result intent with lobby data
        val resultIntent = Intent().apply {
            putExtra("lobby_name", lobbyName)
            putExtra("selected_drink", selectedDrinkIndex)
            putExtra("safety_mode", selectedSafetyMode.name)
        }

        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "Pokój \"$lobbyName\" utworzony!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}