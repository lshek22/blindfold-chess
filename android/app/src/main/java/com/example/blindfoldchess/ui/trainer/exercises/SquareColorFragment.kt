package com.example.blindfoldchess.ui.trainer.exercises

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentSquareColorBinding

class SquareColorFragment : Fragment() {

    private var _binding: FragmentSquareColorBinding? = null
    private val binding get() = _binding!!

    private var targetSquare = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSquareColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        squarePool.shuffle()

        binding.btnBack.setOnClickListener  { findNavController().navigateUp() }
        binding.btnBlack.setOnClickListener { checkAnswer(isWhiteGuess = false) }
        binding.btnWhite.setOnClickListener { checkAnswer(isWhiteGuess = true) }

        nextQuestion()
    }

    private fun nextSquare(): Int {
        if (poolIndex >= squarePool.size) {
            squarePool.shuffle()
            poolIndex = 0
        }
        return squarePool[poolIndex++]
    }

    private fun nextQuestion() {
        if (totalQuestions != -1 && questionsAsked >= totalQuestions) {
            showFinished()
            return
        }
        targetSquare = nextSquare()
        val file = targetSquare % 8
        val rank = targetSquare / 8
        binding.tvSquareName.text = "${"abcdefgh"[file]}${rank + 1}"
        binding.tvResult.text = ""
    }

    private fun checkAnswer(isWhiteGuess: Boolean) {
        val file = targetSquare % 8
        val rank = targetSquare / 8
        val isActuallyLight = (file + rank) % 2 != 0
        val correct = (isWhiteGuess && isActuallyLight) || (!isWhiteGuess && !isActuallyLight)

        questionsAsked++

        if (correct) {
            score++
            streak++
            binding.tvResult.setTextColor(Color.parseColor("#4CAF50"))
            binding.tvResult.text = "Correct!"
        } else {
            streak = 0
            binding.tvResult.setTextColor(Color.parseColor("#F44336"))
            binding.tvResult.text = "Wrong — it was ${if (isActuallyLight) "White" else "Black"}"
        }

        binding.tvScore.text = if (totalQuestions == -1) {
            "Q: $questionsAsked  |  Score: $score  |  Streak: $streak"
        } else {
            "Q: $questionsAsked/$totalQuestions  |  Score: $score  |  Streak: $streak"
        }

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