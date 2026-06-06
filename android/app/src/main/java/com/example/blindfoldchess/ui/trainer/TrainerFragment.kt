package com.example.blindfoldchess.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.blindfoldchess.R
import com.example.blindfoldchess.databinding.FragmentTrainerBinding

class TrainerFragment : Fragment() {

    private var _binding: FragmentTrainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercises = listOf(
            Exercise("Coordinates",      "Tap the named square",                  R.id.action_trainer_to_question_count, R.id.coordinateFragment),
            Exercise("Square color",     "Is this square light or dark?",         R.id.action_trainer_to_question_count, R.id.squareColorFragment),
            Exercise("Sister square", "Find the mirror square", R.id.action_trainer_to_question_count, R.id.sisterSquareFragment),
            Exercise("Same color", "Are these two squares the same color?", R.id.action_trainer_to_question_count, R.id.sameColorFragment),
            Exercise("Same diagonal", "Are these on the same diagonal?", R.id.action_trainer_to_question_count, R.id.sameDiagonalFragment),
            Exercise("Knight moves", "Can a knight jump from A to B?", R.id.action_trainer_to_question_count, R.id.moveCountFragment),
            Exercise("Colliding pieces", "Which square do both pieces attack?",   0, 0),
            Exercise("Tactics",          "Find the best move",                    0, 0),
        )

        binding.exercisesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.exercisesRecycler.adapter = ExerciseAdapter(exercises) { exercise ->
            if (exercise.navActionId != 0) {
                val bundle = Bundle().apply {
                    putInt("destinationId", exercise.destinationId)
                }
                findNavController().navigate(exercise.navActionId, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}