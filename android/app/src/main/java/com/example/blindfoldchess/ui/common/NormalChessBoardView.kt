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

class NormalChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val lightSquareColor = Color.parseColor("#F0D9B5")
    private val darkSquareColor = Color.parseColor("#B58863")
    private val highlightColor = Color.parseColor("#8855FF00")

    private val squarePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = highlightColor
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#333333")
        typeface = Typeface.MONOSPACE
    }

    var piecesPosition: Map<Int, String> = emptyMap()
        set(value) {
            field = value
            invalidate()
        }

    var highlightedSquares: Set<Int> = emptySet()
        set(value) {
            field = value
            invalidate()
        }

    var showLabels = true
        set(value) {
            field = value
            invalidate()
        }

    var isFlipped = false
        set(value) {
            field = value
            invalidate()
        }

    var pieceStyle = "normal"
        set(value) {
            field = value
            loadPieceDrawables()
            invalidate()
        }

    var onMoveAttempt: ((Int, Int) -> Unit)? = null

    var canMovePiece: ((String) -> Boolean)? = null

    private var squareSize = 0f

    private val pieceDrawables = mutableMapOf<String, Drawable>()

    private var selectedSquare: Int? = null

    private var isDragging = false

    private var dragX = 0f

    private var dragY = 0f

    init {
        loadPieceDrawables()
    }

    private fun loadPieceDrawables() {

        val pieces = listOf(
            "P", "N", "B", "R", "Q", "K",
            "p", "n", "b", "r", "q", "k"
        )

        for (piece in pieces) {

            val resName = getResourceNameForPiece(piece)

            val resId = context.resources.getIdentifier(
                resName,
                "drawable",
                context.packageName
            )

            if (resId != 0) {
                ContextCompat.getDrawable(context, resId)?.let {
                    pieceDrawables[piece] = it
                }
            }
        }
    }

    private fun getResourceNameForPiece(piece: String): String {
        if (pieceStyle == "invisible") {
            return "empty_placeholder"
        }

        if (pieceStyle == "all_white") {
            return "white_checker"
        }

        val prefix = if (piece[0].isUpperCase()) "white_" else "black_"

        if (pieceStyle == "checkers") {
            return prefix + "checker"
        }

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

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {

        super.onSizeChanged(w, h, oldw, oldh)

        squareSize = minOf(w, h) / 8f

        labelPaint.textSize = squareSize * 0.2f
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth

        setMeasuredDimension(width, width)
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (rank in 0..7) {
            for (file in 0..7) {
                val displayRank = if (isFlipped) rank else 7 - rank
                val displayFile = if (isFlipped) 7 - file else file

                val square = rank * 8 + file
                val left = displayFile * squareSize
                val top = displayRank * squareSize
                val right = left + squareSize
                val bottom = top + squareSize

                squarePaint.color = if ((rank + file) % 2 == 0) lightSquareColor else darkSquareColor
                canvas.drawRect(left, top, right, bottom, squarePaint)

                if (square in highlightedSquares) {
                    canvas.drawRect(left, top, right, bottom, highlightPaint)
                }
            }
        }

        if (showLabels) {
            drawLabels(canvas)
        }

        for ((square, piece) in piecesPosition) {
            drawPiece(canvas, square, piece)
        }
    }

    private fun drawPiece(canvas: Canvas, square: Int, piece: String) {
        val file = square % 8
        val rank = square / 8

        val displayFile = if (isFlipped) 7 - file else file
        val displayRank = if (isFlipped) rank else 7 - rank

        val left = (displayFile * squareSize).toInt()
        val top = (displayRank * squareSize).toInt()

        pieceDrawables[piece]?.let {
            it.setBounds(left, top, left + squareSize.toInt(), top + squareSize.toInt())
            it.draw(canvas)
        }
    }

    private fun drawLabels(canvas: Canvas) {
        val files = "abcdefgh"
        val padding = squareSize * 0.05f

        for (i in 0..7) {
            val displayFileChar = if (isFlipped) files[7 - i] else files[i]
            canvas.drawText(
                displayFileChar.toString(),
                i * squareSize + padding,
                8 * squareSize - padding,
                labelPaint
            )

            val displayRankNum = if (isFlipped) (8 - i) else (i + 1)
            canvas.drawText(
                displayRankNum.toString(),
                padding,
                (7 - i) * squareSize + labelPaint.textSize + padding,
                labelPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event)
        }

        val displayFile = (event.x / squareSize).toInt().coerceIn(0, 7)
        val displayRank = (event.y / squareSize).toInt().coerceIn(0, 7)

        val file = if (isFlipped) 7 - displayFile else displayFile
        val rank = if (isFlipped) displayRank else 7 - displayRank

        val square = rank * 8 + file

        val currentSelection = selectedSquare

        if (currentSelection == null) {
            piecesPosition[square]?.let { piece ->
                if (canMovePiece?.invoke(piece) != false) {
                    selectedSquare = square
                    highlightedSquares = setOf(square)
                    invalidate()
                }
            }
        } else {
            if (currentSelection == square) {
                selectedSquare = null
                highlightedSquares = emptySet()
                invalidate()
            } else {
                selectedSquare = null
                highlightedSquares = emptySet()
                invalidate()

                onMoveAttempt?.invoke(currentSelection, square)
            }
        }

        return true
    }
}