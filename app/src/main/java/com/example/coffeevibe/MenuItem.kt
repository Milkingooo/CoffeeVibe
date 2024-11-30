package com.example.coffeevibe

data class MenuItem(
    val name: String,
    var price: String,
    val basePrice: Int,
    val image: String,
    var count: Int
)