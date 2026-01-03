package com.example.refconnect.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.User
import com.example.refconnect.model.UserRole
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,  // Start immediately
            initialValue = false
        )

    // Get actual login state from DataStore (bypasses StateFlow initial value)
    suspend fun checkLoginState(): Boolean {
        return repository.isLoggedIn.first()
    }

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginRole by mutableStateOf(UserRole.SEEKER)
    var loginError by mutableStateOf("")
    var isLoggingIn by mutableStateOf(false)

    var signupName by mutableStateOf("")
    var signupEmail by mutableStateOf("")
    var signupPassword by mutableStateOf("")
    var signupConfirmPassword by mutableStateOf("")
    var signupRole by mutableStateOf(UserRole.SEEKER)
    var signupError by mutableStateOf("")
    var isSigningUp by mutableStateOf(false)

    fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun canLogin(): Boolean {
        return validateEmail(loginEmail) && validatePassword(loginPassword) && !isLoggingIn
    }

    fun login(onSuccess: (User) -> Unit) {
        loginError = ""
        if (!canLogin()) {
            loginError = "Please enter valid email and password (min 6 characters)"
            return
        }

        isLoggingIn = true
        viewModelScope.launch {
            try {
                val user = repository.login(loginEmail, loginPassword, loginRole)
                if (user != null) {
                    onSuccess(user)
                } else {
                    loginError = "Invalid email or password"
                }
            } catch (e: IllegalStateException) {
                loginError = e.message ?: "Login failed"
            } catch (e: Exception) {
                loginError = "Login failed: ${e.message}"
            } finally {
                isLoggingIn = false
            }
        }
    }

    fun canSignup(): Boolean {
        return signupName.isNotEmpty() &&
                validateEmail(signupEmail) &&
                validatePassword(signupPassword) &&
                signupPassword == signupConfirmPassword &&
                !isSigningUp
    }

    fun signup(onSuccess: (User) -> Unit) {
        signupError = ""
        when {
            signupName.isEmpty() -> signupError = "Name is required"
            !validateEmail(signupEmail) -> signupError = "Please enter a valid email"
            !validatePassword(signupPassword) -> signupError = "Password must be at least 6 characters"
            signupPassword != signupConfirmPassword -> signupError = "Passwords do not match"
            else -> {
                isSigningUp = true
                viewModelScope.launch {
                    try {
                        val user = repository.signup(
                            signupName,
                            signupEmail,
                            signupPassword,
                            signupRole
                        )
                        onSuccess(user)
                    } catch (e: IllegalStateException) {
                        signupError = e.message ?: "Signup failed"
                    } catch (e: Exception) {
                        signupError = "Signup failed: ${e.message}"
                    } finally {
                        isSigningUp = false
                    }
                }
            }
        }
    }

    fun resetLoginFields() {
        loginEmail = ""
        loginPassword = ""
        loginRole = UserRole.SEEKER
        loginError = ""
    }

    fun resetSignupFields() {
        signupName = ""
        signupEmail = ""
        signupPassword = ""
        signupConfirmPassword = ""
        signupRole = UserRole.SEEKER
        signupError = ""
    }
}
