package com.example.coffeevibe.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.OrderAdapter
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore


class OrderFragment : Fragment(R.layout.fragment_order) {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private lateinit var orderVm: OrderViewModel
    private val CHANNEL_ID = "channel_id"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            orderVm = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        } catch (e: Exception) {
            Log.d("ViewModel", e.message.toString())
        }

        orderRecyclerView = view.findViewById(R.id.orderRecyclerView)
        orderRecyclerView.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrderAdapter(orderVm.orderItem.value ?: ArrayList()) { updatedItem ->
            orderVm.updateItem(updatedItem)
        }
        orderRecyclerView.adapter = orderAdapter


        val totalSumTv = view.findViewById<TextView>(R.id.textViewTotalSum)
        //totalSumTv.text = totalPrice(orderVm.orderItem.value ?: ArrayList<MenuItem>()).toString()

        orderVm.orderItem.observe(viewLifecycleOwner) {
            totalSumTv.text = "${orderVm.calculateTotal()} руб."
        }

        orderVm.orderItem.observe(viewLifecycleOwner) { updatedItems ->
            totalSumTv.text = "${orderVm.calculateTotal()} руб."
        }

        orderVm.onItemQuantityChange = { item ->
            orderVm.updateItem(item)
            totalSumTv.text = "${orderVm.calculateTotal()} руб."
        }

        view.findViewById<Button>(R.id.orderButton).setOnClickListener {
            //getNotify()
            createOrder()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createOrder() {
        if(auth.currentUser?.uid != null) {
            val repOrderItems = mutableListOf<String>()

            orderVm.orderItem.value?.forEach { item ->
                repeat(item.count) {
                    repOrderItems.add(item.name)
                }
            }
            addOrderInDatabase(repOrderItems)
            repOrderItems.clear()
        }else {
            Toast.makeText(context, "Вы не можете создать заказ, Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNotify(price: Int) {
        try {
            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CoffeeVibe")
                .setContentText(getString(R.string.order_create) + " $price руб.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(requireContext())) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@with
                }
                notify(123, builder.build())
            }
        } catch (e: Exception) {
            Log.d("Notification", e.message.toString())
        }
    }

    private fun addOrderInDatabase(items: MutableList<String>) {
        try {
            if(items.isEmpty()) {
                Toast.makeText(context, "Выберите позиции", Toast.LENGTH_SHORT).show()
                return
            }

            val totalPrice = orderVm.calculateTotal()
            val order = hashMapOf(
                "menu_items" to items,
                "order_date" to Timestamp.now(),
                "status" to "Создан",
                "total_price" to totalPrice,
                "user_id" to FirebaseAuth.getInstance().currentUser?.uid
            )

            db.collection("order")
                .add(order)
                .addOnSuccessListener {
                    Log.d("Order", "Order added with ID: ${it.id}")
                    getNotify(totalPrice)
                }
                .addOnFailureListener {
                    Log.w("Order", "Error adding document", it)
                }
        } catch (e: Exception) {
            Log.d("Order", "Error: ${e.message}")
        }
    }


}
