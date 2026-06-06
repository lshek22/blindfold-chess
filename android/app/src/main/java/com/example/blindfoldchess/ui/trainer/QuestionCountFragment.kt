package com.example.blindfoldchess.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.R
import com.example.blindfoldchess.databinding.FragmentQuestionCountBinding

class QuestionCountFragment : Fragment() {

    private var _binding: FragmentQuestionCountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionCountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val destinationId = arguments?.getInt("destinationId", 0) ?: 0

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        listOf(
            binding.btn10       to 10,
            binding.btn25       to 25,
            binding.btn50       to 50,
            binding.btn100      to 100,
            binding.btnInfinite to -1,
        ).forEach { (btn, count) ->
            btn.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("questionCount", count)
                    putInt("destinationId", destinationId)
                }
                if (destinationId == R.id.moveCountFragment) {
                    bundle.putInt("destinationId", R.id.knightMovesFragment)
                    findNavController().navigate(R.id.action_question_count_to_move_count, bundle)
                } else {
                    findNavController().navigate(destinationId, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}