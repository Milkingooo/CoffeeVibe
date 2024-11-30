package com.example.coffeevibe.Activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private val CHANNEL_ID = "channel_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)

        if (existingChannel == null) {
            createNotificationChannel()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun tvClick(view: View) {
        when (view.id) {
            R.id.tvCoffee -> {
                findViewById<TextView>(R.id.titleCoffee).visibility = View.VISIBLE
                findViewById<TextView>(R.id.titleTea).visibility = View.GONE
                findViewById<TextView>(R.id.titleDesserts).visibility = View.GONE
                findViewById<TextView>(R.id.titleSandwiches).visibility = View.GONE

                findViewById<RecyclerView>(R.id.coffeeRecyclerView).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.teaRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.dessertRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.sandwichRecyclerView).visibility = View.GONE
            }
            R.id.tvTea -> {
                findViewById<TextView>(R.id.titleTea).visibility = View.VISIBLE
                findViewById<TextView>(R.id.titleCoffee).visibility = View.GONE
                findViewById<TextView>(R.id.titleDesserts).visibility = View.GONE
                findViewById<TextView>(R.id.titleSandwiches).visibility = View.GONE

                findViewById<RecyclerView>(R.id.teaRecyclerView).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.coffeeRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.dessertRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.sandwichRecyclerView).visibility = View.GONE
            }
            R.id.tvDesserts -> {
                findViewById<TextView>(R.id.titleDesserts).visibility = View.VISIBLE
                findViewById<TextView>(R.id.titleCoffee).visibility = View.GONE
                findViewById<TextView>(R.id.titleTea).visibility = View.GONE
                findViewById<TextView>(R.id.titleSandwiches).visibility = View.GONE

                findViewById<RecyclerView>(R.id.dessertRecyclerView).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.teaRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.sandwichRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.coffeeRecyclerView).visibility = View.GONE
            }
            R.id.tvSandwiches ->{
                findViewById<TextView>(R.id.titleSandwiches).visibility = View.VISIBLE
                findViewById<TextView>(R.id.titleCoffee).visibility = View.GONE
                findViewById<TextView>(R.id.titleTea).visibility = View.GONE
                findViewById<TextView>(R.id.titleDesserts).visibility = View.GONE

                findViewById<RecyclerView>(R.id.sandwichRecyclerView).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.teaRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.dessertRecyclerView).visibility = View.GONE
                findViewById<RecyclerView>(R.id.coffeeRecyclerView).visibility = View.GONE
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name notification"
            val descriptionText = "some description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}