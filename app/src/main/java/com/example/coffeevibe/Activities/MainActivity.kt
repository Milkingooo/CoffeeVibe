package com.example.coffeevibe.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private val CHANNEL_ID = "channel_id"
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore


    @SuppressLint("MissingInflatedId")
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

        findUserInFirestore(auth.currentUser?.email.toString()).toString()

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
    private fun findUserInFirestore(email: String) {
        try {
            db.collection("admin")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val pass = document.getString("password").toString().trim()
                        if (auth.currentUser != null) {
                            onLoginAdmin(email = auth.currentUser!!.email.toString(), password = pass)
                        }
                        break
                    }
                }
                .addOnFailureListener {
                    Log.d("AccountFragment", "User is not found")
                }
        } catch (e: Exception) {
            Log.d("AccountFragment", "findUserInFirestore: $e")
        }

    }

    fun onLoginAdmin(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(email == "admin@bk.ru" && password == "adminadmin") {
            editor.putBoolean("isAdmin", true)
        }
        else {
            editor.putBoolean("isAdmin", false)
        }
        editor.apply()
        updateNavUi()
    }

    private fun updateNavUi(){
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val menu = bottomNavigationView.menu

        if(isAdmin) {
            if(menu.findItem(R.id.adminFragment) == null) {
                menu.add(0, R.id.adminFragment, menu.size(), "Admin")
                    .setIcon(R.drawable.baseline_admin_panel_settings_24)
            }
            else {
                menu.removeItem(R.id.adminFragment)
            }
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