package com.example.blindfoldchess.ui.trainer.exercises

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentSisterSquareBinding

class SisterSquareFragment : Fragment() {

    private var _binding: FragmentSisterSquareBinding? = null
    private val binding get() = _binding!!

    private var targetSquare = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSisterSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        squarePool.shuffle()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnSubmit.setOnClickListener { submitAnswer() }
        binding.etAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { submitAnswer(); true } else false
        }

        nextQuestion()
    }

    private fun sisterOf(square: Int): Int {
        val file = square % 8
        val rank = square / 8
        return (7 - file) + (7 - rank) * 8
    }

    private fun squareName(square: Int): String {
        val file = square % 8
        val rank = square / 8
        return "${"abcdefgh"[file]}${rank + 1}"
    }

    private fun parseSquare(input: String): Int? {
        val s = input.trim().lowercase()
        if (s.length != 2) return null
        val file = "abcdefgh".indexOf(s[0])
        val rank = s[1].digitToIntOrNull()?.minus(1) ?: return null
        if (file < 0 || rank !in 0..7) return null
        return file + rank * 8
    }

    private fun nextSquare(): Int {
        if (poolIndex >= squarePool.size) { squarePool.shuffle(); poolIndex = 0 }
        return squarePool[poolIndex++]
    }

    private fun nextQuestion() {
        if (totalQuestions != -1 && questionsAsked >= totalQuestions) {
            showFinished()
            return
        }
        targetSquare = nextSquare()
        binding.tvSquareName.text = squareName(targetSquare)
        binding.tvResult.text = ""
        binding.etAnswer.text?.clear()
        binding.etAnswer.isEnabled = true
        binding.btnSubmit.isEnabled = true
    }

    private fun submitAnswer() {
        val raw = binding.etAnswer.text?.toString() ?: return
        val guessedSquare = parseSquare(raw)
        val correctSquare = sisterOf(targetSquare)

        questionsAsked++
        binding.etAnswer.isEnabled = false
        binding.btnSubmit.isEnabled = false

        if (guessedSquare == correctSquare) {
            score++; streak++
            binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
            binding.tvResult.text = "Correct!"
        } else {
            streak = 0
            binding.tvResult.setTextColor(Color.parseColor("#F44336"))
            val correctName = squareName(correctSquare)
            binding.tvResult.text = if (guessedSquare == null) "Invalid — answer was $correctName"
            else "Wrong — answer was $correctName"
        }

        binding.tvScore.text = if (totalQuestions == -1)
            "Q: $questionsAsked  |  Score: $score  |  Streak: $streak"
        else
            "Q: $questionsAsked/$totalQuestions  |  Score: $score  |  Streak: $streak"

        binding.root.postDelayed({ nextQuestion() }, 1000)
    }

    private fun showFinished() {
        binding.tvSquareName.text = "Done!"
        binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
        binding.tvResult.text = "Final score: $score / $totalQuestions"
        binding.root.postDelayed({ findNavController().navigateUp() }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}