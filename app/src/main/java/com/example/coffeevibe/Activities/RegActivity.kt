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
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegActivity : AppCompatActivity() {
    private val bd = Firebase.firestore
    private val auth = Firebase.auth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            findViewById<Button>(R.id.button2).setOnClickListener {
                val name = findViewById<EditText>(R.id.editTextText)
                val password = findViewById<EditText>(R.id.editTextTextPassword2)
                val email = findViewById<EditText>(R.id.editTextTextEmailAddress)

                lifecycleScope.launch {
                    if (checkForNull(
                            name.text.toString(),
                            password.text.toString(),
                            email.text.toString()
                        )
                        && checkForEmail(email.text.toString())
                    ) {
                        signUp(
                            email.text.toString(),
                            password.text.toString(),
                            name.text.toString()
                        )
                        Log.d("Register", "Success")
                    } else {
                        Log.d("Register", "Error")
                    }
                }

            }
        } catch (e: Exception) {
            Log.d("Register", "Error main: ${e.message}")
        }

        findViewById<TextView>(R.id.textView5).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkForNull(name: String, password: String, email: String): Boolean {
        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Log.d("Register", "Empty fields!")
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show()
            return false
        } else {
            Log.d("Register", "All fields filled!")
            return true
        }
    }

    private fun saveUserToDatabase(name: String, password: String, email: String) {
        try {
            val user = hashMapOf(
                "name" to name,
                "password" to password,
                "email" to email,
                "loyality_points" to 0
            )
            bd.collection("user")
                .add(user)
                .addOnSuccessListener {
                    Log.d("Register", "User added with ID: ${it.id}")
                }
                .addOnFailureListener {
                    Log.w("Register", "Error adding document", it)
                }
        } catch (e: Exception) {
            Log.d("Register", "Error: ${e.message}")
        }
    }

    private fun signUp(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToDatabase(name, password, email)
                    Log.d("Register", "createUserWithEmail:success")
                    Toast.makeText(this, "You are registered!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Log.d("Register", "createUserWithEmail:failure", it)
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun checkForEmail(email: String): Boolean {
        return try {
            val querySnapshot = bd.collection("user").whereEqualTo("email", email).get().await()
            if (querySnapshot.isEmpty) {
                true // email доступен
            } else {
                Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show()
                false // email уже существует
            }
        } catch (e: Exception) {
            Log.d("Register", "Error checking email")
            false // произошла ошибка
        }
    }
}