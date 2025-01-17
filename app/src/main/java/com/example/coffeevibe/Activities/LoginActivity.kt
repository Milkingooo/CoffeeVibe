package com.example.coffeevibe.Activities

import android.content.Intent
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeevibe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        setupListeners()
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.textView5).setOnClickListener {
            navigateToRegistration()
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString().trim()

            if (validateInput(email, password)) {
                signIn(email, password)
            } else {
                Log.d("Login", "Validation failed")
            }
        }
        findViewById<TextView>(R.id.textView2).setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()

        if (email.isEmpty()) {
            showToast("Please enter your email")
            return
        } else {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast(getString(R.string.password_reset_email_sent))
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to send password reset email")
                    Log.e("FirebaseAuth", "Failed to send password reset email", exception)
                }
        }
    }

    fun validateInput(email: String, password: String): Boolean {
        return if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields")
            false
        } else {
            true
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Sign-in successful, user: ${auth.currentUser}")
                    navigateToMain()
                } else {
                    handleSignInError(task.exception)
                }
            }
            .addOnFailureListener(this) { exception ->
                if (exception is NetworkOnMainThreadException) {
                    showToast("Ошибка соединения")
                } else {
                    handleSignInError(exception)
                }
            }
    }

    fun handleSignInError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                Log.e("FirebaseAuth", "Invalid credentials: ${exception.message}")
                showToast("Неверная почта или пароль!")
            }

            is NetworkOnMainThreadException -> {
                showToast("Ошибка соединения")
            }

            else -> {
                Log.e("FirebaseAuth", "Sign-in failed", exception)
                showToast("Ошибка авторизации")
            }
        }
    }

    private fun navigateToRegistration() {
        startActivity(Intent(this, RegActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}