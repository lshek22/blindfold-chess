package com.example.blindfoldchess.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blindfoldchess.R
import com.example.blindfoldchess.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val prefs = requireContext().getSharedPreferences("chess_prefs", Context.MODE_PRIVATE)
        val currentTheme = prefs.getString("selected_board_theme", "blue") ?: "blue"

        val availableThemes = listOf(
            BoardTheme("blue", "Blue", R.drawable.bluethumbnail),
            BoardTheme("blue2", "Blue2", R.drawable.blue2thumbnail),
            BoardTheme("blue3", "Blue3", R.drawable.blue3thumbnail),
            BoardTheme("bluemarble", "Bluemarble", R.drawable.bluemarblethumbnail),
            BoardTheme("brown", "Brown", R.drawable.brownthumbnail),
            BoardTheme("canvas2", "Canvas2", R.drawable.canvas2thumbnail),
            BoardTheme("green", "Green", R.drawable.greenthumbnail),
            BoardTheme("greenplastic", "Greenplastic", R.drawable.greenplasticthumbnail),
            BoardTheme("grey", "Grey", R.drawable.greythumbnail),
            BoardTheme("horsey", "Horsey", R.drawable.horseythumbnail),
            BoardTheme("ic", "Ic", R.drawable.icthumbnail),
            BoardTheme("leather", "Leather", R.drawable.leatherthumbnail),
            BoardTheme("maple", "Maple", R.drawable.maplethumbnail),
            BoardTheme("maple2", "Maple2", R.drawable.maple2thumbnail),
            BoardTheme("marble", "Marble", R.drawable.marblethumbnail),
            BoardTheme("metal", "Metal", R.drawable.metalthumbnail),
            BoardTheme("olive", "Olive", R.drawable.olivethumbnail),
            BoardTheme("pinkpyramid", "Pinkpyramid", R.drawable.pinkpyramidthumbnail),
            BoardTheme("purple", "Purple", R.drawable.purplethumbnail),
            BoardTheme("purplediag", "Purplediag", R.drawable.purplediagthumbnail),
            BoardTheme("wood", "Wood", R.drawable.woodthumbnail),
            BoardTheme("wood2", "Wood2", R.drawable.wood2thumbnail),
            BoardTheme("wood3", "Wood3", R.drawable.wood3thumbnail),
            BoardTheme("wood4", "Wood4", R.drawable.wood4thumbnail),


        )

        val adapter = BoardThemeAdapter(availableThemes, currentTheme) { selectedTheme ->
            prefs.edit().putString("selected_board_theme", selectedTheme.id).apply()
        }

        binding.rvBoardThemes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBoardThemes.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}