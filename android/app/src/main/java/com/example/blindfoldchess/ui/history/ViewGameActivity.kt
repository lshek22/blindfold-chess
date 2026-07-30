package com.example.blindfoldchess.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // ADD THIS IMPORT
import com.example.blindfoldchess.R
import com.example.blindfoldchess.chess.SimpleChessBoard
import com.example.blindfoldchess.data.GameHistoryEntity

class ViewGameActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_META = "extra_meta"
        private const val EXTRA_MOVE_LOGS = "extra_move_logs"
        private const val EXTRA_SNAPSHOT_FEN = "extra_snapshot_fen"
        private const val EXTRA_DIAGRAM_PLIES = "extra_diagram_plies"

        fun start(context: Context, game: GameHistoryEntity) {
            val intent = Intent(context, ViewGameActivity::class.java).apply {
                putExtra(EXTRA_META, "${game.playerSide.uppercase()} | Style: ${game.pieceStyle} | Mode: ${game.gameVariant}")
                putExtra(EXTRA_MOVE_LOGS, game.moveLogs)
                putExtra(EXTRA_SNAPSHOT_FEN, game.snapshotFen)
                putExtra(EXTRA_DIAGRAM_PLIES, game.diagramPlies)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_game)

        val meta = intent.getStringExtra(EXTRA_META).orEmpty()
        val moveLogs = intent.getStringExtra(EXTRA_MOVE_LOGS).orEmpty()
        val diagramPliesRaw = intent.getStringExtra(EXTRA_DIAGRAM_PLIES).orEmpty()

        val moves = if (moveLogs.isBlank()) emptyList() else moveLogs.trim().split(Regex("\\s+"))

        val diagramPlies = if (diagramPliesRaw.isBlank()) {
            moves.indices.map { index -> index + 1 }.filter { ply -> ply % 2 == 0 }
        } else {
            diagramPliesRaw.split(",").mapNotNull { it.trim().toIntOrNull() }
        }

        findViewById<TextView>(R.id.txtGameTitle).text = meta
        val transcriptContainer = findViewById<LinearLayout>(R.id.transcriptContainer)

        buildTranscript(transcriptContainer, moves, diagramPlies)
    }

    private fun buildTranscript(container: LinearLayout, moves: List<String>, diagramPlies: List<Int>) {
        val remainingDiagramPlies = diagramPlies.toMutableList()
        var currentLine: TextView? = null

        for ((i, move) in moves.withIndex()) {
            val ply = i + 1
            val moveNumber = (ply + 1) / 2
            val isWhiteMove = ply % 2 == 1

            val line = currentLine
            if (line != null) {
                line.text = "${line.text} $move"
                currentLine = null
            } else {
                val prefix = if (isWhiteMove) "$moveNumber" else "$moveNumber..."
                val newLine = TextView(this).apply {
                    text = "$prefix $move"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(this@ViewGameActivity, R.color.white))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = dp(4) }
                }
                container.addView(newLine)
                currentLine = if (isWhiteMove) newLine else null
            }

            while (remainingDiagramPlies.remove(ply)) {
                val diagram = ChessMeridaBoardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(dp(160), dp(160)).apply {
                        topMargin = dp(8)
                        bottomMargin = dp(8)
                    }
                    setPosition(SimpleChessBoard.replay(moves, ply))
                }
                container.addView(diagram)
                currentLine = null
            }
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}