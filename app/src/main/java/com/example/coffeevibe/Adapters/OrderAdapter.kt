package com.example.coffeevibe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R

class OrderAdapter(private val items: MutableList<MenuItem>, val onItemQuantityChange: (MenuItem) -> Unit) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]

        holder.orderItemName.text = item.name.toString()
        holder.orderItemPrice.text = "${item.count * extractPrice(item.basePrice.toString())} руб."
        holder.orderCount.text = item.count.toString()

        Glide
            .with(holder.orderItemImage.context)
            .load(item.image)
            .into(holder.orderItemImage)

        holder.plus.setOnClickListener{
            if(item.count < 10){
                item.count++
                item.price = (item.count * extractPrice(item.basePrice.toString())).toString()
                holder.orderCount.text = item.count.toString()
                holder.orderItemPrice.text = "${item.price} руб."
                holder.orderCount.text = item.count.toString()
                onItemQuantityChange(item)
            }
        }

        holder.minus.setOnClickListener{
            if(item.count > 1){
                item.count--
                item.price = (item.count * extractPrice(item.basePrice.toString())).toString()
                holder.orderCount.text = item.count.toString()
                holder.orderItemPrice.text = "${item.price} руб."
                holder.orderCount.text = item.count.toString()
                onItemQuantityChange(item)
            }
            else{
                holder.bindItem()
                onItemQuantityChange(item)
            }
        }

        holder.deleteButton.setOnClickListener{
            holder.bindItem()
        }
    }

    override fun getItemCount(): Int = items.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderItemName = itemView.findViewById<TextView>(R.id.orderItemName)
        val orderItemPrice = itemView.findViewById<TextView>(R.id.orderItemPrice)
        val orderItemImage = itemView.findViewById<ImageView>(R.id.orderItemImg)
        val orderCount = itemView.findViewById<TextView>(R.id.textView10)

        val plus = itemView.findViewById<ImageButton>(R.id.imageButtonPlus)
        val minus = itemView.findViewById<ImageButton>(R.id.imageButtonMinus)

        val deleteButton = itemView.findViewById<ImageButton>(R.id.imageButton3)

        fun bindItem(){
            if (adapterPosition != RecyclerView.NO_POSITION){
                deleteItem(adapterPosition)
            }
        }
    }

    fun deleteItem(position: Int){
        if (position in 0..items.size){
            items.removeAt(position)
            notifyDataSetChanged()
            notifyItemRangeChanged(position, items.size)
        }
    }

    fun updateList(itemNew: MutableList<MenuItem>) {
        this.items.clear()
        this.items.addAll(itemNew)
        notifyDataSetChanged()
    }

    private fun extractPrice(priceStr: String) : Int{
        val regex = "\\d+".toRegex()

        val matchResult = regex.find(priceStr)
        return matchResult?.value?.toInt() ?: 0
    }

}