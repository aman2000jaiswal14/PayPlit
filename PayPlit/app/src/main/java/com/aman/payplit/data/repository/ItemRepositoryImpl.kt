package com.aman.payplit.data.repository

import com.aman.payplit.data.remote.PayPlitApiService
import com.aman.payplit.data.remote.dto.AddItemRequest
import com.aman.payplit.data.remote.dto.ItemDto
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.aman.payplit.data.remote.dto.UserDto

class ItemRepositoryImpl @Inject constructor(
    private val api: PayPlitApiService
) : ItemRepository {

    override suspend fun getGroupData(
        groupId: String,
        userId: String
    ): Flow<Resource<Pair<List<ItemDto>, List<String>>>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                // Fetch items (Returns raw List<ItemDto>)
                val itemsDeferred = async { api.getGroupItems(groupId) }

                // Fetch summary (Returns ExpenseDetailResponse)
                val summaryDeferred = async {
                    api.getCurrentUserExpenseDetail(mapOf("groupId" to groupId, "currentUserId" to userId))
                }

                val itemsRes = itemsDeferred.await()
                val summaryRes = summaryDeferred.await()

                val items = itemsRes.body() ?: emptyList()
                val summary = summaryRes.body()?.expenseDetail ?: emptyList()

                emit(Resource.Success(Pair(items, summary)))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Server Error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Check internet connection"))
        }
    }

    override suspend fun deleteItem(itemId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteItem(itemId)
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Failed to delete item"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }



    override suspend fun getGroupMembers(groupId: String): Flow<Resource<List<UserDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getGroupMembersDetail(groupId)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error fetching members"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    override suspend fun createExpense(request: AddItemRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createItem(request)
            if (response.isSuccessful) {
                // Read raw string "item created"
                val result = response.body()?.string() ?: "Success"
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Failed to create item: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Connection error"))
        }
    }

    // Inside ItemRepositoryImpl.kt

    override suspend fun getItemDetails(itemId: String): Flow<Resource<ItemDto>> = flow {
        emit(Resource.Loading())
        try {
            // 1. Call the API
            val response = api.getItemById(itemId)

            if (response.isSuccessful) {
                // 2. Extract from the wrapper {"data": {"item": {...}}}
                val item = response.body()?.data?.item
                if (item != null) {
                    emit(Resource.Success(item))
                } else {
                    emit(Resource.Error("Item data is null"))
                }
            } else {
                emit(Resource.Error("Failed to fetch item: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network Error"))
        }
    }
}