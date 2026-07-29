package com.example.blindfoldchess.ui.history

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

/**
 * Draws a plain 8x8 chess board and renders pieces using the chess_merida.ttf
 * font's plain K/Q/R/B/N/P (white) and k/q/r/b/n/p (black) glyphs.
 *
 * We deliberately draw the light/dark square backgrounds ourselves rather than
 * relying on the font's own colored-square glyph variants (that font also
 * contains extra letters for pieces pre-rendered on a given square color, but
 * we didn't want to guess that letter mapping without visually confirming it
 * against your specific font build). This approach only depends on the
 * standard piece letters, which are confirmed present in the font.
 */
class ChessMeridaBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var board: Array<CharArray> = Array(8) { CharArray(8) { ' ' } }

    private val lightSquarePaint = Paint().apply { color = Color.parseColor("#EEEED2") }
    private val darkSquarePaint = Paint().apply { color = Color.parseColor("#6B8E4E") }
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val piecePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private val chessTypeface by lazy {
        // Loaded from assets rather than res/font: this font's legacy symbol
        // cmap encoding fails Android's res/font resource-compiler validation
        // at runtime (Resources.NotFoundException), even though the file is a
        // valid TTF. Typeface.createFromAsset bypasses that validation.
        // Requires chess_merida.ttf placed at assets/fonts/chess_merida.ttf
        Typeface.createFromAsset(context.assets, "fonts/chess_merida.ttf")
    }

    /** Replace the position being displayed. Pass a board as produced by SimpleChessBoard. */
    fun setPosition(newBoard: Array<CharArray>) {
        board = newBoard
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Keep it square, sized to whichever dimension is smaller.
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > 0 && height > 0) minOf(width, height) else maxOf(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cell = width / 8f
        piecePaint.typeface = chessTypeface
        piecePaint.textSize = cell * 0.8f

        for (row in 0..7) {
            for (col in 0..7) {
                val isLight = (row + col) % 2 == 0
                val left = col * cell
                val top = row * cell
                canvas.drawRect(left, top, left + cell, top + cell, if (isLight) lightSquarePaint else darkSquarePaint)

                val piece = board[row][col]
                if (piece != ' ') {
                    // Vertical centering for text baseline.
                    val fm = piecePaint.fontMetrics
                    val textY = top + cell / 2f - (fm.ascent + fm.descent) / 2f
                    canvas.drawText(piece.toString(), left + cell / 2f, textY, piecePaint)
                }
            }
        }

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
    }
}