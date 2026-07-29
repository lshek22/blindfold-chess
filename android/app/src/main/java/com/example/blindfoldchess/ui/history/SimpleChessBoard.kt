package com.example.blindfoldchess.chess

/**
 * Minimal chess board replay engine.
 *
 * IMPORTANT: this does NOT validate legality (no check detection, no move
 * generation). It only *applies* moves you already trust (e.g. typed by the
 * user, or produced by a real engine elsewhere). It handles: normal moves,
 * captures, basic castling (king moves two files), simple en-passant, and
 * promotions. If you need actual move validation/SAN parsing later, pull in
 * a real chess library (e.g. bhlangonijr/chesslib) instead of extending this.
 *
 * Board layout: board[0] = rank 8 (top, black's back rank) ... board[7] = rank 1.
 * board[row][col], col 0 = file 'a' ... col 7 = file 'h'.
 * Uppercase = white piece, lowercase = black piece, ' ' = empty.
 */
object SimpleChessBoard {

    fun initialBoard(): Array<CharArray> = arrayOf(
        charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'),
        charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
        CharArray(8) { ' ' },
        CharArray(8) { ' ' },
        CharArray(8) { ' ' },
        CharArray(8) { ' ' },
        charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
        charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R')
    )

    /** Returns a fresh board with the first [uptoMoveIndex] moves from [moves] applied. */
    fun replay(moves: List<String>, uptoMoveIndex: Int): Array<CharArray> {
        val board = initialBoard()
        val count = uptoMoveIndex.coerceIn(0, moves.size)
        for (i in 0 until count) {
            applyUciMove(board, moves[i])
        }
        return board
    }

    private fun squareToRowCol(square: String): Pair<Int, Int> {
        val file = square[0] - 'a'
        val rank = square[1] - '1' // 0 = rank1
        return (7 - rank) to file
    }

    /** Mutates [board] in place by applying one UCI-style move, e.g. "e2e4", "e7e8q". */
    fun applyUciMove(board: Array<CharArray>, move: String) {
        if (move.length < 4) return
        val (fr, fc) = squareToRowCol(move.substring(0, 2))
        val (tr, tc) = squareToRowCol(move.substring(2, 4))
        if (fr !in 0..7 || fc !in 0..7 || tr !in 0..7 || tc !in 0..7) return

        val piece = board[fr][fc]
        if (piece == ' ') return
        val promo = move.getOrNull(4)

        // Simple en-passant: pawn moving diagonally onto an empty square.
        if (piece.lowercaseChar() == 'p' && fc != tc && board[tr][tc] == ' ') {
            board[fr][tc] = ' '
        }

        board[fr][fc] = ' '
        board[tr][tc] = promo?.let { p ->
            if (piece.isUpperCase()) p.uppercaseChar() else p.lowercaseChar()
        } ?: piece

        // Basic castling: king moved two files -> move the matching rook too.
        if (piece.lowercaseChar() == 'k' && kotlin.math.abs(tc - fc) == 2) {
            if (tc > fc) {
                board[fr][5] = board[fr][7]; board[fr][7] = ' '
            } else {
                board[fr][3] = board[fr][0]; board[fr][0] = ' '
            }
        }
    }

    /** FEN piece-placement field only (no side-to-move/castling/etc), handy for snapshots. */
    fun toFenPlacement(board: Array<CharArray>): String {
        return board.joinToString("/") { row ->
            val sb = StringBuilder()
            var empty = 0
            for (c in row) {
                if (c == ' ') {
                    empty++
                } else {
                    if (empty > 0) { sb.append(empty); empty = 0 }
                    sb.append(c)
                }
            }
            if (empty > 0) sb.append(empty)
            sb.toString()
        }
    }

    /** Reverses toFenPlacement: turns a saved FEN placement string back into a board grid. */
    fun fromFenPlacement(fen: String): Array<CharArray> {
        val board = Array(8) { CharArray(8) { ' ' } }
        val rows = fen.split("/")
        for (row in 0 until minOf(8, rows.size)) {
            var col = 0
            for (c in rows[row]) {
                if (col > 7) break
                if (c.isDigit()) {
                    col += c.digitToInt()
                } else {
                    board[row][col] = c
                    col++
                }
            }
        }
        return board
    }
}