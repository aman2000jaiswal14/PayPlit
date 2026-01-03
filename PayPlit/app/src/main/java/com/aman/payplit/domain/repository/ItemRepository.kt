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
    ): Flow<Resource<Triple<List<ItemDto>, List<String>, Map<String, Map<String, Double>>>>>
    suspend fun getItemsPaginated(groupId: String, limit: Int, offset: Int): Flow<Resource<List<ItemDto>>>
    suspend fun deleteItem(itemId: String): Flow<Resource<Unit>>
    suspend fun getGroupMembers(groupId: String): Flow<Resource<List<UserDto>>>
    suspend fun createExpense(request: AddItemRequest): Flow<Resource<String>>
    suspend fun getItemDetails(itemId: String): Flow<Resource<ItemDto>>
    suspend fun getSummary(groupId: String, userId: String): Flow<Resource<List<String>>>

    suspend fun createPayment(
        groupId: String,
        payerId: String,
        receiverId: String,
        amount: Double
    ): Flow<Resource<String>>

}