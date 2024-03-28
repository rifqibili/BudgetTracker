package com.example.budgettracker.plans

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.budgettracker.MainActivity
import com.example.budgettracker.R
import com.example.budgettracker.ViewModel
import com.example.budgettracker.operations.OperationsData

class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationIntent  = Intent(context, MainActivity::class.java)
        val notificationCode = intent.getIntExtra("code", 0)
        notificationIntent.putExtra("fromNotification", true)
        notificationIntent.putExtra("code", notificationCode)

        val pendingIntent =
            PendingIntent.getActivity(context, notificationCode, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationContent = intent.getStringExtra("notificationContent")

        val notificationBuilder = NotificationCompat.Builder(context, "mama")
            .setSmallIcon(R.drawable.bank_icon)
            .setContentTitle("Time to perform the scheduled operation")
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val notificationId = System.currentTimeMillis().toInt()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder)
    }
}