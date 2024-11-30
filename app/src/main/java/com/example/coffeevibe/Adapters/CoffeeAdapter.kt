package com.example.coffeevibe

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R

class CoffeeAdapter(private val food: MutableList<MenuItem>): RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>(){

    private lateinit var orderVm : OrderViewModel

    fun setOrderVm(videoModel: OrderViewModel){
        orderVm = videoModel
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoffeeViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item_layout, parent, false)
        return CoffeeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val menuItem = food[position]
        holder.itemName.text = menuItem.name
        holder.itemPrice.text = menuItem.price

        Glide.with(holder.itemView.context)
            .load(menuItem.image)
            .into(holder.itemImage)

        holder.addBtn.setOnClickListener{
            if (orderVm.inList(menuItem)){
                orderVm.deleteFromOrder(menuItem)
                holder.addBtn.setImageResource(R.drawable.baseline_add_24)
            } else{
                orderVm.addToOrder(menuItem)
                holder.addBtn.setImageResource(R.drawable.baseline_delete_24)
            }
        }
    }

    override fun getItemCount(): Int = food.size

    inner class CoffeeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemName = itemView.findViewById<TextView>(R.id.foodName)
        val itemPrice = itemView.findViewById<TextView>(R.id.foodPrice)
        val itemImage = itemView.findViewById<ImageView>(R.id.foodImg)

        val addBtn = itemView.findViewById<ImageButton>(R.id.imageButtonAddRem)
    }

    fun updateFood(food: MutableList<MenuItem>) {
        this.food.clear()
        this.food.addAll(food)
        notifyDataSetChanged()
    }

}