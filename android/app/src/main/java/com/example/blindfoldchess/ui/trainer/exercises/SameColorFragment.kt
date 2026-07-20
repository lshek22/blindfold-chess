package com.example.blindfoldchess.ui.trainer.exercises

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentSameColorBinding

class SameColorFragment : Fragment() {

    private var _binding: FragmentSameColorBinding? = null
    private val binding get() = _binding!!

    private var squareA = 0
    private var squareB = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSameColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        squarePool.shuffle()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnYes.setOnClickListener  { checkAnswer(guessedSameColor = true) }
        binding.btnNo.setOnClickListener   { checkAnswer(guessedSameColor = false) }

        nextQuestion()
    }

    private fun nextSquare(): Int {
        if (poolIndex >= squarePool.size) {
            squarePool.shuffle()
            poolIndex = 0
        }
        return squarePool[poolIndex++]
    }

    private fun squareName(square: Int): String {
        val file = square % 8
        val rank = square / 8
        return "${"abcdefgh"[file]}${rank + 1}"
    }

    private fun isLight(square: Int): Boolean {
        val file = square % 8
        val rank = square / 8
        return (file + rank) % 2 != 0
    }

    private fun nextQuestion() {
        if (totalQuestions != -1 && questionsAsked >= totalQuestions) {
            showFinished()
            return
        }

        squareA = nextSquare()
        do { squareB = nextSquare() } while (squareB == squareA)

        binding.tvSquareA.text = squareName(squareA)
        binding.tvSquareB.text = squareName(squareB)
        binding.tvResult.text = ""

        binding.btnYes.isEnabled = true
        binding.btnNo.isEnabled  = true
    }

    private fun checkAnswer(guessedSameColor: Boolean) {
        binding.btnYes.isEnabled = false
        binding.btnNo.isEnabled  = false

        val actualSameColor = isLight(squareA) == isLight(squareB)
        val correct = guessedSameColor == actualSameColor

        questionsAsked++

        if (correct) {
            score++
            streak++
            binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
            binding.tvResult.text = "Correct!"
        } else {
            streak = 0
            binding.tvResult.setTextColor(Color.parseColor("#F44336"))
            val answer = if (actualSameColor) "Yes, same color" else "No, different color"
            binding.tvResult.text = "Wrong — $answer"
        }

        binding.tvScore.text = if (totalQuestions == -1) {
            "Q: $questionsAsked  |  Score: $score  |  Streak: $streak"
        } else {
            "Q: $questionsAsked/$totalQuestions  |  Score: $score  |  Streak: $streak"
        }

        binding.root.postDelayed({ nextQuestion() }, 1000)
    }

    private fun showFinished() {
        binding.tvSquareA.text = "Done!"
        binding.tvSquareB.text = ""
        binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
        binding.tvResult.text = "Final score: $score / $totalQuestions"
        binding.btnYes.isEnabled = false
        binding.btnNo.isEnabled  = false
        binding.root.postDelayed({ findNavController().navigateUp() }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}