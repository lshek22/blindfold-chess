package com.example.blindfoldchess

class ChessEngine {

    init {
        System.loadLibrary("blindfoldchess")
    }

    external fun initEngine()
    external fun getBoardState(): String
    external fun sendPositionCommand(command: String): String
    external fun sendGoCommand(command: String)
    external fun thinkAndGetBestMove(command: String): String

}