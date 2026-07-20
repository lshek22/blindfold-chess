package com.example.blindfoldchess.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    suspend fun getAllGames(): List<GameHistoryEntity>

    @Insert
    suspend fun insertGame(game: GameHistoryEntity)

    @Delete
    suspend fun deleteGame(game: GameHistoryEntity)
}