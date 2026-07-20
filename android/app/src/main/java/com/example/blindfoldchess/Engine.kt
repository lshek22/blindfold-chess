package com.example.blindfoldchess


class Engine {

    external fun initEngine()

    external fun setPosition(fen: String)

    external fun getBestMove(depth: Int): String

    external fun getBoard(): String

    external fun makeMove(move: String): Boolean

    external fun isCheckmate(): Boolean
    external fun isDraw(): Boolean

    companion object {
        init {
            System.loadLibrary("blindfoldchess")
        }
    }
}