package com.example.blindfoldchess.ui.trainer.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentCoordinateBinding

class CoordinateFragment : Fragment() {

    private var _binding: FragmentCoordinateBinding? = null
    private val binding get() = _binding!!

    private var targetSquare = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCoordinateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        squarePool.shuffle()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.chessBoard.onSquareTapped = { square ->
            checkAnswer(square)
        }

        updateProgressText()
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
        binding.tvSquareName.text = binding.chessBoard.squareName(targetSquare)
        binding.tvResult.text = ""
        binding.chessBoard.highlightedSquares = emptySet()
    }

    private fun checkAnswer(tappedSquare: Int) {
        questionsAsked++

        if (tappedSquare == targetSquare) {
            score++
            streak++
            binding.tvResult.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            binding.tvResult.text = "Correct!"
            binding.chessBoard.highlightedSquares = setOf(tappedSquare)
        } else {
            streak = 0
            binding.tvResult.setTextColor(android.graphics.Color.parseColor("#F44336"))
            binding.tvResult.text = "Wrong — it was ${binding.chessBoard.squareName(targetSquare)}"
            binding.chessBoard.highlightedSquares = setOf(targetSquare)
        }

        updateProgressText()
        binding.root.postDelayed({ nextQuestion() }, 1000)
    }

    private fun updateProgressText() {
        binding.tvScore.text = if (totalQuestions == -1) {
            "Q: $questionsAsked  |  Score: $score  |  Streak: $streak"
        } else {
            "Q: $questionsAsked/$totalQuestions  |  Score: $score  |  Streak: $streak"
        }
    }

    private fun showFinished() {
        binding.tvSquareName.text = "Done!"
        binding.tvResult.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        binding.tvResult.text = "Final score: $score / $totalQuestions"
        binding.chessBoard.highlightedSquares = emptySet()
        binding.root.postDelayed({ findNavController().navigateUp() }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}