package com.example.coffeevibe.Fragments

import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class MenuFragmentTest {
    private lateinit var menuFragment: MenuFragment
    private val mockToast: Toast = mockk()

    @Before
    fun setUp() {
        menuFragment = MenuFragment()
        every {
            mockToast.show()
        } returns Unit
    }

    @Test
    fun extractPrice_validData() {
        val price = menuFragment.extractPrice("1000")
        assertEquals(1000, price)
    }

    @Test
    fun extractPrice_invalidData() {
        val price = menuFragment.extractPrice("abc")
        assertEquals(0, price)
    }

    @Test
    fun extractPrice_charData() {
        val price = menuFragment.extractPrice("#$%")
        assertEquals(0, price)
    }

    @Test
    fun extractPrice_emptyData() {
        val price = menuFragment.extractPrice("")
        assertEquals(0, price)
    }

    @Test
    fun transformGoogleLink_validData() {
        val link =
            menuFragment.transformGoogleDriveLink("https://drive.google.com/file/d/1EMoIbb4OtRbR-ZmTuE3-zUqpd2Iq9_yJ/view?usp=drive_link")
        assertEquals("https://drive.google.com/uc?id=1EMoIbb4OtRbR-ZmTuE3-zUqpd2Iq9_yJ", link)
    }

    @Test
    fun transformGoogleLink_invalidData() {
        val link =
            menuFragment.transformGoogleDriveLink("https://drive.google.com/file/k/1EMoIbb4OtRbR-ZmTuE3-zUqpd2Iq9_yJ")
        assertEquals("", link)
    }

    @Test
    fun transformGoogleLink_emptyData() {
        val link = menuFragment.transformGoogleDriveLink("")
        assertEquals("", link)
    }
}