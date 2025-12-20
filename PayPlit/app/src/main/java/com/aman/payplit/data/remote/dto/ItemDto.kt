package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Industry Grade DTO: Maps the backend expense item to Kotlin.
 * Note: Fields are nullable (?) to prevent crashes if the backend
 * sends incomplete data.
 */
data class ItemDto(
    @SerializedName("itemId")
    val itemId: String,

    @SerializedName("itemName")
    val itemName: String,

    @SerializedName("itemTotalAmount")
    val itemTotalAmount: Double,

    @SerializedName("itemPayer")
    val itemPayer: List<String> = emptyList(),

    @SerializedName("itemSpliter")
    val itemSpliter: List<String> = emptyList(),

    @SerializedName("itemSpliterValue")
    val itemSpliterValue: List<Double> = emptyList(),

    @SerializedName("itemGroupId")
    val itemGroupId: String,

    @SerializedName("itemDateUpdate")
    val itemDateUpdate: String,

    @SerializedName("itemTimeUpdate")
    val itemTimeUpdate: String
)