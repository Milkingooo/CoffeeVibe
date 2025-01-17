package com.example.coffeevibe

import androidx.recyclerview.widget.DiffUtil

sealed class MenuItemUI {
    data class Header(val title: String) : MenuItemUI()
    data class Item(val name: String, val price: String, val imageUrl: String) : MenuItemUI()


}