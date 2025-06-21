package com.example.drinkapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Person
import com.example.drinkapp.utils.AlcoholCalculator
import com.example.drinkapp.viewmodel.DrinkAppViewModel
import com.example.drinkapp.adapters.PersonAdapter
import com.example.drinkapp.databinding.ActivityMainBinding
import com.example.drinkapp.viewmodel.TimerState

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DrinkAppViewModel
    private lateinit var personAdapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DrinkAppViewModel::class.java]
    }

    private fun setupRecyclerView() {
        personAdapter = PersonAdapter(
            onPersonClick = { person -> showPersonDetails(person) },
            onPersonDelete = { person -> viewModel.removePerson(person.id) }
        )
        binding.recyclerViewPeople.adapter = personAdapter
        binding.recyclerViewPeople.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.fabAddPerson.setOnClickListener {
            startActivity(Intent(this, AddPersonActivity::class.java))
        }

        binding.buttonDrinkUp.setOnClickListener {
            viewModel.startDrinking()
        }

        binding.spinnerDrink.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDrink = Drink.COMMON_DRINKS[position]
                viewModel.updateCurrentDrink(selectedDrink)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        viewModel.lobby.observe(this) { lobby ->
            updateUI(lobby)
        }

        viewModel.timerState.observe(this) { timerState ->
            updateTimer(timerState)
        }

        viewModel.showWarning.observe(this) { shouldShow ->
            if (shouldShow) showSafetyWarning()
        }
    }

    private fun updateUI(lobby: Lobby) {
        personAdapter.submitList(lobby.people.toList())
        binding.textPeopleCount.text = "${lobby.people.size} people"

        // Update drink spinner
        val drinkNames = Drink.COMMON_DRINKS.map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, drinkNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDrink.adapter = spinnerAdapter

        val selectedIndex = Drink.COMMON_DRINKS.indexOf(viewModel.getCurrentDrink())
        if (selectedIndex >= 0) {
            binding.spinnerDrink.setSelection(selectedIndex)
        }

        // Update button state
        binding.buttonDrinkUp.isEnabled = lobby.people.isNotEmpty()
        binding.buttonDrinkUp.text = if (lobby.isTimerActive) "⚠️ Override Timer" else "🍻 Drink Up!"
    }

    private fun updateTimer(timerState: TimerState) {
        binding.textTimer.text = formatTime(timerState.remainingSeconds)
        binding.progressBarGlass.progress = timerState.progressPercentage

        // Update glass animation
        updateGlassAnimation(timerState.progressPercentage)
    }

    private fun updateGlassAnimation(progressPercentage: Int) {
        // Animate the custom wine glass view
        binding.wineGlassView.setFillPercentage(progressPercentage.toFloat())
    }

    private fun showSafetyWarning() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Safety Warning")
            .setMessage("It's not safe to drink yet! Timer still has ${formatTime(viewModel.getRemainingTime())} left.")
            .setPositiveButton("Override") { _, _ ->
                viewModel.overrideTimer()
            }
            .setNegativeButton("Wait", null)
            .show()
    }

    private fun showPersonDetails(person: Person) {
        val waitTime = AlcoholCalculator.calculateSafeWaitTimeMinutes(person, viewModel.getCurrentDrink())
        AlertDialog.Builder(this)
            .setTitle(person.name)
            .setMessage("Safe wait time: ${waitTime} minutes\nWeight: ${person.weightKg}kg\nGender: ${person.gender}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}