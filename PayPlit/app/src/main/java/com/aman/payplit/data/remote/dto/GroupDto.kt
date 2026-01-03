package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("groupName") val groupName: String?,
    @SerializedName("groupMembers") val groupMembers: List<String>?,
    @SerializedName("memberNames") val memberNames: Map<String, String>?, // 🔥 New Denormalized Field
    @SerializedName("groupItems") val groupItems: List<String>?,
    @SerializedName("groupGraph") val groupGraph: Map<String, Map<String, Double>>?
)