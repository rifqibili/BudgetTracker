package com.example.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.budgettracker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val navigationBar : BottomNavigationView = binding.bottomNavigationView
        var navigationController = findNavController(R.id.fragmentContainerView)
        navigationBar.setupWithNavController(navigationController)
        navigationController.addOnDestinationChangedListener{_, destination, _ ->
            if (destination.id == R.id.operations) {
                navigationBar.visibility = View.VISIBLE
            }
        }


    }
}