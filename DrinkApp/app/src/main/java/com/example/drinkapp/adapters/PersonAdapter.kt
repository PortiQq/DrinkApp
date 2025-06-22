package com.example.drinkapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.drinkapp.models.Person
import com.example.drinkapp.models.Gender
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.drinkapp.databinding.ItemPersonBinding
import com.example.drinkapp.models.displayName

class PersonAdapter(
    private val onPersonClick: (Person) -> Unit,
    private val onPersonDelete: (Person) -> Unit
) : ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PersonViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.textName.text = person.name
            binding.textDetails.text = "${person.gender.displayName()} • ${person.age}y • ${person.weightKg}kg"
            binding.textGender.text = if (person.gender == Gender.MALE) "♂" else "♀"

            binding.root.setOnClickListener { onPersonClick(person) }
            binding.buttonDelete.setOnClickListener { onPersonDelete(person) }
        }
    }
}

class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {
    override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem == newItem
    }
}