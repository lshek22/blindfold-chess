package com.example.blindfoldchess.ui.trainer.exercises

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.databinding.FragmentKnightMovesBinding

class KnightMovesFragment : Fragment() {

    private var _binding: FragmentKnightMovesBinding? = null
    private val binding get() = _binding!!

    private var squareA = 0
    private var squareB = 0
    private var score = 0
    private var streak = 0
    private var questionsAsked = 0
    private var totalQuestions = -1
    private var moveCount = 2

    private val squarePool = (0..63).toMutableList()
    private var poolIndex = 0

    private val knightDeltas = listOf(
        -2 to -1, -2 to 1, -1 to -2, -1 to 2,
        1 to -2,  1 to 2,  2 to -1,  2 to 1
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKnightMovesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalQuestions = arguments?.getInt("questionCount", -1) ?: -1
        moveCount      = arguments?.getInt("moveCount", 2)      ?: 2
        squarePool.shuffle()

        binding.tvMoveCount.text = "Can the knight reach B from A in $moveCount move${if (moveCount == 1) "" else "s"}?"

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnYes.setOnClickListener  { checkAnswer(true) }
        binding.btnNo.setOnClickListener   { checkAnswer(false) }

        nextQuestion()
    }

    private fun nextSquare(): Int {
        if (poolIndex >= squarePool.size) { squarePool.shuffle(); poolIndex = 0 }
        return squarePool[poolIndex++]
    }

    private fun squareName(sq: Int) = "${"abcdefgh"[sq % 8]}${sq / 8 + 1}"


    private fun reachableInExactly(from: Int, moves: Int): Set<Int> {
        var current = setOf(from)
        repeat(moves) {
            val next = mutableSetOf<Int>()
            for (sq in current) {
                val file = sq % 8
                val rank = sq / 8
                for ((df, dr) in knightDeltas) {
                    val nf = file + df
                    val nr = rank + dr
                    if (nf in 0..7 && nr in 0..7) next.add(nf + nr * 8)
                }
            }
            current = next
        }
        return current
    }

    private fun nextQuestion() {
        if (totalQuestions != -1 && questionsAsked >= totalQuestions) {
            showFinished(); return
        }

        squareA = nextSquare()
        squareB = if (Math.random() < 0.5) {
            val reachable = reachableInExactly(squareA, moveCount).filter { it != squareA }
            if (reachable.isNotEmpty()) reachable.random() else nextSquare()
        } else {
            nextSquare()
        }

        binding.tvSquares.text = "${squareName(squareA)} → ${squareName(squareB)}"
        binding.tvResult.text  = ""
        binding.btnYes.isEnabled = true
        binding.btnNo.isEnabled  = true
    }

    private fun checkAnswer(guessYes: Boolean) {
        val correct = squareB in reachableInExactly(squareA, moveCount)
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
            binding.tvResult.text = if (correct) "Wrong — knight CAN reach in $moveCount move${if (moveCount == 1) "" else "s"}"
            else         "Wrong — knight CANNOT reach in $moveCount move${if (moveCount == 1) "" else "s"}"
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