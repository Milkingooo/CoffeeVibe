package com.example.coffeevibe.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate
import com.example.coffeevibe.Activities.AboutUsActivity
import com.example.coffeevibe.Activities.AccountManageActivity
import com.example.coffeevibe.Activities.GetHelpActivity
import com.example.coffeevibe.Activities.LoginActivity
import com.example.coffeevibe.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AccountFragment : Fragment(R.layout.fragment_account) {
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val aboutUsButton = view.findViewById<Button>(R.id.aboutButton)
        val getHelpButton = view.findViewById<Button>(R.id.getHelpButton)
        val accManageButton = view.findViewById<Button>(R.id.accManageButton)
        val themeSwitcher = view.findViewById<SwitchMaterial>(R.id.themeSwitcher)

        if (currentUser == null) {
            loginButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            accManageButton.visibility = View.GONE
        }
        else {
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            findUserInFirestore(currentUser?.email.toString())
            accManageButton.visibility = View.VISIBLE
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        loginButton.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        aboutUsButton.setOnClickListener{
            val intent = Intent(requireContext(), AboutUsActivity::class.java)
            startActivity(intent)
        }

        getHelpButton.setOnClickListener{
            val intent = Intent(requireContext(), GetHelpActivity::class.java)
            startActivity(intent)
        }

        accManageButton.setOnClickListener{
            startActivity(Intent(requireContext(), AccountManageActivity::class.java))
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun findUserInFirestore(email: String) {
        val currentUserView = view?.findViewById<TextView>(R.id.textViewName)
        try {
            Log.d("AccountFragment", "findUserInFirestore: $email")
            db.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        currentUserView?.text = document.getString("name").toString()
                        Log.d("AccountFragment", "User is find: ${currentUserView?.text}")
                        break
                    }
                }
                .addOnFailureListener {
                    Log.d("AccountFragment", "User is not found")
                    currentUserView?.text = "Володя"
                }
        }catch (e: Exception) {
            Log.d("AccountFragment", "findUserInFirestore: $e")
        }
    }
}