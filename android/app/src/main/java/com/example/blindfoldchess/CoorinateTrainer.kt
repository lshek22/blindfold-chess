package com.example.blindfoldchess

import kotlin.random.Random

class CoordinateTrainer(private val totalQuestions: Int) {

    private var currentQuestionIndex = 0
    private var score = 0
    private var currentTargetIndex = -1

    interface TrainerListener {
        fun onNextQuestion(coordinateText: String)
        fun onTrainerFinished(finalScore: Int, total: Int)
    }

    private var listener: TrainerListener? = null

    fun setListener(listener: TrainerListener) {
        this.listener = listener
    }

    fun start() {
        currentQuestionIndex = 0
        score = 0
        generateNextQuestion()
    }

    private fun generateNextQuestion() {
        if (currentQuestionIndex >= totalQuestions) {
            listener?.onTrainerFinished(score, totalQuestions)
            return
        }

        currentTargetIndex = Random.nextInt(64)
        val coordText = indexToCoordinate(currentTargetIndex)

        listener?.onNextQuestion(coordText)
    }

    fun submitAnswer(clickedIndex: Int): Boolean {
        val isCorrect = clickedIndex == currentTargetIndex
        if (isCorrect) {
            score++
        }

        currentQuestionIndex++
        generateNextQuestion()
        return isCorrect
    }

    private fun indexToCoordinate(index: Int): String {
        val row = index / 8
        val col = index % 8
        val fileChar = ('a' + col)
        val rankNum = 8 - row
        return "$fileChar$rankNum"
    }
}