package com.example.coffeevibe.Fragments

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.OrderAdapter
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R


class OrderFragment : Fragment(R.layout.fragment_order) {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    private lateinit var orderVm : OrderViewModel
    private val CHANNEL_ID = "channel_id"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            orderVm = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        }
        catch (e: Exception){
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
            getNotify()
        }
    }

    fun getNotify(){
        try {
            var builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CoffeeVibe")
                .setContentText("Order is placed, please wait!")
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
        }
        catch (e: Exception) {
            Log.d("Notification", e.message.toString())
        }
    }

}