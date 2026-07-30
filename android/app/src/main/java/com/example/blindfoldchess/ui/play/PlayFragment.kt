package com.example.blindfoldchess.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.blindfoldchess.Engine
import com.example.blindfoldchess.chess.SimpleChessBoard
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
    private lateinit var soundManager: SoundManager

    private var playerSide = "white"
    private var initialStyle = "normal"
    private var gameVariant = "standard"
    private var difficulty = "medium"
    private var halfMoveClock = 0

    private val dynamicMoveLog = StringBuilder()
    private var isGameFinished = false
    private var isWhiteTurn = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)

        playerSide = arguments?.getString("selectedSide") ?: "white"
        initialStyle = arguments?.getString("pieceStyle") ?: "normal"
        gameVariant = arguments?.getString("gameVariant") ?: "standard"
        difficulty = arguments?.getString("difficulty") ?: "medium"
        halfMoveClock = arguments?.getInt("halfMoveCount", 0) ?: 0

        binding.chessBoard.isFlipped = (playerSide == "black")
        binding.chessBoard.pieceStyle = initialStyle

        engine = Engine()
        engine.initEngine()

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
            if (isGameFinished) {
                false
            } else if (playerSide == "pass_and_play") {
                if (isWhiteTurn) piece[0].isUpperCase() else piece[0].isLowerCase()
            } else if (playerSide == "white") {
                piece[0].isUpperCase()
            } else {
                piece[0].isLowerCase()
            }
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
                executeGameMove(inputMove)
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
            triggerEngineMove()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = SoundManager(requireContext())
    }

    private fun executeGameMove(move: String) {
        if (isGameFinished) return

        val isCapture = checkIsCapture(move)

        if (engine.makeMove(move)) {
            val prefix = if (playerSide == "pass_and_play") {
                if (isWhiteTurn) "White: " else "Black: "
            } else {
                "You: "
            }

            dynamicMoveLog.append("$prefix$move\n")
            binding.txtGameLog.text = dynamicMoveLog.toString()

            isWhiteTurn = !isWhiteTurn

            if (playerSide == "pass_and_play") {
                binding.chessBoard.isFlipped = !isWhiteTurn
            }

            updateBoard()

            val winnerLabel = if (playerSide == "pass_and_play") {
                if (!isWhiteTurn) "White" else "Black"
            } else {
                "Engine"
            }

            if (checkAndHandleGameOver(winnerLabel)) return
            playMoveSound(isCapture)

            if (playerSide != "pass_and_play") {
                triggerEngineMove()
            }
        } else {
            binding.edtMoveInput.error = "Invalid move"
        }
    }

    private fun triggerEngineMove() {
        lifecycleScope.launch(Dispatchers.Default) {
            val bestMove = engine.getBestMove(searchDepth)
            val engineCapture = checkIsCapture(bestMove)

            engine.makeMove(bestMove)

            withContext(Dispatchers.Main) {
                dynamicMoveLog.append("Engine: $bestMove\n")
                binding.txtGameLog.text = dynamicMoveLog.toString()
                isWhiteTurn = !isWhiteTurn

                updateBoard()

                if (!checkAndHandleGameOver("You")) {
                    playMoveSound(engineCapture)
                }
            }
        }
    }

    private fun checkIsCapture(move: String): Boolean {
        if (move.length < 4) return false
        val destSquareStr = move.substring(2, 4)
        val file = destSquareStr[0] - 'a'
        val rank = destSquareStr[1].toString().toInt()
        val viewSquare = (8 - rank) * 8 + file

        return binding.chessBoard.piecesPosition.containsKey(viewSquare)
    }

    private fun playMoveSound(isCapture: Boolean) {
        if (isCapture) {
            soundManager.playSound("capture")
        } else {
            soundManager.playSound("move")
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

    private fun checkAndHandleGameOver(winnerOrCheckmated: String): Boolean {
        val isCheckmate = engine.isCheckmate()
        val isDraw = engine.isDraw()

        if (isCheckmate) {
            isGameFinished = true

            val message = if (playerSide == "pass_and_play") {
                soundManager.playSound("victory")
                "Checkmate! $winnerOrCheckmated wins!"
            } else if (winnerOrCheckmated == "Engine") {
                soundManager.playSound("victory")
                "Checkmate! You win!"
            } else {
                soundManager.playSound("defeat")
                "Checkmate! The engine wins."
            }

            showGameOverDialog(message)
            saveCurrentGameToHistory()
            return true
        } else if (isDraw) {
            isGameFinished = true
            soundManager.playSound("draw")
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
        val rawLogs = dynamicMoveLog.toString().trim()

        if (rawLogs.isEmpty()) {
            android.util.Log.d("BlindfoldChessDB", "Save skipped: Move log is empty.")
            return
        }

        // Clean move log: strip "You: ", "Engine: ", "White: ", "Black: " prefixes to extract standard UCI moves
        val uciMoves = rawLogs.lines()
            .map { line -> line.substringAfter(":").trim().lowercase() }
            .filter { line -> line.matches(Regex("^[a-h][1-8][a-h][1-8][qrbn]?$")) }

        if (uciMoves.isEmpty()) return

        val movesString = uciMoves.joinToString(" ")

        // Generate diagram markers after every full move pair (every 2 plies)
        val diagramPliesString = uciMoves.indices
            .map { index -> index + 1 }
            .filter { ply -> ply % 2 == 0 }
            .joinToString(",")

        val appContext = context?.applicationContext ?: return
        val currentTimestamp = System.currentTimeMillis()

        val rawEngineBoard = engine.getBoard()
        val cleanBoard = rawEngineBoard.replace("\n", "").replace(" ", "")
        val boardGrid = Array(8) { CharArray(8) { ' ' } }

        if (cleanBoard.length == 64) {
            for (i in 0 until 64) {
                val r = i / 8
                val c = i % 8
                val ch = cleanBoard[i]
                boardGrid[r][c] = if (ch == '.') ' ' else ch
            }
        }

        val fenPlacement = SimpleChessBoard.toFenPlacement(boardGrid)
        val activeColor = if (isWhiteTurn) "w" else "b"

        val totalHalfMoves = uciMoves.size
        val fullMoveNumber = (totalHalfMoves / 2) + 1

        val finalFen = "$fenPlacement $activeColor - - $halfMoveClock $fullMoveNumber"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(appContext)
                val newEntry = GameHistoryEntity(
                    id = 0,
                    timestamp = currentTimestamp,
                    playerSide = playerSide,
                    pieceStyle = initialStyle,
                    gameVariant = gameVariant,
                    moveLogs = movesString,
                    isManual = false,
                    snapshotFen = finalFen,
                    snapshotMoveIndex = totalHalfMoves,
                    diagramPlies = diagramPliesString // Pass auto-generated diagram positions
                )
                db.gameHistoryDao().insertGame(newEntry)
                android.util.Log.d("BlindfoldChessDB", "Game saved successfully.")
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
        soundManager.release()
        if (!isGameFinished) {
            saveCurrentGameToHistory()
        }
        super.onDestroyView()
        _binding = null
    }
}