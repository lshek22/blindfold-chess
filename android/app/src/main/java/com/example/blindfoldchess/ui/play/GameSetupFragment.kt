package com.example.blindfoldchess.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blindfoldchess.R

class GameSetupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_setup, container, false)

        val radioGroup = view.findViewById<RadioGroup>(R.id.sideRadioGroup)
        val pieceStyleSpinner = view.findViewById<Spinner>(R.id.pieceStyleSpinner)
        val gameModeSpinner = view.findViewById<Spinner>(R.id.gameModeSpinner)
        val difficultySpinner = view.findViewById<Spinner>(R.id.difficultySpinner)
        val btnStartGame = view.findViewById<Button>(R.id.btnStartGame)

        val styleOptions = listOf(
            "Black & White Checkers",
            "All White Checkers",
            "Invisible Pieces",
            "Completely Blind (Text Input)"
        )
        val styleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, styleOptions)
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pieceStyleSpinner.adapter = styleAdapter

        val modeOptions = listOf("Standard Full Game", "Kings & Pawns Only")
        val modeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modeOptions)
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gameModeSpinner.adapter = modeAdapter

        val difficultyOptions = listOf("Easy", "Medium", "Hard", "Master")
        val difficultyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficultyOptions)
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = difficultyAdapter

        btnStartGame.setOnClickListener {
            val selectedSide = if (radioGroup.checkedRadioButtonId == R.id.radioWhite) "white" else "black"

            val pieceStyle = when (pieceStyleSpinner.selectedItem?.toString()) {
                "All White Checkers" -> "all_white"
                "Invisible Pieces" -> "invisible"
                "Completely Blind (Text Input)" -> "text_only"
                else -> "checkers"
            }

            val gameVariant = if (gameModeSpinner.selectedItem?.toString() == "Kings & Pawns Only") {
                "pawns_only"
            } else {
                "standard"
            }

            val difficulty = when (difficultySpinner.selectedItem?.toString()) {
                "Easy" -> "easy"
                "Medium" -> "medium"
                "Hard" -> "hard"
                "Master" -> "master"
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