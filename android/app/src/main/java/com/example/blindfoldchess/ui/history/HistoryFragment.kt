package com.example.blindfoldchess.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.R
import com.example.blindfoldchess.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GameHistoryAdapter(
            games = emptyList(),
            onItemClicked = { selectedGame ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Move History Log")
                    .setMessage(selectedGame.moveLogs.ifEmpty { "No moves recorded." })
                    .setPositiveButton("Close", null)
                    .show()
            },
            onDeleteClicked = { targetGame ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getDatabase(requireContext()).gameHistoryDao()
                    dao.deleteGame(targetGame)

                    val updatedList = dao.getAllGames()
                    withContext(Dispatchers.Main) {
                        adapter.updateData(updatedList)
                    }
                }
            }
        )
        recyclerView.adapter = adapter

        loadGameHistory()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadGameHistory()
    }

    private fun loadGameHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dbGames = AppDatabase.getDatabase(requireContext()).gameHistoryDao().getAllGames()
            withContext(Dispatchers.Main) {
                adapter.updateData(dbGames)
            }
        }
    }
}