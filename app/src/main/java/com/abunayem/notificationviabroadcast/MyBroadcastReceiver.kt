package com.abunayem.notificationviabroadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder

class MyBroadcastReceiver : BroadcastReceiver() {

    private val channelId = "channel_id_example_01"
    private val channelName = "channel_name_example_01"

    override fun onReceive(context: Context?, intent: Intent?) {
        val currentDateTimeString = intent?.getStringExtra("currentTime")
        Toast.makeText(context, "Alarm time $currentDateTimeString", Toast.LENGTH_LONG).show()
        val title = intent?.getStringExtra("title")
        val content = intent?.getStringExtra("content")


        // Create an Intent for the activity you want to start.
        val resultIntent = Intent(context, NotificationActivity::class.java)
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("content", content)
        resultIntent.putExtra("currentTime", currentDateTimeString)
        resultIntent.putExtra("fromWhere", "notification")
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // Create the TaskStackBuilder.
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context!!.applicationContext).run {
            // Add the intent, which inflates the back stack.
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack.
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }


        showNotification(context, title!!, content!!, resultPendingIntent!!)
    }

    private fun showNotification(context: Context, title: String, content: String, pendingIntent: PendingIntent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setStyle(NotificationCompat.BigTextStyle().bigText(content))
                }
            }
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setWhen(System.currentTimeMillis())
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify((Math.random() * 10000).toInt(), notificationBuilder.build())
    }
}