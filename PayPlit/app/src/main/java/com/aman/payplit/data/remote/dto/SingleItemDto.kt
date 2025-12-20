package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SingleItemDto(
    @SerializedName("item") val item: ItemDto?
)