package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

// Matches your Python: data.get("email"), data.get("password")
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String // 🔥 Added

)

// Matches your Python create_user logic
data class CreateUserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("mobileNo") val mobileNo: String = "",
    @SerializedName("groupIds") val groupIds: List<String> = emptyList()
)