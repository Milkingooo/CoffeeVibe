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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.coffeevibe.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
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
        setupFirestoreListener()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        findUserInFirestore(auth.currentUser?.email.toString()).toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)

        if (existingChannel == null) {
            createNotificationChannel()
        }

    }

    private fun findUserInFirestore(email: String) {
        db.collection("admin")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                val isAdmin = !documents.isEmpty // Если есть документы, пользователь - админ
                saveAdminStatus(isAdmin)
                updateNavUi(isAdmin)
            }
            .addOnFailureListener { exception ->
                Log.e("AccountFragment", "Error finding user in Firestore: ${exception.message}", exception)
            }
    }

    private fun saveAdminStatus(isAdmin: Boolean) {
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("isAdmin", isAdmin)
            .apply()
    }

    private fun updateNavUi(isAdmin: Boolean) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val menu = bottomNavigationView.menu

        if (isAdmin) {
            if (menu.findItem(R.id.adminFragment) == null) {
                menu.add(0, R.id.adminFragment, menu.size(), "Admin")
                    .setIcon(R.drawable.baseline_admin_panel_settings_24)
            }
        } else {
            menu.removeItem(R.id.adminFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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

    private fun setupFirestoreListener() {
        val db = Firebase.firestore

        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUser == null) {
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("order")
            .whereEqualTo("user_id", currentUser)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Ошибка получения изменений: ${e.message}")
                    return@addSnapshotListener
                }

                for (change in snapshots!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            //sendNotification("Добавлен новый заказ", "Заказ: ${change.document.getString("orderItems")}")
                        }

                        DocumentChange.Type.MODIFIED -> {
                            sendNotification(
                                "Обновление ззаказа",
                                "${change.document.getString("status")}"
                            )
                        }

                        DocumentChange.Type.REMOVED -> {
                            //sendNotification("Удален заказ", "Удаленный заказ: ${change.document.getString("orderItems")}")
                        }
                    }
                }
            }
    }

    private fun sendNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher) // Иконка уведомления
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this@MainActivity)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }
            }
        }
    }
}