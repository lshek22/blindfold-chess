package com.example.blindfoldchess.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.R
import com.google.android.material.chip.ChipGroup

class GameSetupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_setup, container, false)

        val sideChipGroup = view.findViewById<ChipGroup>(R.id.sideChipGroup)
        val pieceStyleChipGroup = view.findViewById<ChipGroup>(R.id.pieceStyleChipGroup)
        val variantChipGroup = view.findViewById<ChipGroup>(R.id.variantChipGroup)
        val difficultyChipGroup = view.findViewById<ChipGroup>(R.id.difficultyChipGroup)
        val btnStartGame = view.findViewById<Button>(R.id.btnStartGame)

        btnStartGame.setOnClickListener {
            // Updated side evaluation to handle Pass & Play
            val selectedSide = when (sideChipGroup.checkedChipId) {
                R.id.chipBlack -> "black"
                R.id.chipPassAndPlay -> "pass_and_play"
                else -> "white"
            }

            val pieceStyle = when (pieceStyleChipGroup.checkedChipId) {
                R.id.chipAllWhite -> "all_white"
                R.id.chipInvisible -> "invisible"
                R.id.chipTextInput -> "text_only"
                else -> "checkers"
            }

            val gameVariant = if (variantChipGroup.checkedChipId == R.id.chipPawnsOnly) {
                "pawns_only"
            } else {
                "standard"
            }

            val difficulty = when (difficultyChipGroup.checkedChipId) {
                R.id.chipEasy -> "easy"
                R.id.chipHard -> "hard"
                R.id.chipMaster -> "master"
                else -> "medium"
            }

            val bundle = Bundle().apply {
                putString("selectedSide", selectedSide)
                putString("pieceStyle", pieceStyle)
                putString("gameVariant", gameVariant)
                putString("difficulty", difficulty)
            }

            findNavController().navigate(R.id.action_gameSetupFragment_to_playFragment, bundle)
        }

        return view
    }
}