package com.example.coffeevibe

import android.view.Menu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {
    var onItemQuantityChange: (MenuItem) -> Unit = {}

    val orderItem = MutableLiveData<MutableList<MenuItem>>()
    val buttonStates = ArrayList<Boolean>()

    fun getButtonStates() : List<Boolean>{
        return buttonStates
    }

    fun addToOrder(item: MenuItem){
        val currentOrderItem = orderItem.value ?: ArrayList<MenuItem>()
        currentOrderItem.add(item)
        orderItem.value = currentOrderItem
        buttonStates.add(true)
    }

    fun inList(item: MenuItem) : Boolean{
        val currentItem = orderItem.value ?: ArrayList<MenuItem>()
        return currentItem.contains(item)
    }

    fun deleteFromOrder(item: MenuItem){
        val currItem = orderItem.value ?: ArrayList<MenuItem>()
        val index = currItem.indexOf(item)
        if (index != -1){
            currItem.removeAt(index)
            orderItem.value = currItem
            buttonStates.removeAt(index)
        }
    }

    fun calculateTotal() : Int{
        var totalSum = 0

        orderItem.value?.forEach { item ->
            totalSum += item.basePrice * item.count
        }
        return totalSum
    }

    fun extractPrice(priceStr: String) : Int{
        val regex = "\\d+".toRegex()

        val matchResult = regex.find(priceStr)
        return matchResult?.value?.toInt() ?: 0
    }

    fun updateItem(item: MenuItem){
        val currList = orderItem.value ?: return
        val index = currList.indexOfFirst { it.name == item.name }

        if(index != -1){
            currList[index] = item
            orderItem.value = currList.toMutableList()
        }
    }



}