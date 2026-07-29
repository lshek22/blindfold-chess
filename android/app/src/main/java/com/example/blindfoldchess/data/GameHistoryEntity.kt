package com.example.blindfoldchess.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val playerSide: String,
    val pieceStyle: String,
    val gameVariant: String,
    val moveLogs: String,
    val isManual: Boolean = false,
    val snapshotFen: String? = null,
    val snapshotMoveIndex: Int = 0,
    val diagramPlies: String? = null
)