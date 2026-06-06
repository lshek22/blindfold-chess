package com.example.blindfoldchess.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val lightSquareColor = Color.parseColor("#F0D9B5")
    private val darkSquareColor  = Color.parseColor("#B58863")
    private val highlightColor   = Color.parseColor("#8855FF00") // semi-transparent green

    private val squarePaint    = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = highlightColor
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color    = Color.parseColor("#333333")
        typeface = Typeface.MONOSPACE
    }

    var highlightedSquares: Set<Int> = emptySet()
        set(value) { field = value; invalidate() }

    var onSquareTapped: ((square: Int) -> Unit)? = null

    var showLabels: Boolean = true
        set(value) { field = value; invalidate() }

    private var squareSize = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        squareSize = minOf(w, h) / 8f
        labelPaint.textSize = squareSize * 0.2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (squareSize == 0f) return

        for (rank in 0..7) {
            for (file in 0..7) {
                val square = rank * 8 + file
                val isLight = (rank + file) % 2 == 0

                val left   = file * squareSize
                val top    = (7 - rank) * squareSize
                val right  = left + squareSize
                val bottom = top + squareSize

                // draw base square color
                squarePaint.color = if (isLight) lightSquareColor else darkSquareColor
                canvas.drawRect(left, top, right, bottom, squarePaint)

                // draw highlight on top if needed
                if (square in highlightedSquares) {
                    canvas.drawRect(left, top, right, bottom, highlightPaint)
                }
            }
        }

        if (showLabels) drawLabels(canvas)
    }

    private fun drawLabels(canvas: Canvas) {
        val files = "abcdefgh"
        val padding = squareSize * 0.05f

        for (i in 0..7) {
            // file letters along the bottom edge
            canvas.drawText(
                files[i].toString(),
                i * squareSize + padding,
                8 * squareSize - padding,
                labelPaint
            )
            // rank numbers along the left edge
            canvas.drawText(
                (i + 1).toString(),
                padding,
                (7 - i) * squareSize + labelPaint.textSize + padding,
                labelPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (squareSize == 0f) return true
            val file   = (event.x / squareSize).toInt().coerceIn(0, 7)
            val rank   = (7 - (event.y / squareSize).toInt()).coerceIn(0, 7)
            val square = rank * 8 + file
            onSquareTapped?.invoke(square)
            return true
        }
        return true  // must return true on ACTION_DOWN too or UP never fires
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // force the view to be square based on width
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

    fun squareName(square: Int): String {
        val file = square % 8
        val rank = square / 8
        return "${"abcdefgh"[file]}${rank + 1}"
    }

    fun squareIndex(name: String): Int {
        val file = name[0] - 'a'
        val rank = name[1] - '1'
        return rank * 8 + file
    }
}