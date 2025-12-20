package com.aman.payplit.data.remote.dto
import com.google.gson.annotations.SerializedName

data class SingleGroupDto(
    @SerializedName("group") val group: GroupDto?
)