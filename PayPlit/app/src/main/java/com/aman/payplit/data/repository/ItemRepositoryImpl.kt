package com.aman.payplit.data.repository

import com.aman.payplit.data.local.dao.ExpenseDao
import com.aman.payplit.data.mapper.toDto
import com.aman.payplit.data.mapper.toEntity
import com.aman.payplit.data.remote.PayPlitApiService
import com.aman.payplit.data.remote.dto.*
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val api: PayPlitApiService,
    private val expenseDao: ExpenseDao
) : ItemRepository {

    /**
     * 🔥 INDUSTRY STRATEGY: Room First, API Second.
     * This provides a Triple: (Items List, Balance Summary, Debt Graph Map)
     */
    override suspend fun getGroupData(
        groupId: String,
        userId: String
    ): Flow<Resource<Triple<List<ItemDto>, List<String>, Map<String, Map<String, Double>>>>> = flow {
        emit(Resource.Loading())

        // 1. Fetch Metadata (Summary and Graph) from API in parallel.
        // These are computed values and shouldn't be stored in the local expenses table.
        val metadata = try {
            coroutineScope {
                val summaryDef = async {
                    api.getCurrentUserExpenseDetail(mapOf("groupId" to groupId, "currentUserId" to userId))
                }
                val groupDef = async {
                    api.getGroupById(mapOf("groupId" to groupId))
                }

                val summaryRes = summaryDef.await()
                val groupRes = groupDef.await()

                Pair(
                    summaryRes.body()?.expenseDetail ?: emptyList(),
                    groupRes.body()?.data?.group?.groupGraph ?: emptyMap()
                )
            }
        } catch (e: Exception) {
            // Fallback to empty if API fails (Server sleeping)
            Pair(emptyList(), emptyMap())
        }

        // 2. 🔥 SSOT: Start the Room Stream
        // We use .onStart to trigger the network sync for items without blocking the Room data.
        val localFlow = expenseDao.getExpensesForGroup(groupId)
            .map { entities ->
                val dtos = entities.map { it.toDto() }
                Resource.Success(Triple(dtos, metadata.first, metadata.second))
            }
            .onStart {
                // Background sync: Fetch newest items from server and update local DB
                syncItemsFromServer(groupId)
            }

        emitAll(localFlow)
    }

    private suspend fun syncItemsFromServer(groupId: String) {
        try {
            // Fetch first page (offset 0) to update local cache
            val response = api.getGroupItems(mapOf("groupId" to groupId, "limit" to 10, "offset" to 0))
            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                // Room will update, which triggers the localFlow above automatically
                expenseDao.upsertExpenses(items.map { it.toEntity() })
                expenseDao.trimOldExpenses(groupId)
            }
        } catch (e: Exception) {
            // Fails silently to maintain offline experience
        }
    }

    override suspend fun getItemsPaginated(groupId: String, limit: Int, offset: Int): Flow<Resource<List<ItemDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getGroupItems(mapOf("groupId" to groupId, "limit" to limit, "offset" to offset))
            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                expenseDao.upsertExpenses(items.map { it.toEntity() }) // Cache the new page
                emit(Resource.Success(items))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network Error"))
        }
    }

    /**
     * 🔥 SETTLE UP: Create a cash payment between two users.
     */
    override suspend fun createPayment(
        groupId: String,
        payerId: String,
        receiverId: String,
        amount: Double
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val body = mapOf(
                "itemGroupId" to groupId,
                "itemPayer" to listOf(payerId),
                "itemSpliter" to listOf(receiverId),
                "itemTotalAmount" to amount
            )
            val response = api.createPayment(body)
            if (response.isSuccessful) {
                emit(Resource.Success("Success"))
            } else {
                emit(Resource.Error("Failed to record payment"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Check connection"))
        }
    }

    override suspend fun createExpense(request: AddItemRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createItem(request)
            if (response.isSuccessful) emit(Resource.Success("Success"))
            else emit(Resource.Error("Failed"))
        } catch (e: Exception) { emit(Resource.Error("Error")) }
    }

    override suspend fun deleteItem(itemId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteItem(itemId)
            if (response.isSuccessful) {
                expenseDao.deleteItemById(itemId) // Cleanup Room
                emit(Resource.Success(Unit))
            }
        } catch (e: Exception) { emit(Resource.Error("Error")) }
    }

    override suspend fun getSummary(groupId: String, userId: String): Flow<Resource<List<String>>> = flow {
        try {
            val res = api.getCurrentUserExpenseDetail(mapOf("groupId" to groupId, "currentUserId" to userId))
            if (res.isSuccessful) emit(Resource.Success(res.body()?.expenseDetail ?: emptyList()))
        } catch (e: Exception) { emit(Resource.Error("Error")) }
    }

    override suspend fun getItemDetails(itemId: String): Flow<Resource<ItemDto>> = flow {
        try {
            val response = api.getItemById(itemId)
            val item = response.body()?.data?.item
            if (item != null) emit(Resource.Success(item))
        } catch (e: Exception) { emit(Resource.Error("Error")) }
    }

    override suspend fun getGroupMembers(groupId: String): Flow<Resource<List<UserDto>>> = flow {
        try {
            val response = api.getGroupMembersDetail(groupId)
            if (response.isSuccessful) emit(Resource.Success(response.body() ?: emptyList()))
        } catch (e: Exception) { emit(Resource.Error("Error")) }
    }
}