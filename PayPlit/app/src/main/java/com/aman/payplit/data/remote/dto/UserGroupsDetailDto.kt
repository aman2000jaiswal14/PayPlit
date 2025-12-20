package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

// Matches your Python: wrap_data("groups", result) where result is a list of dicts
data class UserGroupsDetailDto(
    @SerializedName("groups") val groups: List<GroupDto> = emptyList()
)