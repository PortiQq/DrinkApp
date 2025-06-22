package com.example.drinkapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drinkapp.models.Drink
import com.example.drinkapp.models.Gender
import com.example.drinkapp.models.Lobby
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.TimerState
import com.example.drinkapp.utils.AlcoholCalculator
import com.example.drinkapp.viewmodel.LobbyViewModel
import com.example.drinkapp.adapters.PersonAdapter
import com.example.drinkapp.databinding.ActivityLobbyBinding
import com.example.drinkapp.models.SafetyMode


class LobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLobbyBinding
    private lateinit var viewModel: LobbyViewModel
    private lateinit var personAdapter: PersonAdapter
    private var lobbyId: String? = null

    // Activity result launcher for AddPersonActivity
    private val addPersonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                // Extract person data from intent
                val name = data.getStringExtra("person_name") ?: return@let
                val genderString = data.getStringExtra("person_gender") ?: return@let
                val age = data.getIntExtra("person_age", 0)
                val weight = data.getDoubleExtra("person_weight", 0.0)
                val height = data.getIntExtra("person_height", 0)
                val id = data.getStringExtra("person_id") ?: return@let

                // Create person
                val gender = try {
                    Gender.valueOf(genderString)
                } catch (e: IllegalArgumentException) {
                    Gender.MALE // Default
                }

                val person = Person(
                    id = id,
                    name = name,
                    gender = gender,
                    age = age,
                    weightKg = weight,
                    heightCm = height
                )

                // Add to ViewModel
                viewModel.addPerson(person)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get lobby ID from intent
        lobbyId = intent.getStringExtra("lobby_id")
        if (lobbyId == null) {
            finish()
            return
        }

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LobbyViewModel::class.java]
        lobbyId?.let { viewModel.initializeLobby(it) }
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
            val intent = Intent(this, AddPersonActivity::class.java)
            addPersonLauncher.launch(intent)
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
        // Safety mode selection
        binding.radioGroupSafetyMode.setOnCheckedChangeListener { _, checkedId ->
            val safetyMode = when (checkedId) {
                binding.radioSafe.id -> SafetyMode.SAFE
                binding.radioBalanced.id -> SafetyMode.BALANCED
                binding.radioParty.id -> SafetyMode.PARTY
                else -> SafetyMode.SAFE
            }
            viewModel.updateSafetyMode(safetyMode)
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

    private fun updateUI(lobby: Lobby?) {
        if (lobby == null) return

        supportActionBar?.apply {
            title = lobby.name
            setDisplayHomeAsUpEnabled(true)
        }

        personAdapter.submitList(lobby.people.toList())
        binding.textPeopleCount.text = "${lobby.people.size} osób"

        // Show or hide the empty state based on people count
        if (lobby.people.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.emptyState.visibility = View.GONE
        }

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
        binding.buttonDrinkUp.text = if (lobby.isTimerActive) "⚠️ Na Zdrowie ⚠️" else "🍻 Na Zdrowie!"

        // Update safety mode radio
        when (lobby.safetyMode) {
            SafetyMode.SAFE -> binding.radioSafe.isChecked = true
            SafetyMode.BALANCED -> binding.radioBalanced.isChecked = true
            SafetyMode.PARTY -> binding.radioParty.isChecked = true
        }
    }

    private fun updateTimer(timerState: TimerState) {
        binding.textTimer.text = formatTime(timerState.remainingSeconds)
        binding.progressBarGlass.progress = timerState.progressPercentage

        updateGlassAnimation(timerState.progressPercentage)
    }

    private fun updateGlassAnimation(progressPercentage: Int) {
        binding.wineGlassView.setFillPercentage(progressPercentage.toFloat())
    }

    private fun showSafetyWarning() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Ostrzeżenie")
            .setMessage("Nie upłynął jeszcze bezpieczny czas między kolejkami! Pozostało ${formatTime(viewModel.getRemainingTime())}!")
            .setPositiveButton("Zignoruj") { _, _ ->
                viewModel.overrideTimer()
            }
            .setNegativeButton("Zaczekaj", null)
            .show()
    }

    private fun showPersonDetails(person: Person) {
        val waitTime = AlcoholCalculator.calculateSafeWaitTimeMinutes(person, viewModel.getCurrentDrink())
        AlertDialog.Builder(this)
            .setTitle(person.name)
            .setMessage("Bezpieczny czas: ${waitTime} minut\nWzrost: ${person.heightCm}cm\nWaga: ${person.weightKg}kg\nPłeć: ${person.gender}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            viewModel.onResume()  // Re-establish timer connection
        }
    }
}