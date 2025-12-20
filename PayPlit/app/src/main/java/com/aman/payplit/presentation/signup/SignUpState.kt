package com.aman.payplit.presentation.signup

import com.aman.payplit.data.remote.dto.UserDto

data class SignUpState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val isSignUpSuccess: Boolean = false
)