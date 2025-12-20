package com.aman.payplit.presentation.login

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
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter both email and password")
            return
        }

        viewModelScope.launch {
            repository.login(email, password).onEach { result -> // 🔥 Passing password
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = LoginState(
                            user = result.data,
                            isLoginSuccess = true
                        )
                    }
                    is Resource.Error -> {
                        _state.value = LoginState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                }
            }.launchIn(this)
        }
    }
}


