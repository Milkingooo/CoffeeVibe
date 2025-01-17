package com.example.coffeevibe.Activities

import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class RegActivityTest{
    private lateinit var regActivity: RegActivity
    private val mockToast: Toast = mockk()

    @Before
    fun setUp() {
        regActivity = RegActivity()
        every{
            mockToast.show()
        } returns Unit
    }

    @Test
    fun validateInput_validData_returnsTrue() {
        val name = "John"
        val password = "password123"
        val email = "john@example.com"
        val result = regActivity.validateInput(name, password, email)
        assertTrue(result)
    }

    @Test
    fun validateInput_emptyName_returnsFalse() {
        val result = regActivity.validateInput("", "password123", "john@example.com")
        assertFalse(result)
    }

    @Test
    fun validateInput_emptyPassword_returnsFalse() {
        val result = regActivity.validateInput("John", "", "john@example.com")
        assertFalse(result)
    }

    @Test
    fun validateInput_emptyEmail_returnsFalse() {
        val result = regActivity.validateInput("John", "password123", "")
        assertFalse(result)
    }
}