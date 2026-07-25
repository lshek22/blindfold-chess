package com.example.blindfoldchess.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PuzzleDao {
    @Query("SELECT * FROM local_puzzles WHERE rating >= :userRating ORDER BY rating ASC LIMIT 1")
    fun getNextLadderPuzzle(userRating: Int): PuzzleEntity?
}