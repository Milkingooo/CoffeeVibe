package com.example.coffeevibe.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.coffeevibe.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        enableEdgeToEdge()
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        setupListeners()
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.button2).setOnClickListener {
            handleRegister()
        }

        findViewById<TextView>(R.id.textView5).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegister() {
        val name = findViewById<EditText>(R.id.editTextText).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassword2).text.toString()
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()

        lifecycleScope.launch {
            if (validateInput(name, password, email) && isEmailAvailable(email)) {
                signUp(email, password, name)
            } else {
                Log.d("Register", "Validation failed or email exists")
            }
        }
    }

    fun validateInput(name: String, password: String, email: String): Boolean {
        if (name.isBlank() || password.isBlank() || email.isBlank()) {
            showToast("Fill all fields!")
            return false
        }
        if(password.length < 8){
            showToast(getString(R.string.email_is_too_short_minimum_length_is_8))
            return false
        }
        else {
            return true
        }
    }

    private fun signUp(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToDatabase(name, email, password)
                    navigateToMain()
                } else {
                    showToast(getString(R.string.authentication_failed))
                }
            }
            .addOnFailureListener {
                Log.e("Register", "Error during registration", it)
            }
    }

    private fun saveUserToDatabase(name: String, email: String, password: String) {
        val user = mapOf(
            "id" to (auth.currentUser?.uid ?: ""),
            "name" to name,
            "password" to password,
            "email" to email,
            "loyalty_points" to 0
        )
        db.collection("user").add(user)
            .addOnSuccessListener { Log.d("Register", "User added with ID: ${it.id}") }
            .addOnFailureListener { Log.e("Register", "Error adding user", it)  }
    }

    private fun navigateToMain() {
        showToast(getString(R.string.you_are_registered))
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private suspend fun isEmailAvailable(email: String): Boolean {
        return try {
            val querySnapshot = db.collection("user").whereEqualTo("email", email).get().await()
            if (querySnapshot.isEmpty) true else {
                showToast(getString(R.string.email_already_exists))
                false
            }
        } catch (e: Exception) {
            Log.e("Register", "Error checking email availability", e)
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}