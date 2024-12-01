package com.example.coffeevibe.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeevibe.AddItem
import com.example.coffeevibe.Category
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.R

class RemoveItemAdapter(private val items: MutableList<AddItem>, private val onCategorySelected: (AddItem) -> Unit) : RecyclerView.Adapter<RemoveItemAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RemoveItemAdapter.ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.remove_item, parent, false)
        return ItemViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RemoveItemAdapter.ItemViewHolder, position: Int) {
        val item = items[position]

        holder.nameItem.text = item.name
        holder.categoryItem.text = item.category

        holder.itemView.setOnClickListener {
            onCategorySelected(item)
            holder.bind()
        }

    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameItem = itemView.findViewById<TextView>(R.id.foodName)
        val categoryItem= itemView.findViewById<TextView>(R.id.foodCategory)
        val delBtn = itemView.findViewById<ImageButton>(R.id.imageButtonRem)

        fun bind() {
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

    fun updateFood(food: MutableList<AddItem>) {
        this.items.clear()
        this.items.addAll(food)
        notifyDataSetChanged()
    }


}