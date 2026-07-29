package com.example.blindfoldchess.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.R
import com.example.blindfoldchess.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameHistoryAdapter
    private var isManual: Boolean = false

    companion object {
        private const val ARG_IS_MANUAL = "arg_is_manual"

        fun newInstance(isManual: Boolean): GameListFragment {
            val fragment = GameListFragment()
            val args = Bundle().apply {
                putBoolean(ARG_IS_MANUAL, isManual)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isManual = arguments?.getBoolean(ARG_IS_MANUAL) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GameHistoryAdapter(
            games = emptyList(),
            onItemClicked = { selectedGame ->
                ViewGameActivity.start(requireContext(), selectedGame)
            },
            onDeleteClicked = { targetGame ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getDatabase(requireContext()).gameHistoryDao()
                    dao.deleteGame(targetGame)
                    loadGameHistory()
                }
            }
        )
        recyclerView.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        loadGameHistory()
    }

    private fun loadGameHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(requireContext()).gameHistoryDao()

            // Adjust this call based on your DAO implementation
            // e.g., dao.getGamesBySource(isManual) or filtering the list
            val allGames = dao.getAllGames()
            val filteredGames = allGames.filter { it.isManual == isManual }

            withContext(Dispatchers.Main) {
                adapter.updateData(filteredGames)
            }
        }
    }
}