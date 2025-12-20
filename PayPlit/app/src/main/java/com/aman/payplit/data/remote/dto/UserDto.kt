package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("mobileNo") val mobileNo: String? = null,
    @SerializedName("groupIds") val groupIds: List<String> = emptyList()
)