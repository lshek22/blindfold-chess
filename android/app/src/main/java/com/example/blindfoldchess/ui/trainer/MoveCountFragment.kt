package com.example.blindfoldchess.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.R
import com.example.blindfoldchess.databinding.FragmentMoveCountBinding

class MoveCountFragment : Fragment() {

    private var _binding: FragmentMoveCountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoveCountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionCount  = arguments?.getInt("questionCount", -1) ?: -1
        val destinationId  = arguments?.getInt("destinationId", 0)  ?: 0

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        listOf(
            binding.btn1 to 1,
            binding.btn2 to 2,
            binding.btn3 to 3,
            binding.btn4 to 4,
        ).forEach { (btn, moves) ->
            btn.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("questionCount", questionCount)
                    putInt("moveCount",     moves)
                }
                findNavController().navigate(destinationId, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}