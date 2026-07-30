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

class NormalChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boardDrawable: Drawable? = null

    // Subtle yellow-green highlight overlay for selected/moved squares
    private val highlightColor = Color.parseColor("#66FFEB3B")
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = highlightColor
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
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
    private var offsetX = 0f
    private var offsetY = 0f

    private val pieceDrawables = mutableMapOf<String, Drawable>()

    private var selectedSquare: Int? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        loadPieceDrawables()
        loadBoardTheme()
    }

    fun loadBoardTheme() {
        val prefs = context.getSharedPreferences("chess_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("selected_board_theme", "blue") ?: "blue"

        val resId = context.resources.getIdentifier(themeName, "drawable", context.packageName)
        boardDrawable = if (resId != 0) ContextCompat.getDrawable(context, resId) else ContextCompat.getDrawable(context, R.drawable.blue)
        invalidate()
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
        val boardSide = minOf(w, h).toFloat()
        squareSize = boardSide / 8f

        offsetX = (w - boardSide) / 2f
        offsetY = (h - boardSide) / 2f

        labelPaint.textSize = squareSize * 0.2f
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val boardSize = squareSize * 8
        boardDrawable?.let {
            it.setBounds(
                offsetX.toInt(),
                offsetY.toInt(),
                (offsetX + boardSize).toInt(),
                (offsetY + boardSize).toInt()
            )
            it.draw(canvas)
        }

        for (rank in 0..7) {
            for (file in 0..7) {
                val square = rank * 8 + file

                if (square in highlightedSquares) {
                    val displayRank = if (isFlipped) rank else 7 - rank
                    val displayFile = if (isFlipped) 7 - file else file

                    val left = offsetX + displayFile * squareSize
                    val top = offsetY + displayRank * squareSize
                    val right = left + squareSize
                    val bottom = top + squareSize

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

        val left = (offsetX + displayFile * squareSize).toInt()
        val top = (offsetY + displayRank * squareSize).toInt()
        val right = (left + squareSize).toInt()
        val bottom = (top + squareSize).toInt()

        pieceDrawables[piece]?.let { drawable ->
            val pieceDrawable = drawable.mutate()
            pieceDrawable.setBounds(left, top, right, bottom)
            pieceDrawable.draw(canvas)
        }
    }

    private fun drawLabels(canvas: Canvas) {
        val files = "abcdefgh"
        val padding = squareSize * 0.05f

        for (i in 0..7) {
            val displayFileChar = if (isFlipped) files[7 - i] else files[i]
            canvas.drawText(
                displayFileChar.toString(),
                offsetX + i * squareSize + padding,
                offsetY + 8 * squareSize - padding,
                labelPaint
            )

            val displayRankNum = if (isFlipped) (8 - i) else (i + 1)
            canvas.drawText(
                displayRankNum.toString(),
                offsetX + padding,
                offsetY + (7 - i) * squareSize + labelPaint.textSize + padding,
                labelPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event)
        }

        val relativeX = event.x - offsetX
        val relativeY = event.y - offsetY

        if (relativeX < 0 || relativeX >= squareSize * 8 || relativeY < 0 || relativeY >= squareSize * 8) {
            return false
        }

        val displayFile = (relativeX / squareSize).toInt().coerceIn(0, 7)
        val displayRank = (relativeY / squareSize).toInt().coerceIn(0, 7)

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
            val prevSelection = currentSelection
            selectedSquare = null

            if (prevSelection == square) {
                invalidate()
            } else {
                onMoveAttempt?.invoke(prevSelection, square)
            }
        }

        return true
    }
}