package com.example.blindfoldchess.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_puzzles")
data class PuzzleEntity(
    @PrimaryKey @ColumnInfo(name = "puzzle_id") val puzzleId: String,
    @ColumnInfo(name = "fen") val fen: String,
    @ColumnInfo(name = "solution") val solution: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "themes") val themes: String
)