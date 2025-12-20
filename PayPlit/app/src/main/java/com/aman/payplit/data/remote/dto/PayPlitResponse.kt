package com.aman.payplit.data.remote.dto

// This matches your safe_json_response function in Flask
data class PayPlitResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)