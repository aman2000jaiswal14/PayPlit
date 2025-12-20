package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

// This matches your Python wrap_data("groups", group_ids)
data class UserGroupsDto(
    @SerializedName("groups") val groups: List<String> = emptyList()
)