package com.example.blindfoldchess.ui.trainer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val items: List<Exercise>,
    private val onClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = items[position]
        holder.binding.exerciseTitle.text = exercise.title
        holder.binding.exerciseDescription.text = exercise.description
        holder.itemView.setOnClickListener { onClick(exercise) }
    }

    override fun getItemCount() = items.size
}