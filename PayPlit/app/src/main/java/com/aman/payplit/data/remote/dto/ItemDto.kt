package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Industry Grade DTO: Maps the backend expense item to Kotlin.
 * Note: Fields are nullable (?) to prevent crashes if the backend
 * sends incomplete data.
 */
data class ItemDto(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("itemTotalAmount") val itemTotalAmount: Double,
    @SerializedName("itemGroupId") val itemGroupId: String,
    @SerializedName("itemDateUpdate") val itemDateUpdate: String,
    @SerializedName("itemTimeUpdate") val itemTimeUpdate: String,

    // IDs
    @SerializedName("itemPayer") val itemPayer: List<String>,
    @SerializedName("itemSpliter") val itemSpliter: List<String>,
    @SerializedName("itemSpliterValue") val itemSpliterValue: List<Double>? = emptyList(),

    // 🔥 NEW: Denormalized Names
    @SerializedName("itemPayerNames") val itemPayerNames: List<String> = emptyList(),
    @SerializedName("itemSpliterNames") val itemSpliterNames: List<String> = emptyList(),

    @SerializedName("itemType") val itemType: String? = "EXPENSE"
)