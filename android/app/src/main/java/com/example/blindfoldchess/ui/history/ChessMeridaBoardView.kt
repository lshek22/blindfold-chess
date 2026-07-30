package com.example.blindfoldchess.ui.history

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.blindfoldchess.R

class ChessMeridaBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boardDrawable: Drawable? = null
    private val pieceDrawables = mutableMapOf<String, Drawable>()

    private var board: Array<CharArray> = Array(8) { CharArray(8) { ' ' } }

    var pieceStyle: String = "normal"
        set(value) {
            field = value
            loadPieceDrawables()
            invalidate()
        }

    init {
        loadBoardTheme()
        loadPieceDrawables()
    }


    fun loadBoardTheme() {
        val prefs = context.getSharedPreferences("chess_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("selected_board_theme", "blue") ?: "blue"

        val resId = context.resources.getIdentifier(themeName, "drawable", context.packageName)
        boardDrawable = if (resId != 0) {
            ContextCompat.getDrawable(context, resId)
        } else {
            ContextCompat.getDrawable(context, R.drawable.blue)
        }
        invalidate()
    }

    /**
     * Loads drawables matching the active piece style.
     */
    private fun loadPieceDrawables() {
        pieceDrawables.clear()
        val pieces = listOf("P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k")

        for (piece in pieces) {
            val resName = getResourceNameForPiece(piece)
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)

            if (resId != 0) {
                ContextCompat.getDrawable(context, resId)?.let {
                    pieceDrawables[piece] = it
                }
            }
        }
    }

    private fun getResourceNameForPiece(piece: String): String {
        if (pieceStyle == "invisible") return "empty_placeholder"
        if (pieceStyle == "all_white") return "white_checker"

        val prefix = if (piece[0].isUpperCase()) "white_" else "black_"
        if (pieceStyle == "checkers") return prefix + "checker"

        val pieceName = when (piece.lowercase()) {
            "p" -> "pawn"
            "n" -> "knight"
            "b" -> "bishop"
            "r" -> "rook"
            "q" -> "queen"
            "k" -> "king"
            else -> "pawn"
        }

        return prefix + pieceName
    }

    fun setPosition(newBoard: Array<CharArray>) {
        board = newBoard
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > 0 && height > 0) minOf(width, height) else maxOf(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = width / 8f

        boardDrawable?.let {
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
        }

        for (row in 0..7) {
            for (col in 0..7) {
                val pieceChar = board[row][col]
                if (pieceChar != ' ') {
                    val pieceStr = pieceChar.toString()
                    pieceDrawables[pieceStr]?.let { drawable ->
                        val left = (col * cellSize).toInt()
                        val top = (row * cellSize).toInt()
                        val right = (left + cellSize).toInt()
                        val bottom = (top + cellSize).toInt()

                        val pieceDrawable = drawable.mutate()
                        pieceDrawable.setBounds(left, top, right, bottom)
                        pieceDrawable.draw(canvas)
                    }
                }
            }
        }
    }
}