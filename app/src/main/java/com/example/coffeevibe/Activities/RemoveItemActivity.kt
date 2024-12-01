package com.example.coffeevibe.Activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.Adapters.RemoveItemAdapter
import com.example.coffeevibe.AddItem
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale.filter

class RemoveItemActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RemoveItemAdapter
    private lateinit var dataList: MutableList<AddItem>
    private lateinit var filteredList: MutableList<AddItem>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remove_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView= findViewById<RecyclerView>(R.id.rvItems)
        dataList = mutableListOf()
        filteredList = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RemoveItemAdapter(mutableListOf()){ item ->
            db.collection("menu").document(item.id).delete().addOnSuccessListener {
                Log.d("RemoveItemActivity", "DocumentSnapshot successfully deleted!")
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.w("RemoveItemActivity", "Error deleting document", it)
            }
        }
        recyclerView.adapter = adapter

        getMenuItemsFormDb(){
            adapter.updateFood(it)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return true
            }
        })

        saveBtn.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun filter(query: String) {
        filteredList.clear()
        dataList.forEach {
            if (it.name.contains(query, true)) {
                filteredList.add(it)
            }
        }
        adapter.updateFood(filteredList)
    }

    private fun getMenuItemsFormDb(callback: (MutableList<AddItem>) -> Unit) {
        val menuItems = mutableListOf<AddItem>()

        db.collection("menu").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.data["name"].toString()
                    val category = document.data["category"].toString()
                    val id = document.id
                    menuItems.add(AddItem(name, category, 0, id))
                    dataList.add(AddItem(name, category, 0, id))
                    filteredList.add(AddItem(name, category, 0, id))
                }
                Log.d("MenuFragment", "Success getting documents")
                callback(menuItems)
            }
            .addOnFailureListener {
                Log.d("MenuFragment", "Error getting documents: ", it)
            }
    }
}