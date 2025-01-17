package com.example.coffeevibe.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.R
import com.example.coffeevibe.User

class UsersAdapter(private val users: MutableList<User>): RecyclerView.Adapter<UsersAdapter.UserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.UserViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: UsersAdapter.UserViewHolder, position: Int) {
        val user = users[position]

        holder.name.text = user.name
        holder.email.text = user.email

        holder.delete.setOnClickListener {
            //Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.textViewName)
        val email = itemView.findViewById<TextView>(R.id.textViewEmail)

        val delete = itemView.findViewById<ImageButton>(R.id.delBtn)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(users: MutableList<User>){
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }
}