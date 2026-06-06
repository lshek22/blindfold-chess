package com.example.blindfoldchess.ui.trainer.exercises

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentSameDiagonalBinding

class SameDiagonalFragment : Fragment() {

    private var _binding: FragmentSameDiagonalBinding? = null
    private val binding get() = _binding!!

    private var squareA = 0
    private var squareB = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSameDiagonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        squarePool.shuffle()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnYes.setOnClickListener { checkAnswer(true) }
        binding.btnNo.setOnClickListener  { checkAnswer(false) }

        nextQuestion()
    }

    private fun nextSquare(): Int {
        if (poolIndex >= squarePool.size) { squarePool.shuffle(); poolIndex = 0 }
        return squarePool[poolIndex++]
    }

    private fun squareName(square: Int): String {
        val file = square % 8
        val rank = square / 8
        return "${"abcdefgh"[file]}${rank + 1}"
    }

    private fun onSameDiagonal(a: Int, b: Int): Boolean {
        val fileDiff = Math.abs(a % 8 - b % 8)
        val rankDiff = Math.abs(a / 8 - b / 8)
        return fileDiff == rankDiff
    }

    private fun nextQuestion() {
        if (totalQuestions != -1 && questionsAsked >= totalQuestions) {
            showFinished()
            return
        }

        squareA = nextSquare()
        squareB = if (Math.random() < 0.5) {
            diagonalNeighbor(squareA) ?: nextSquare()
        } else {
            nextSquare()
        }

        binding.tvSquares.text = "${squareName(squareA)} — ${squareName(squareB)}"
        binding.tvResult.text = ""
        binding.btnYes.isEnabled = true
        binding.btnNo.isEnabled  = true
    }

    private fun diagonalNeighbor(square: Int): Int? {
        val file = square % 8
        val rank = square / 8
        val candidates = mutableListOf<Int>()
        for (d in 1..7) {
            for ((df, dr) in listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1)) {
                val nf = file + df * d
                val nr = rank + dr * d
                if (nf in 0..7 && nr in 0..7) candidates.add(nf + nr * 8)
            }
        }
        return if (candidates.isEmpty()) null else candidates.random()
    }

    private fun checkAnswer(guessYes: Boolean) {
        val correct = onSameDiagonal(squareA, squareB)
        val isRight = guessYes == correct

        questionsAsked++
        binding.btnYes.isEnabled = false
        binding.btnNo.isEnabled  = false

        if (isRight) {
            score++; streak++
            binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
            binding.tvResult.text = "Correct!"
        } else {
            streak = 0
            binding.tvResult.setTextColor(Color.parseColor("#F44336"))
            binding.tvResult.text = if (correct) "Wrong — they ARE on the same diagonal"
            else         "Wrong — they are NOT on the same diagonal"
        }

        binding.tvScore.text = if (totalQuestions == -1)
            "Q: $questionsAsked  |  Score: $score  |  Streak: $streak"
        else
            "Q: $questionsAsked/$totalQuestions  |  Score: $score  |  Streak: $streak"

        binding.root.postDelayed({ nextQuestion() }, 1000)
    }

    private fun showFinished() {
        binding.tvSquares.text = "Done!"
        binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
        binding.tvResult.text = "Final score: $score / $totalQuestions"
        binding.root.postDelayed({ findNavController().navigateUp() }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}