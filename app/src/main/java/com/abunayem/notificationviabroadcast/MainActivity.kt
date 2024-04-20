package com.abunayem.notificationviabroadcast

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btnSendBroadcast)

        val nextButton = findViewById<Button>(R.id.btnLaunchNotificationActivity)
        nextButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            intent.putExtra("title", "title")
            intent.putExtra("content", "content")
            intent.putExtra("currentTime", "currentTime")
            intent.putExtra("fromWhere", "activity")
            startActivity(intent)
        }

        // enable receiver
        val receiver = ComponentName(this, MyBroadcastReceiver::class.java)
        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        button.setOnClickListener {
            if (hasNotificationPermission()) {
                openTimePickerDialog()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestForNotification()
                }
            }
        }
    }

    // permission request for notification
    private fun requestForNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }


    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        } else {
            true
        }
    }

    private fun openTimePickerDialog() {
        val myCalendar = Calendar.getInstance()
        val is24Hour = DateFormat.is24HourFormat(this)
        val clockFormat = if (is24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(myCalendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(myCalendar.get(Calendar.MINUTE))
            .setTitleText("টাইম সেট করুন")
            .build()
        // positive button click listener
        timePicker.addOnPositiveButtonClickListener {
            val hour24 = timePicker.hour // Get the hour in 24-hour format
            val hour12 = if (hour24 > 12) hour24 - 12 else hour24 // Convert to 12-hour format
            val minute = timePicker.minute // Get the minute
            val amPm = if (timePicker.hour > 11) "PM" else "AM" // Get the AM/PM value

            // Create a Calendar instance and set the selected time
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hour24
            calendar[Calendar.MINUTE] = minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            // Get the time in milliseconds
            val timeInMillis = calendar.timeInMillis

            // set selected time and send to alarm manager
            val intent = Intent(this, MyBroadcastReceiver::class.java)
            intent.putExtra("title", "title")
            intent.putExtra("content", "content")
            intent.putExtra("currentTime", "$hour12:$minute $amPm")
            val pendingIntent = PendingIntent.getBroadcast(this, requestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                alarmManager.canScheduleExactAlarms()
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
//            } else {
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
//            }
            scheduleExactAlarm(this, timeInMillis, pendingIntent)
        }

        timePicker.show(supportFragmentManager, "timePicker")
    }


    private fun scheduleExactAlarm(context: Context, alarmTime: Long, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Acquire a partial wakelock to ensure alarm delivery even during Doze
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:AlarmWakeLock")
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/) // Acquire before scheduling the alarm

        // Schedule the alarm using setExact()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }

        // Release the wakelock after scheduling
        wakeLock.release()
    }
}