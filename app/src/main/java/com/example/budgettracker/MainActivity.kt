package com.example.budgettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.budgettracker.databinding.ActivityMainBinding
import com.example.budgettracker.operations.OperationsData
import com.example.budgettracker.plans.PlannedOperation
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var operationAddedFromNotification = false
    private lateinit var plannedOperation : PlannedOperation
    private val NOTIFICATION_CHANNEL_ID = "my_channel_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }

        val fromNotification = intent.getBooleanExtra("fromNotification", false)
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        if (fromNotification) {
            viewModel.allPlannedOperations.observe(this) { plannedOperations ->
                plannedOperations?.let {
                    if (it.isNotEmpty() && !operationAddedFromNotification) {
                        val notificationCode = intent.getIntExtra("code", 0)
                        for (element in it) {
                            if (element.code == notificationCode) {
                                plannedOperation = element
                            }
                        }
                        val operationToAdd = OperationsData(
                            plannedOperation.id, plannedOperation.amount, plannedOperation.icon,
                            plannedOperation.category, plannedOperation.type, plannedOperation.date,
                            plannedOperation.account, "", false, 0, plannedOperation.note
                        )
                        viewModel.addOperation(operationToAdd)
                        viewModel.deletePlannedOperation(plannedOperation)
                        operationAddedFromNotification = true
                    }
                }
            }
        }



        val navigationBar : BottomNavigationView = binding.bottomNavigationView
        var navigationController = findNavController(R.id.fragmentContainerView)
        navigationBar.setupWithNavController(navigationController)
        navigationController.addOnDestinationChangedListener{_, destination, _ ->
            if (destination.id == R.id.operations) {
                navigationBar.visibility = View.VISIBLE
            }
        }
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
    private fun requestNotificationPermission() {
        NotificationManagerCompat.from(this).apply {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }
    }
}