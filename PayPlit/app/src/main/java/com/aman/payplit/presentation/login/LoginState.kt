package com.aman.payplit.presentation.login

import com.aman.payplit.data.remote.dto.UserDto

data class LoginState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)