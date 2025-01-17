package com.example.coffeevibe.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.MenuItemUI
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R

class MenuAdapter(private val menuItems: MutableList<MenuItemUI>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    private lateinit var orderViewModel: OrderViewModel

    fun setOrderViewModel(orderViewModel: OrderViewModel) {
        this.orderViewModel = orderViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view)
            }

            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.food_item_layout, parent, false)
                ItemViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val header = menuItems[position] as MenuItemUI.Header
                holder.title.text = header.title
            }

            is ItemViewHolder -> {
                val item = menuItems[position] as MenuItemUI.Item
                holder.itemName.text = item.name
                holder.itemPrice.text = item.price + " руб."

                Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.itemImage)

                val menuItem = MenuItem(
                    item.name,
                    item.price,
                    item.price.toInt(),
                    item.imageUrl,
                    1
                )
                if (orderViewModel.inList(menuItem)) {
                    holder.addBtn.setImageResource(R.drawable.baseline_delete_24)  // Картинка удаления
                }
                else {
                    holder.addBtn.setImageResource(R.drawable.baseline_add_24)  // Картинка добавления
                }
                holder.addBtn.setOnClickListener {

                    val menuItem = MenuItem(
                        item.name,
                        item.price,
                        item.price.toInt(),
                        item.imageUrl,
                        1
                    ) // Преобразуем в MenuItem

                    if (orderViewModel.inList(menuItem)) {
                        orderViewModel.deleteFromOrder(menuItem)
                        holder.addBtn.setImageResource(R.drawable.baseline_add_24)  // Картинка добавления
                    } else {
                        orderViewModel.addToOrder(menuItem)
                        Log.d("TAGGGG", "addToOrder : ${menuItem.name}")
                        holder.addBtn.setImageResource(R.drawable.baseline_delete_24)  // Картинка удаления
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = menuItems.size

    override fun getItemViewType(position: Int): Int {
        return when (menuItems[position]) {
            is MenuItemUI.Header -> VIEW_TYPE_HEADER
            is MenuItemUI.Item -> VIEW_TYPE_ITEM
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.headerTitle)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.foodName)
        val itemPrice: TextView = itemView.findViewById(R.id.foodPrice)
        val itemImage: ImageView = itemView.findViewById(R.id.foodImg)

        val addBtn: ImageButton = itemView.findViewById(R.id.imageButtonAddRem)
    }

    // Метод для обновления данных с использованием DiffUtil
    fun submitList(newList: List<MenuItemUI>) {
        val diffCallback = MenuItemDiffCallback(menuItems, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        menuItems.clear()
        menuItems.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil.Callback для сравнения данных
    class MenuItemDiffCallback(
        private val oldList: List<MenuItemUI>,
        private val newList: List<MenuItemUI>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is MenuItemUI.Header && newItem is MenuItemUI.Header -> oldItem.title == newItem.title
                oldItem is MenuItemUI.Item && newItem is MenuItemUI.Item -> oldItem.name == newItem.name
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }
}

