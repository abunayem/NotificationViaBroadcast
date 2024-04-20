package com.abunayem.notificationviabroadcast

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.abunayem.notificationviabroadcast.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private var binding: ActivityNotificationBinding? = null
    var title: String? = null
    var content: String? = null
    var fromWhere: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Register the callback
//        backPressedCallback.isEnabled = true
//        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        val toolbar: Toolbar = binding!!.includedToolbarLayout.toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)

        fromWhere = intent.getStringExtra("fromWhere")
        if (fromWhere == "notification") {
            title = intent.getStringExtra("title")
            content = intent.getStringExtra("content")
            savedInstanceState?.let {
                title = it.getString("title")
                content = it.getString("content")
            }
        } else {
            title = sharedPreferences.getString("title", "title")
            content = sharedPreferences.getString("content", "content")
        }

        savedInstanceState?.let {
            title = it.getString("title")
            content = it.getString("content")
        }

        val currentTime = intent.getStringExtra("currentTime")

        val text = "Title: $title\nContent: $content\nCurrent Time: $currentTime"
        Log.w("NotificationActivity", text)
        val textView = findViewById<TextView>(R.id.tvNotification)
        textView.text = currentTime
    }

    //    private var backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
//        override fun handleOnBackPressed() {
//            // Handle the back button event
//            if (fromWhere == "notification") {
//                startActivity(Intent(this@NotificationDetailsActivity, MainActivity::class.java))
//                finish()
//            } else {
//                finish()
//            }
//        }
//    }

    private fun saveDataToSharedPreference() {
        val sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("title", "title")
        editor.putString("content", "content")
        editor.apply()
    }

    private fun getDataFromSharedPreference() {
        val sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val title = sharedPreferences.getString("title", "title")
        val content = sharedPreferences.getString("content", "content")
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    //
//    override fun onDestroy() {
//        // Unregister the callback by disabling it
//        backPressedCallback.isEnabled = false
//        super.onDestroy()
//    }
}