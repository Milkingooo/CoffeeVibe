package com.example.coffeevibe.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeevibe.R
import com.example.coffeevibe.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AccountManageActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_manage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
    }

    private fun setListeners() {
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            saveChanges()
        }

        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun saveChanges() {
        val name = findViewById<EditText>(R.id.editTextNameAcc)
        val password = findViewById<EditText>(R.id.editTextPassword)

        if (password.text.length < 8 && password.text.toString() != ""){
            Toast.makeText(this, R.string.email_is_too_short_minimum_length_is_8, Toast.LENGTH_SHORT).show()
        }
        else {
            val user = User(
                name = name.text.toString().trim(),
                email = currentUser?.email.toString().trim(),
                password = password.text.toString().trim()
            )
            changeSettings(user)
            Toast.makeText(this, "Данные изменены!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun changeSettings(userNew: User)   {
        if (userNew.password.length < 8 && userNew.password != ""){
            Toast.makeText(this, R.string.email_is_too_short_minimum_length_is_8, Toast.LENGTH_SHORT).show()
        }
        db.collection("user").whereEqualTo("email", userNew.email).get()
            .addOnSuccessListener { user ->
                if (userNew.name != "") {
                    user.documents[0].reference.update("name", userNew.name)
                }
                if (userNew.password != "" && userNew.password.length >= 8) {
                    user.documents[0].reference.update("password", userNew.password)
                    auth.currentUser?.updatePassword(userNew.password)
                }
            }
            .addOnFailureListener() { exception ->
                Log.d("AccountManageActivity", exception.message.toString())
                //Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
                if (exception is NetworkOnMainThreadException){
                    Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            }
    }

}