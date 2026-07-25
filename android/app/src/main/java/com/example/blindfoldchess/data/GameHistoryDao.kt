package com.example.blindfoldchess.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun getAllGames(): List<GameHistoryEntity>

    @Insert
    fun insertGame(game: GameHistoryEntity): Long

    @Delete
    fun deleteGame(game: GameHistoryEntity): Int
}