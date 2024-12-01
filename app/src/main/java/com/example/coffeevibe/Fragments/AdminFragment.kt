package com.example.coffeevibe.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.Activities.AddItemActivity
import com.example.coffeevibe.Activities.RemoveItemActivity
import com.example.coffeevibe.Adapters.UsersAdapter
import com.example.coffeevibe.AddItem
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.OrderAdapter
import com.example.coffeevibe.R
import com.example.coffeevibe.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AdminFragment : Fragment(R.layout.fragment_home) {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter
    private val bd = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRecyclerView = view.findViewById(R.id.rvUsers)
        userRecyclerView.layoutManager = LinearLayoutManager(context)

        val users = mutableListOf(
            User("Aoaoa", "Aoaoa@gmail.com"),
            User("Aoaoa", "Aoaoa@gmail.com"),
            User("Aoaoa", "Aoaoa@gmail.com"),
            User("Aoaoa", "Aoaoa@gmail.com"),
        )

        usersAdapter = UsersAdapter(users)
        userRecyclerView.adapter = usersAdapter

        val resultManager = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val user= result.data?.getParcelableExtra<AddItem>("result_item")
                user?. let {
                    addItemInDatabase(it)
                }
            }
        }

        val resultManager2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val user= result.data?.getParcelableExtra<AddItem>("result_delete")
                user?. let {
                    addItemInDatabase(it)
                }
            }
        }

        view.findViewById<Button>(R.id.addMenuBtn).setOnClickListener {
            val intent = Intent(requireContext(), AddItemActivity::class.java)
            resultManager.launch(intent)
        }

        view.findViewById<Button>(R.id.remMenuBtn).setOnClickListener {
            val intent = Intent(requireContext(), RemoveItemActivity::class.java)
            resultManager2.launch(intent)
        }

    }

    private fun removeItemInDatabase(itemNew: AddItem) {

    }

    private fun addItemInDatabase(itemNew: AddItem) {
        try {
            val item = hashMapOf(
                "name" to itemNew.name,
                "price" to "${itemNew.price} руб.",
                "category" to itemNew.category,
                "image" to "",
            )
            bd.collection("menu")
                .add(item)
                .addOnSuccessListener {
                    Log.d("AddItem", "Item added with ID: ${it.id}")
                }
                .addOnFailureListener {
                    Log.w("AddItem", "Error adding document", it)
                }
        } catch (e: Exception) {
            Log.d("AddItem", "Error: ${e.message}")
        }
    }
}