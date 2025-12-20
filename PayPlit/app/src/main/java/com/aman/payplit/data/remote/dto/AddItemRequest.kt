package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Industry Grade DTO: Matches your Python 'item_dict' keys exactly.
 */
data class AddItemRequest(
    @SerializedName("itemName")
    val itemName: String,

    @SerializedName("itemDateUpdate")
    val itemDateUpdate: String,

    @SerializedName("itemTimeUpdate")
    val itemTimeUpdate: String,

    @SerializedName("itemTotalAmount")
    val itemTotalAmount: Double,

    @SerializedName("itemPayer")
    val itemPayer: List<String>, // Note: API expects a list: data["itemPayer"][0]

    @SerializedName("itemSpliter")
    val itemSpliter: List<String>,

    @SerializedName("itemSpliterValue")
    val itemSpliterValue: List<Double>,

    @SerializedName("itemGroupId")
    val itemGroupId: String
)