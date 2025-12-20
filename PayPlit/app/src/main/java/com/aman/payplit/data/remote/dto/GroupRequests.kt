package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Matches your Python API: data.get("groupMembers")
 */
data class CreateGroupRequest(
    @SerializedName("groupName") val groupName: String,
    @SerializedName("groupMembers") val groupMembers: List<String>,
    // You can add other fields like groupGraph initialized to empty map if your API needs it
    @SerializedName("groupGraph") val groupGraph: Map<String, Map<String, Double>> = emptyMap()
)