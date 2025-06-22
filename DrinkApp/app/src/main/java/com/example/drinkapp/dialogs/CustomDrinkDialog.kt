package com.example.drinkapp.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.drinkapp.databinding.DialogCustomDrinkBinding
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.DrinkCategory

class CustomDrinkDialog(
    private val onDrinkCreated: (Drink) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogCustomDrinkBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCustomDrinkBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("🍹 Własny Drink")
            .setView(binding.root)
            .setPositiveButton("Dodaj") { _, _ ->
                if (validateInputs()) {
                    createCustomDrink()
                }
            }
            .setNegativeButton("Anuluj", null)
            .create()
    }

    private fun validateInputs(): Boolean {
        val volumeText = binding.editTextVolume.text.toString()
        val percentageText = binding.editTextAlcoholPercentage.text.toString()

        when {
            volumeText.isEmpty() || volumeText.toIntOrNull() == null -> {
                Toast.makeText(context, "Wprowadź objętość", Toast.LENGTH_SHORT).show()
                return false
            }
            volumeText.toInt() < 10 || volumeText.toInt() > 2000 -> {
                Toast.makeText(context, "Objętość musi być między 10-2000 ml", Toast.LENGTH_SHORT).show()
                return false
            }
            percentageText.isEmpty() || percentageText.toDoubleOrNull() == null -> {
                Toast.makeText(context, "Wprowadź zawartość alkoholu", Toast.LENGTH_SHORT).show()
                return false
            }
            percentageText.toDouble() < 0 || percentageText.toDouble() > 100 -> {
                Toast.makeText(context, "Zawartość alkoholu musi być między 0-100%", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun createCustomDrink() {
        val volume = binding.editTextVolume.text.toString().toInt()
        val percentage = binding.editTextAlcoholPercentage.text.toString().toDouble()

        val customDrink = Drink(
            name = "Własny ${volume}ml (${percentage}%)",
            alcoholPercentage = percentage,
            volumeMl = volume,
            category = DrinkCategory.OTHER
        )

        onDrinkCreated(customDrink)
    }
}