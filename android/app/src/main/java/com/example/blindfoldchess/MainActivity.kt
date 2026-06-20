package com.example.blindfoldchess



import android.graphics.Color

import android.os.Bundle

import android.view.inputmethod.InputMethodManager

import android.widget.Button

import android.widget.EditText

import android.widget.GridLayout

import android.widget.ImageView

import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.ui.NavigationUI

import com.example.blindfoldchess.Engine

import com.example.blindfoldchess.CoordinateTrainer

import com.example.blindfoldchess.R

import com.example.blindfoldchess.databinding.ActivityMainBinding

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen



class MainActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.gameSetupFragment -> {
                    navController.popBackStack(R.id.gameSetupFragment, false)
                    navController.navigate(R.id.gameSetupFragment)
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
            }
        }

    }

}