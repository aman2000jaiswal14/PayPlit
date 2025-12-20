package com.aman.payplit.domain.repository

import com.aman.payplit.data.remote.dto.AddItemRequest
import com.aman.payplit.data.remote.dto.ItemDto
import com.aman.payplit.data.remote.dto.UserDto
import com.aman.payplit.util.Resource
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun getGroupData(
        groupId: String,
        userId: String
    ): Flow<Resource<Pair<List<ItemDto>, List<String>>>>

    // Fetches member names for the split screen
    suspend fun getGroupMembers(groupId: String): Flow<Resource<List<UserDto>>>

    // Submits the new expense
    suspend fun createExpense(request: AddItemRequest): Flow<Resource<String>>

    suspend fun deleteItem(itemId: String): Flow<Resource<Unit>>
    suspend fun getItemDetails(itemId: String): Flow<Resource<ItemDto>>
}