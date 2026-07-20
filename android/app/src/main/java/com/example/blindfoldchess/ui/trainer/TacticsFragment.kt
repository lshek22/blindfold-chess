package com.example.blindfoldchess.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.blindfoldchess.Engine
import com.example.blindfoldchess.data.AppDatabase
import com.example.blindfoldchess.data.PuzzleDatabase
import com.example.blindfoldchess.databinding.FragmentTacticsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TacticsFragment : Fragment() {

    private var _binding: FragmentTacticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var engine: Engine

    private var targetMoves = listOf<String>()
    private var currentMoveIndex = 0
    private var puzzleFen = ""

    private var userCurrentRating = 1200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTacticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        engine = Engine()
        engine.initEngine()

        binding.btnSubmitPuzzleMove.setOnClickListener {
            val userMove = binding.edtPuzzleInput.text.toString().trim().lowercase()
            if (userMove.length == 4) {
                binding.edtPuzzleInput.text.clear()
                verifyPuzzleMove(userMove)
            }
        }

        binding.puzzleChessBoard.onMoveAttempt = { from, to ->
            val moveString = convertMoveIndicesToString(from, to)
            verifyPuzzleMove(moveString)
        }

        loadProgressivePuzzle()
    }

    private fun convertMoveIndicesToString(from: Int, to: Int): String {
        val files = "abcdefgh"
        val fromFile = files[from % 8]
        val fromRank = (from / 8) + 1
        val toFile = files[to % 8]
        val toRank = (to / 8) + 1
        return "$fromFile$fromRank$toFile$toRank"
    }

    private fun loadProgressivePuzzle() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = PuzzleDatabase.getDatabase(requireContext())
                val puzzle = db.puzzleDao().getNextLadderPuzzle(userCurrentRating)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    if (puzzle != null) {
                        puzzleFen = puzzle.fen

                        targetMoves = puzzle.solution.split(" ")

                        initializePuzzleBoard()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Amazing! You completed the entire database ladder!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    android.util.Log.e("TacticsDebug", "Database Error", e)
                    Toast.makeText(requireContext(), "DB Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initializePuzzleBoard() {
        engine.setPosition(puzzleFen)
        updatePuzzleUIBoard()
        currentMoveIndex = 0

        binding.txtPuzzleHint.text = "Difficulty: $userCurrentRating. Find the best move!"
    }

    private fun verifyPuzzleMove(move: String) {
        if (targetMoves.isEmpty() || currentMoveIndex >= targetMoves.size) return

        val correctNextMove = targetMoves[currentMoveIndex]

        if (move == correctNextMove) {
            engine.makeMove(move)
            updatePuzzleUIBoard()
            currentMoveIndex++

            if (currentMoveIndex >= targetMoves.size) {
                binding.txtPuzzleHint.text = "Excellent! Puzzle completely solved!"
                Toast.makeText(requireContext(), "Correct! 🎉 +15 Rating", Toast.LENGTH_SHORT).show()

                userCurrentRating += 15
                loadProgressivePuzzle()
            } else {
                val opponentCounterMove = targetMoves[currentMoveIndex]
                engine.makeMove(opponentCounterMove)
                updatePuzzleUIBoard()
                currentMoveIndex++

                binding.txtPuzzleHint.text = "Correct move! Keep going..."
            }
        } else {
            binding.edtPuzzleInput.error = "Incorrect move, try a different line!"
            Toast.makeText(requireContext(), "Wrong move. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePuzzleUIBoard() {
        binding.puzzleChessBoard.piecesPosition = parseBoard(engine.getBoard())
    }

    private fun parseBoard(board: String): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        val cleanBoard = board.replace("\n", "").replace(" ", "")
        if (cleanBoard.length != 64) return map
        for (i in cleanBoard.indices) {
            val piece = cleanBoard[i]
            if (piece != '.') {
                val file = i % 8
                val rank = i / 8
                val viewSquare = (7 - rank) * 8 + file
                map[viewSquare] = piece.toString()
            }
        }
        return map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}