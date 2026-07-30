package com.example.blindfoldchess.chess


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
        val rank = square[1] - '1'
        return (7 - rank) to file
    }

    fun applyUciMove(board: Array<CharArray>, move: String) {
        if (move.length < 4) return
        val (fr, fc) = squareToRowCol(move.substring(0, 2))
        val (tr, tc) = squareToRowCol(move.substring(2, 4))
        if (fr !in 0..7 || fc !in 0..7 || tr !in 0..7 || tc !in 0..7) return

        val piece = board[fr][fc]
        if (piece == ' ') return
        val promo = move.getOrNull(4)

        if (piece.lowercaseChar() == 'p' && fc != tc && board[tr][tc] == ' ') {
            board[fr][tc] = ' '
        }

        board[fr][fc] = ' '
        board[tr][tc] = promo?.let { p ->
            if (piece.isUpperCase()) p.uppercaseChar() else p.lowercaseChar()
        } ?: piece

        if (piece.lowercaseChar() == 'k' && kotlin.math.abs(tc - fc) == 2) {
            if (tc > fc) {
                board[fr][5] = board[fr][7]; board[fr][7] = ' '
            } else {
                board[fr][3] = board[fr][0]; board[fr][0] = ' '
            }
        }
    }

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