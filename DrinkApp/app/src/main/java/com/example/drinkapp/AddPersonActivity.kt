package com.example.drinkapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.drinkapp.databinding.ActivityAddPersonBinding
import com.example.drinkapp.models.Gender
import com.example.drinkapp.models.Person
import com.example.drinkapp.viewmodel.DrinkAppViewModel

class AddPersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPersonBinding
    private lateinit var viewModel: DrinkAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get shared ViewModel
        viewModel = ViewModelProvider(this)[DrinkAppViewModel::class.java]

        setupClickListeners()
        setupToolbar()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Add Person"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddPerson.setOnClickListener {
            if (validateInputs()) {
                addPerson()
            }
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.editTextName.text.toString().trim()
        val ageText = binding.editTextAge.text.toString()
        val weightText = binding.editTextWeight.text.toString()
        val heightText = binding.editTextHeight.text.toString()

        when {
            name.isEmpty() -> {
                binding.editTextName.error = "Name is required"
                return false
            }
            ageText.isEmpty() || ageText.toIntOrNull() == null -> {
                binding.editTextAge.error = "Valid age is required"
                return false
            }
            ageText.toInt() < 18 -> {
                binding.editTextAge.error = "Must be 18 or older"
                return false
            }
            weightText.isEmpty() || weightText.toDoubleOrNull() == null -> {
                binding.editTextWeight.error = "Valid weight is required"
                return false
            }
            weightText.toDouble() < 30 || weightText.toDouble() > 300 -> {
                binding.editTextWeight.error = "Weight must be between 30-300 kg"
                return false
            }
            heightText.isEmpty() || heightText.toIntOrNull() == null -> {
                binding.editTextHeight.error = "Valid height is required"
                return false
            }
            heightText.toInt() < 100 || heightText.toInt() > 250 -> {
                binding.editTextHeight.error = "Height must be between 100-250 cm"
                return false
            }
        }

        return true
    }

    private fun addPerson() {
        val name = binding.editTextName.text.toString().trim()
        val age = binding.editTextAge.text.toString().toInt()
        val weight = binding.editTextWeight.text.toString().toDouble()
        val height = binding.editTextHeight.text.toString().toInt()
        val gender = if (binding.radioMale.isChecked) Gender.MALE else Gender.FEMALE

        val person = Person(
            name = name,
            gender = gender,
            age = age,
            weightKg = weight,
            heightCm = height
        )

        viewModel.addPerson(person)
        Toast.makeText(this, "$name added to party!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}