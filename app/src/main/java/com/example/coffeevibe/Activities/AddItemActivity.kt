package com.example.coffeevibe.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeevibe.AddItem
import com.example.coffeevibe.R

class AddItemActivity : AppCompatActivity() {
    @SuppressLint("CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editTextName)
        val category = findViewById<EditText>(R.id.editTextCategory)
        val price = findViewById<EditText>(R.id.editTextPrice)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)
        val addImg = findViewById<Button>(R.id.imageBtn)
        val imageView = findViewById<ImageView>(R.id.imageView)
        var image: Uri? = null


        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                image = selectedImageUri
                imageView.setImageURI(selectedImageUri)
            }
        }

        addImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        saveBtn.setOnClickListener {
            val item =
                AddItem(
                    name = name.text.toString(),
                    category = category.text.toString(),
                    price = price.text.toString().toInt(),
                )
            val resIntent = Intent().apply {
                putExtra("result_item", item)
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