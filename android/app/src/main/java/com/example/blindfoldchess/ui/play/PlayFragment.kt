package com.example.blindfoldchess.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.blindfoldchess.Engine
import com.example.blindfoldchess.data.AppDatabase
import com.example.blindfoldchess.data.GameHistoryEntity
import com.example.blindfoldchess.databinding.FragmentPlayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var engine: Engine

    private var playerSide = "white"
    private var initialStyle = "normal"
    private var gameVariant = "standard"
    private val dynamicMoveLog = StringBuilder()
    private var isGameFinished = false

    private var difficulty = "medium"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlayBinding.inflate(inflater, container, false)

        playerSide = arguments?.getString("selectedSide") ?: "white"
        initialStyle = arguments?.getString("pieceStyle") ?: "normal"
        gameVariant = arguments?.getString("gameVariant") ?: "standard"

        binding.chessBoard.isFlipped = (playerSide == "black")
        binding.chessBoard.pieceStyle = initialStyle

        engine = Engine()
        engine.initEngine()

        difficulty = arguments?.getString("difficulty") ?: "medium"


        if (gameVariant == "pawns_only") {
            engine.setPosition("4k3/pppppppp/8/8/8/8/PPPPPPPP/4K3 w ---- - 0 1")
        } else {
            engine.setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        }

        if (initialStyle == "text_only") {
            binding.chessBoard.visibility = View.GONE
            binding.textGameConsole.visibility = View.VISIBLE
        }

        binding.chessBoard.canMovePiece = { piece ->
            if (isGameFinished) false
            else if (playerSide == "white") piece[0].isUpperCase() else piece[0].isLowerCase()
        }

        binding.chessBoard.onMoveAttempt = { from, to ->
            val move = convertMove(from, to)
            executeGameMove(move)
        }

        binding.btnSubmitMove.setOnClickListener {
            if (isGameFinished) return@setOnClickListener
            val inputMove = binding.edtMoveInput.text.toString().trim().lowercase()
            if (inputMove.length == 4) {
                binding.edtMoveInput.setText("")

                if (engine.makeMove(inputMove)) {
                    dynamicMoveLog.append("You: $inputMove\n")
                    binding.txtGameLog.text = dynamicMoveLog.toString()
                    updateBoard()

                    if (checkAndHandleGameOver("Engine")) return@setOnClickListener

                    lifecycleScope.launch(Dispatchers.Default) {
                        val bestMove = engine.getBestMove(searchDepth)

                        engine.makeMove(bestMove)

                        withContext(Dispatchers.Main) {
                            dynamicMoveLog.append("Engine: $bestMove\n")
                            binding.txtGameLog.text = dynamicMoveLog.toString()
                            updateBoard()

                            checkAndHandleGameOver("You")
                        }
                    }
                } else {
                    binding.edtMoveInput.error = "Invalid move"
                }
            }
        }

        binding.btnToggleTheme.setOnClickListener {
            if (initialStyle == "text_only") {
                if (binding.chessBoard.visibility == View.GONE) {
                    binding.chessBoard.pieceStyle = "normal"
                    binding.chessBoard.visibility = View.VISIBLE
                    binding.textGameConsole.visibility = View.GONE
                } else {
                    binding.chessBoard.visibility = View.GONE
                    binding.textGameConsole.visibility = View.VISIBLE
                }
            } else {
                if (binding.chessBoard.pieceStyle == "normal") {
                    binding.chessBoard.pieceStyle = initialStyle
                    binding.btnToggleTheme.text = "Toggle Normal Board"
                } else {
                    binding.chessBoard.pieceStyle = "normal"
                    binding.btnToggleTheme.text = "Toggle Hidden Board"
                }
            }
        }

        updateBoard()

        if (playerSide == "black") {
            lifecycleScope.launch(Dispatchers.Default) {
                val bestMove = engine.getBestMove(searchDepth)
                engine.makeMove(bestMove)
                withContext(Dispatchers.Main) {
                    dynamicMoveLog.append("Engine: $bestMove\n")
                    binding.txtGameLog.text = dynamicMoveLog.toString()
                    updateBoard()
                }
            }
        }

        return binding.root
    }

    private fun executeGameMove(move: String) {
        if (isGameFinished) return
        if (engine.makeMove(move)) {
            dynamicMoveLog.append("You: $move\n")
            binding.txtGameLog.text = dynamicMoveLog.toString()

            updateBoard()

            if (checkAndHandleGameOver("Engine")) return

            lifecycleScope.launch(Dispatchers.Default) {
                val bestMove = engine.getBestMove(searchDepth)
                engine.makeMove(bestMove)
                withContext(Dispatchers.Main) {
                    dynamicMoveLog.append("Engine: $bestMove\n")
                    binding.txtGameLog.text = dynamicMoveLog.toString()

                    updateBoard()
                    checkAndHandleGameOver("You")
                }
            }
        }
    }
    private val searchDepth: Int
        get() = when (difficulty) {
            "easy" -> if (gameVariant == "pawns_only") 4 else 2
            "medium" -> if (gameVariant == "pawns_only") 8 else 5
            "hard" -> if (gameVariant == "pawns_only") 12 else 8
            "master" -> if (gameVariant == "pawns_only") 16 else 10
            else -> 5
        }


    private fun checkAndHandleGameOver(checkmatedPlayer: String): Boolean {
        val isCheckmate = engine.isCheckmate()
        val isDraw = engine.isDraw()

        if (isCheckmate) {
            isGameFinished = true
            val message = if (checkmatedPlayer == "You") "Checkmate! The engine wins." else "Checkmate! You win!"
            showGameOverDialog(message)
            saveCurrentGameToHistory()
            return true
        } else if (isDraw) {
            isGameFinished = true
            showGameOverDialog("The game ended in a draw!")
            saveCurrentGameToHistory()
            return true
        }
        return false
    }

    private fun showGameOverDialog(titleText: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Game Over")
            .setMessage(titleText)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun saveCurrentGameToHistory() {
        val movesString = dynamicMoveLog.toString().trim()

        if (movesString.isEmpty()) {
            android.util.Log.d("BlindfoldChessDB", "Save skipped: Move log is empty.")
            return
        }

        val currentTimestamp = System.currentTimeMillis()

        android.util.Log.d("BlindfoldChessDB", "Attempting to save game. Moves:\n$movesString")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val newEntry = GameHistoryEntity(
                    id = 0,
                    timestamp = currentTimestamp,
                    playerSide = playerSide,
                    pieceStyle = initialStyle,
                    gameVariant = gameVariant,
                    moveLogs = movesString
                )
                db.gameHistoryDao().insertGame(newEntry)

                android.util.Log.d("BlindfoldChessDB", "Database save SUCCESSFUL!")
            } catch (e: Exception) {
                android.util.Log.e("BlindfoldChessDB", "Database save FAILED: ${e.message}", e)
            }
        }
    }
    private fun updateBoard() {
        binding.chessBoard.piecesPosition = parseBoard(engine.getBoard())
    }

    private fun convertMove(from: Int, to: Int): String {
        return squareToString(viewToEngine(from)) + squareToString(viewToEngine(to))
    }

    private fun viewToEngine(square: Int): Int {
        val file = square % 8
        val rank = square / 8
        return (7 - rank) * 8 + file
    }

    private fun squareToString(square: Int): String {
        val file = square % 8
        val rank = 8 - square / 8
        return "${('a' + file)}$rank"
    }

    private fun parseBoard(board: String): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        val cleanBoard = board.replace("\n", "").replace(" ", "")

        if (cleanBoard.length != 64) {
            android.util.Log.e("ChessBoard", "Sanitized board length error. Raw: \n$board")
            return map
        }

        for (engineSquare in cleanBoard.indices) {
            val piece = cleanBoard[engineSquare]
            if (piece != '.') {
                val file = engineSquare % 8
                val rank = engineSquare / 8
                val viewSquare = (7 - rank) * 8 + file
                map[viewSquare] = piece.toString()
            }
        }
        return map
    }

    override fun onDestroyView() {
        if (!isGameFinished) {
            saveCurrentGameToHistory()
        }
        super.onDestroyView()
        _binding = null
    }
}