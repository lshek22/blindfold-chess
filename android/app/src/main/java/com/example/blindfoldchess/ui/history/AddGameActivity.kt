package com.example.blindfoldchess.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.blindfoldchess.R
import com.example.blindfoldchess.chess.SimpleChessBoard
import com.example.blindfoldchess.data.AppDatabase
import com.example.blindfoldchess.data.GameHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddGameActivity : AppCompatActivity() {

    private val moves = mutableListOf<String>()
    private val diagramPlies = mutableListOf<Int>()


    private var currentLineView: TextView? = null

    private lateinit var transcriptContainer: LinearLayout
    private lateinit var transcriptScroll: NestedScrollView
    private lateinit var editMoveInput: EditText

    private val uciMoveRegex = Regex("^[a-h][1-8][a-h][1-8][qrbnQRBN]?$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        transcriptContainer = findViewById(R.id.transcriptContainer)
        transcriptScroll = findViewById(R.id.transcriptScroll)
        editMoveInput = findViewById(R.id.editMoveInput)

        findViewById<Button>(R.id.btnAddMove).setOnClickListener { onAddMove() }
        findViewById<Button>(R.id.btnInsertBoard).setOnClickListener { onInsertBoard() }
        findViewById<Button>(R.id.btnSaveGame).setOnClickListener { onSaveGame() }
    }

    private fun onAddMove() {
        val text = editMoveInput.text.toString().trim().lowercase()
        if (!uciMoveRegex.matches(text)) {
            Toast.makeText(this, "Enter a move like e2e4 or e7e8q", Toast.LENGTH_SHORT).show()
            return
        }
        moves.add(text)
        editMoveInput.text.clear()
        appendMoveToTranscript(text)
        scrollToBottom()
    }

    private fun appendMoveToTranscript(move: String) {
        val ply = moves.size
        val moveNumber = (ply + 1) / 2
        val isWhiteMove = ply % 2 == 1

        val line = currentLineView
        if (line != null) {

            line.text = "${line.text} $move"
            if (!isWhiteMove) currentLineView = null
        } else {
            val prefix = if (isWhiteMove) "$moveNumber" else "$moveNumber..."
            val newLine = TextView(this).apply {
                text = "$prefix $move"
                textSize = 16f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(4) }
            }
            transcriptContainer.addView(newLine)
            currentLineView = if (isWhiteMove) newLine else null
        }
    }

    private fun onInsertBoard() {
        if (moves.isEmpty()) {
            Toast.makeText(this, "Add a move first", Toast.LENGTH_SHORT).show()
            return
        }
        val board = SimpleChessBoard.replay(moves, moves.size)
        diagramPlies.add(moves.size)
        val diagram = ChessMeridaBoardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(160), dp(160)).apply {
                topMargin = dp(8)
                bottomMargin = dp(8)
            }
            setPosition(board)
        }
        transcriptContainer.addView(diagram)
        currentLineView = null
        scrollToBottom()
    }

    private fun scrollToBottom() {
        transcriptScroll.post { transcriptScroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun onSaveGame() {
        if (moves.isEmpty()) {
            Toast.makeText(this, "Add at least one move first", Toast.LENGTH_SHORT).show()
            return
        }


        val snapshotFen = SimpleChessBoard.toFenPlacement(SimpleChessBoard.replay(moves, moves.size))

        val newGame = GameHistoryEntity(
            playerSide = "White",
            pieceStyle = "Standard",
            gameVariant = "Manual Entry",
            moveLogs = moves.joinToString(" "),
            timestamp = System.currentTimeMillis(),
            isManual = true,
            snapshotFen = snapshotFen,
            snapshotMoveIndex = moves.size,
            diagramPlies = diagramPlies.joinToString(",")
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(this@AddGameActivity).gameHistoryDao()
            dao.insertGame(newGame)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddGameActivity, "Game saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}