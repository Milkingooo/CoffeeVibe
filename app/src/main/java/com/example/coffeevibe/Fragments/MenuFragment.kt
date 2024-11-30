package com.example.coffeevibe.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.CoffeeAdapter
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R
import com.google.firebase.firestore.FirebaseFirestore


class MenuFragment : Fragment(R.layout.fragment_menu) {
    private var bd = FirebaseFirestore.getInstance()

    private lateinit var coffeeRecyclerView: RecyclerView
    private lateinit var dessertRecyclerView: RecyclerView
    private lateinit var teaRecyclerView: RecyclerView
    private lateinit var sandwichRecyclerView: RecyclerView

    private lateinit var coffeeAdapter: CoffeeAdapter
    private lateinit var teaAdapter: CoffeeAdapter
    private lateinit var dessertAdapter: CoffeeAdapter
    private lateinit var sandwichAdapter: CoffeeAdapter

    private lateinit var orderVm : OrderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            orderVm = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        }
        catch (e: Exception){
            Log.d("ViewModel", e.message.toString())
        }

        coffeeRecyclerView = view.findViewById(R.id.coffeeRecyclerView)
        teaRecyclerView = view.findViewById(R.id.teaRecyclerView)
        dessertRecyclerView = view.findViewById(R.id.dessertRecyclerView)
        sandwichRecyclerView = view.findViewById(R.id.sandwichRecyclerView)

        coffeeRecyclerView.layoutManager = GridLayoutManager(context, 2)
        teaRecyclerView.layoutManager = GridLayoutManager(context, 2)
        dessertRecyclerView.layoutManager = GridLayoutManager(context, 2)
        sandwichRecyclerView.layoutManager = GridLayoutManager(context, 2)


        teaAdapter = CoffeeAdapter(mutableListOf())
        teaAdapter.setOrderVm(orderVm)
        coffeeAdapter = CoffeeAdapter(mutableListOf())
        coffeeAdapter.setOrderVm(orderVm)
        dessertAdapter = CoffeeAdapter(mutableListOf())
        dessertAdapter.setOrderVm(orderVm)
        sandwichAdapter = CoffeeAdapter(mutableListOf())
        sandwichAdapter.setOrderVm(orderVm)

        teaRecyclerView.adapter = teaAdapter
        coffeeRecyclerView.adapter = coffeeAdapter
        dessertRecyclerView.adapter = dessertAdapter
        sandwichRecyclerView.adapter = sandwichAdapter

        getMenuItemsFormDb("Кофе") {
            coffeeAdapter.updateFood(it)
        }
        getMenuItemsFormDb("Чай") {
            teaAdapter.updateFood(it)
        }
        getMenuItemsFormDb("Десерты") {
            dessertAdapter.updateFood(it)
        }
        getMenuItemsFormDb("Сэндвичи") {
            sandwichAdapter.updateFood(it)
        }

    }

    private fun getMenuItemsFormDb(category: String, callback: (MutableList<MenuItem>) -> Unit) {
        val menuItems = mutableListOf<MenuItem>()

        bd.collection("menu").whereEqualTo("category", category).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.data["name"].toString()
                    val price = document.data["price"].toString()
                    var imgUrl = document.data["image"].toString().trim()
                    imgUrl = transformGoogleDriveLink(imgUrl)

                    menuItems.add(MenuItem(name, price, extractPrice(price) , imgUrl, 1))
                }
                Log.d("MenuFragment", "Success getting documents")
                callback(menuItems)
            }
            .addOnFailureListener {
                Log.d("MenuFragment", "Error getting documents: ", it)
            }
    }

    private fun transformGoogleDriveLink(link: String): String {
        val fileId = link.substringAfter("/d/").substringBefore("/view")
        return "https://drive.google.com/uc?id=$fileId"
    }

    fun extractPrice(priceStr: String) : Int{
        val regex = "\\d+".toRegex()

        val matchResult = regex.find(priceStr)
        return matchResult?.value?.toInt() ?: 0
    }
}

