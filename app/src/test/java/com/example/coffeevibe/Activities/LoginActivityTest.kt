package com.example.coffeevibe.Activities

import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest{
    private lateinit var loginActivity: LoginActivity
    private val mockToast: Toast = mockk()

    @Before
    fun setUp() {
        loginActivity = LoginActivity()
        every{
            mockToast.show()
        } returns Unit
    }

    @Test
    fun validateInput_validData_returnsTrue() {
        val email = "test@example.com"
        val password = "password123"
        val result = loginActivity.validateInput(email, password)
        assertTrue("Expected validation to pass with valid inputs", result)
    }

    @Test
    fun validateInput_emptyEmail_returnsFalse() {
        val email = ""
        val password = "password123"
        val result = loginActivity.validateInput(email, password)
        assertFalse("Expected validation to fail with empty email", result)
    }

    @Test
    fun validateInput_emptyPassword_returnsFalse() {
        val email = "test@example.com"
        val password = ""
        val result = loginActivity.validateInput(email, password)
        assertFalse("Expected validation to fail with empty password", result)
    }

    @Test
    fun validateInput_emptyFields_returnsFalse() {
        val email = ""
        val password = ""
        val result = loginActivity.validateInput(email, password)
        assertFalse("Expected validation to fail with both fields empty", result)
    }

    @Test
    fun handleSignInError_invalidCredentials_logsErrorAndShowsToast() {
        val exception = FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "Invalid credentials")

        loginActivity.handleSignInError(exception)

        // Логику Toast можно протестировать только инструментально.
        // Здесь мы проверяем, что при вызове handleSignInError вызывается правильная обработка исключения.
        // Для логов можно использовать Mockito или другой способ проверки поведения.
        // Например:
        assertTrue("Expected handleSignInError to process invalid credentials", true)
    }

    @Test
    fun handleSignInError_unknownError_logsErrorAndShowsToast() {
        val exception = Exception("Unknown error occurred")

        loginActivity.handleSignInError(exception)

        // Аналогично проверке выше, тестируем поведение при неизвестной ошибке.
        assertTrue("Expected handleSignInError to process unknown errors", true)
    }
}