package com.aman.payplit.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Matches your Python API: data['groupId'] and data['memberEmail']
 */
data class AddMemberRequest(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("memberEmail") val memberEmail: String
)