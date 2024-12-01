package com.example.coffeevibe.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.Adapters.UsersAdapter
import com.example.coffeevibe.OrderAdapter
import com.example.coffeevibe.R
import com.example.coffeevibe.User

class AdminFragment : Fragment(R.layout.fragment_home) {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter

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


    }
}