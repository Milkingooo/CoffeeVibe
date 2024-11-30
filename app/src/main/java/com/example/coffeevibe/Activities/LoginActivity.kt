package com.example.coffeevibe.Activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeevibe.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

//        if (auth.currentUser != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

        findViewById<TextView>(R.id.textView5).setOnClickListener {
            startActivity(Intent(this, RegActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmail)
            val password = findViewById<EditText>(R.id.editTextTextPassword)

            if (checkInputInfo(email.text.toString().trim(), password.text.toString().trim())) {
                signIn(email.text.toString().trim(), password.text.toString().trim())
            } else {
                Log.d("Login", "Login failed 2")
            }
        }
    }


    private fun checkInputInfo(phone: String, password: String) : Boolean {
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        else {
            return true
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Успешный вход
                    Log.d("FirebaseAuth", "Sign-in successful, user: ${auth.currentUser}")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // Ошибка входа
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.e("FirebaseAuth", "Invalid credentials: ${task.exception?.message}")
                    } else {
                        Log.e("FirebaseAuth", "Sign-in failed", task.exception)
                        Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}