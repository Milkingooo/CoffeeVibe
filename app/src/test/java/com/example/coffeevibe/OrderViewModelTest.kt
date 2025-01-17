package com.example.coffeevibe

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class OrderViewModelTest {
    private lateinit var orderViewModel: OrderViewModel

    @Before
    fun setUp() {
        orderViewModel = OrderViewModel()
        orderViewModel.addToOrder(MenuItem(name = "Coffee", price = "150.00", 150, "", 1))
        orderViewModel.addToOrder(MenuItem(name = "Milk", price = "100.00", 100, "", 2))
    }

    @Test
    fun inList_validData() {
        assertTrue(orderViewModel.inList(MenuItem(name = "Coffee", price = "150.00", 150, "", 1)))
    }

    @Test
    fun inList_invalidData() {
        assertFalse(orderViewModel.inList(MenuItem(name = "Tea", price = "100.00", 100, "", 2)))
    }

    @Test
    fun calculateTotal_validData() {
        assertEquals(orderViewModel.calculateTotal() , 350)
    }

    @Test
    fun calculateTotal_invalidData() {
        assertFalse(orderViewModel.calculateTotal() == 251)
    }

    @Test
    fun calculateTotal_noData() {
        orderViewModel.orderItem.value?.clear()
        assertTrue(orderViewModel.calculateTotal() == 0)
    }

    @Test
    fun addToOrder_validData() {
        orderViewModel.addToOrder(MenuItem(name = "Tea", price = "100.00", 100, "", 2))
        assertEquals(orderViewModel.orderItem.value?.size , 3)
    }

    @Test
    fun addToOrder_invalidData() {
        orderViewModel.addToOrder(MenuItem(name = "Tea", price = "100.00", 100, "", 2))
        assertFalse(orderViewModel.orderItem.value?.size == 1)
    }


    @Test
    fun removeFromOrder_validData() {
        orderViewModel.deleteFromOrder(MenuItem(name = "Coffee", price = "150.00", 150, "", 1))
        assertEquals(orderViewModel.orderItem.value?.size , 1)
    }

    @Test
    fun removeFromOrder_invalidData() {
        orderViewModel.deleteFromOrder(MenuItem(name = "Tea", price = "100.00", 100, "", 2))
        assertFalse(orderViewModel.orderItem.value?.size == 1)
    }

    @Test
    fun updateOrder_validData() {
        orderViewModel.updateItem(MenuItem(name = "Coffee", price = "150.00", 150, "", 1))
        assertEquals(orderViewModel.orderItem.value?.size , 2)
    }
    @Test
    fun updateOrder_invalidData() {
        orderViewModel.updateItem(MenuItem(name = "Tea", price = "100.00", 100, "", 2))
        assertFalse(orderViewModel.orderItem.value?.size == 1)
    }
}