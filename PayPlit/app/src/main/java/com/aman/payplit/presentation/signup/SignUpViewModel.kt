package com.aman.payplit.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.domain.repository.UserRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    // Industry Standard: Regex moved to ViewModel
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")

    fun onSignUp(name: String, phone: String, email: String, password: String) {
        // Validation Logic
        if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "All fields are required")
            return
        }
        if (!emailRegex.matches(email)) {
            _state.value = _state.value.copy(error = "Invalid email format")
            return
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "Password too short")
            return
        }

        viewModelScope.launch {
            // Note: We need to update UserRepository to accept mobileNo too
            repository.signUp(name, email, password).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = SignUpState(isLoading = true)
                    is Resource.Success -> _state.value = SignUpState(isSignUpSuccess = true)
                    is Resource.Error -> _state.value = SignUpState(error = result.message)
                }
            }.launchIn(this)
        }
    }
}