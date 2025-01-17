package com.example.coffeevibe.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.Activities.AddAdminActivity
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
        usersAdapter = UsersAdapter(mutableListOf())
        userRecyclerView.adapter = usersAdapter

        getAdmins {
            usersAdapter.updateList(it)
        }
        val resultManager =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val user = result.data?.getParcelableExtra<AddItem>("result_item")
                    user?.let {
                        addItemInDatabase(it)
                    }
                }
            }

        val resultManager2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val user = result.data?.getParcelableExtra<AddItem>("result_delete")
                    user?.let {
                        addItemInDatabase(it)
                    }
                }
            }

        val resultManager3 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val user = result.data?.getParcelableExtra<User>("result_admin")
                    user?.let {
                        addAdmin(it)
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

        view.findViewById<ImageButton>(R.id.addUser).setOnClickListener {
            val intent = Intent(requireContext(), AddAdminActivity::class.java)
            resultManager3.launch(intent)
        }

    }

    private fun addAdmin(user: User) {
        try {
            val admin = hashMapOf(
                "email" to user.email,
                "name" to user.name,
            )
            bd.collection("admin")
                .add(admin)
                .addOnSuccessListener {
                    Log.d("AddAdmin", "Admin added with ID: ${it.id}")
                    Toast.makeText(requireContext(), "Администратор добавлен!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.w("AddAdmin", "Error adding document", it)
                    Toast.makeText(requireContext(), "Администратор не добавлен!", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.d("AddAdmin", "Error: ${e.message}")
        }
    }

    private fun getAdmins(callback: (MutableList<User>) -> Unit) {
        val admins = mutableListOf<User>()

        try {
            bd.collection("admin").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val email = document.data["email"].toString()
                        val name = document.data["name"].toString()
                        admins.add(User(name, email, "admin"))
                    }
                    callback(admins)
                }
                .addOnFailureListener {
                    Log.w("GetAdmins", "Error getting documents.", it)
                }
        } catch (e: Exception) {
            Log.d("GetAdmins", "Error: ${e.message}")
        }
    }

    private fun addItemInDatabase(itemNew: AddItem)     {
        try {
            val item = hashMapOf(
                "name" to itemNew.name,
                "price" to "${itemNew.price} руб.",
                "category" to itemNew.category,
                "image" to ""
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