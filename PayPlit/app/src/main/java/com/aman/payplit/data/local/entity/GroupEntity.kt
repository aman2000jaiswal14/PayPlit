package com.aman.payplit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// 🔥 Changed from "groups" to "group_table" to avoid SQL Keyword conflict
@Entity(tableName = "group_table")
data class GroupEntity(
    @PrimaryKey val groupId: String,
    val groupName: String,
    val groupMembers: List<String>,
    val memberNames: Map<String, String>,
    val groupGraph: Map<String, Map<String, Double>>
)