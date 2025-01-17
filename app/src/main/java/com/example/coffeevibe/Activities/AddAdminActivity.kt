package com.example.coffeevibe.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeevibe.AddItem
import com.example.coffeevibe.R
import com.example.coffeevibe.User

class AddAdminActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editTextName)
        val email = findViewById<EditText>(R.id.editTextEmail)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)


        saveBtn.setOnClickListener {
            val item =
                User(name = name.text.toString(), email = email.text.toString(), password = "password")
            val resIntent = Intent().apply {
                putExtra("result_admin", item)
            }
            setResult(RESULT_OK, resIntent)
            finish()
        }

        cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}