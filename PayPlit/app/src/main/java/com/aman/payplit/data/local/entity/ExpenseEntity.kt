package com.aman.payplit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val itemId: String,
    val groupId: String,
    val itemName: String,
    val amount: Double,
    val payerId: String, // We'll store the primary payer ID
    val payerNames: List<String>, // 🔥 Store the list of names
    val splitterIds: List<String>,
    val splitterNames: List<String>, // 🔥 Store the list of names
    val splitterValues: List<Double>,
    val date: String,
    val time: String,
    val itemType: String
)