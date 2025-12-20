package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("groupId") val groupId: String,
    // Add '?' to make them nullable
    @SerializedName("groupName") val groupName: String?,
    @SerializedName("groupMembers") val groupMembers: List<String>?,
    @SerializedName("groupItems") val groupItems: List<String>?,
    @SerializedName("groupGraph") val groupGraph: Map<String, Map<String, Double>>?
)