package com.example.blindfoldchess.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.blindfoldchess.R

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val boardDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.blue)

    private val highlightColor = Color.parseColor("#8855FF00")
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = highlightColor
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        typeface = Typeface.MONOSPACE
    }

    private val piecePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    var isFlipped: Boolean = false
        set(value) { field = value; invalidate() }

    var pieceStyle: String = "normal"
        set(value) { field = value; invalidate() }

    var piecesPosition: Map<Int, String> = emptyMap()
        set(value) { field = value; invalidate() }

    var highlightedSquares: Set<Int> = emptySet()
        set(value) { field = value; invalidate() }

    var showLabels: Boolean = true
        set(value) { field = value; invalidate() }

    var canMovePiece: ((String) -> Boolean)? = null
    var onMoveAttempt: ((from: Int, to: Int) -> Unit)? = null
    var onSquareTapped: ((square: Int) -> Unit)? = null

    private var selectedSquare: Int? = null
    private var squareSize = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        squareSize = minOf(w, h) / 8f
        labelPaint.textSize = squareSize * 0.2f
        piecePaint.textSize = squareSize * 0.65f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (squareSize == 0f) return

        boardDrawable?.let {
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
        }

        for (square in 0..63) {
            val visualSquare = if (isFlipped) 63 - square else square
            val file = visualSquare % 8
            val rank = visualSquare / 8

            val left = file * squareSize
            val top = rank * squareSize
            val right = left + squareSize
            val bottom = top + squareSize

            if (square == selectedSquare || square in highlightedSquares) {
                canvas.drawRect(left, top, right, bottom, highlightPaint)
            }
        }

        drawPieces(canvas)

        if (showLabels) drawLabels(canvas)
    }

    private fun drawPieces(canvas: Canvas) {
        for ((square, piece) in piecesPosition) {
            val visualSquare = if (isFlipped) 63 - square else square
            val file = visualSquare % 8
            val rank = visualSquare / 8

            val cx = file * squareSize + squareSize / 2f
            val cy = rank * squareSize + squareSize / 2f - (piecePaint.descent() + piecePaint.ascent()) / 2f

            val symbol = getPieceSymbol(piece)
            if (symbol.isNotEmpty()) {
                canvas.drawText(symbol, cx, cy, piecePaint)
            }
        }
    }

    private fun getPieceSymbol(piece: String): String {
        return when (pieceStyle) {
            "invisible" -> ""
            "checkers", "all_white" -> "●"
            else -> when (piece) {
                "K" -> "♔"; "Q" -> "♕"; "R" -> "♖"; "B" -> "♗"; "N" -> "♘"; "P" -> "♙"
                "k" -> "♚"; "q" -> "♛"; "r" -> "♜"; "b" -> "♝"; "n" -> "♞"; "p" -> "♟"
                else -> piece
            }
        }
    }

    private fun drawLabels(canvas: Canvas) {
        val files = if (isFlipped) "hgfedcba" else "abcdefgh"
        val padding = squareSize * 0.05f

        for (i in 0..7) {
            val rankNumber = if (isFlipped) (i + 1).toString() else (8 - i).toString()
            canvas.drawText(
                files[i].toString(),
                i * squareSize + padding,
                8 * squareSize - padding,
                labelPaint
            )
            canvas.drawText(
                rankNumber,
                padding,
                i * squareSize + labelPaint.textSize + padding,
                labelPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (squareSize == 0f) return true

            val col = (event.x / squareSize).toInt().coerceIn(0, 7)
            val row = (event.y / squareSize).toInt().coerceIn(0, 7)

            var clickedSquare = row * 8 + col
            if (isFlipped) clickedSquare = 63 - clickedSquare

            onSquareTapped?.invoke(clickedSquare)

            val currentSelection = selectedSquare
            if (currentSelection == null) {
                val piece = piecesPosition[clickedSquare]
                if (piece != null && canMovePiece?.invoke(piece) == true) {
                    selectedSquare = clickedSquare
                    invalidate()
                }
            } else {
                selectedSquare = null
                invalidate()
                if (currentSelection != clickedSquare) {
                    onMoveAttempt?.invoke(currentSelection, clickedSquare)
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    fun squareName(square: Int): String {
        val file = square % 8
        val rank = 8 - square / 8
        return "${"abcdefgh"[file]}$rank"
    }

    fun squareIndex(name: String): Int {
        val file = name[0] - 'a'
        val rank = 8 - (name[1] - '0')
        return rank * 8 + file
    }
}