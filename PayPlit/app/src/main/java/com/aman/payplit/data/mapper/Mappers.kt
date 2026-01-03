package com.aman.payplit.data.mapper

import com.aman.payplit.data.local.entity.ExpenseEntity
import com.aman.payplit.data.local.entity.GroupEntity
import com.aman.payplit.data.remote.dto.GroupDto
import com.aman.payplit.data.remote.dto.ItemDto

// Convert Group API Data to Room Entity
fun GroupDto.toEntity(): GroupEntity {
    return GroupEntity(
        groupId = groupId,
        groupName = groupName ?: "Unnamed",
        groupMembers = groupMembers ?: emptyList(),
        memberNames = memberNames ?: emptyMap(),
        groupGraph = groupGraph ?: emptyMap()
    )
}

// Convert Item API Data to Room Entity
fun ItemDto.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        itemId = itemId,
        groupId = itemGroupId,
        itemName = itemName,
        amount = itemTotalAmount,
        payerId = itemPayer.firstOrNull() ?: "",
        payerNames = itemPayerNames, // 🔥 Map the list
        splitterIds = itemSpliter,
        splitterNames = itemSpliterNames, // 🔥 Map the list
        splitterValues = itemSpliterValue ?: emptyList(),
        date = itemDateUpdate,
        time = itemTimeUpdate,
        itemType = itemType ?: "EXPENSE"
    )
}

// Convert Room Entity back to Item DTO (for UI consistency)
fun ExpenseEntity.toDto(): ItemDto {
    return ItemDto(
        itemId = itemId,
        itemName = itemName,
        itemTotalAmount = amount,
        itemGroupId = groupId,
        itemDateUpdate = date,
        itemTimeUpdate = time,
        itemPayer = listOf(payerId),
        itemSpliter = splitterIds,
        itemSpliterValue = splitterValues,
        itemPayerNames = payerNames, // 🔥 Map back to list
        itemSpliterNames = splitterNames, // 🔥 Map back to list
        itemType = itemType
    )
}