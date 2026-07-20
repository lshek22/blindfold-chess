package com.example.blindfoldchess.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.R
import com.example.blindfoldchess.data.GameHistoryEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameHistoryAdapter(
    private var games: List<GameHistoryEntity>,
    private val onItemClicked: (GameHistoryEntity) -> Unit,
    private val onDeleteClicked: (GameHistoryEntity) -> Unit
) : RecyclerView.Adapter<GameHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMeta: TextView = view.findViewById(R.id.txtGameMeta)
        val txtDate: TextView = view.findViewById(R.id.txtGameDate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val game = games[position]

        holder.txtMeta.text = "${game.playerSide.uppercase()} | Style: ${game.pieceStyle} | Mode: ${game.gameVariant}"

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.txtDate.text = sdf.format(Date(game.timestamp))

        holder.itemView.setOnClickListener { onItemClicked(game) }
        holder.btnDelete.setOnClickListener { onDeleteClicked(game) }
    }

    override fun getItemCount(): Int = games.size

    fun updateData(newGames: List<GameHistoryEntity>) {
        this.games = newGames
        notifyDataSetChanged()
    }
}