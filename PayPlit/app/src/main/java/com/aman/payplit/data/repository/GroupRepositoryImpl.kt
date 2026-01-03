package com.aman.payplit.data.repository

import com.aman.payplit.data.local.dao.GroupDao
import com.aman.payplit.data.mapper.toEntity
import com.aman.payplit.data.remote.PayPlitApiService
import com.aman.payplit.data.remote.dto.*
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: PayPlitApiService,
    private val groupDao: GroupDao // 🔥 Room DAO
) : GroupRepository {

    override suspend fun getGroups(userId: String): Flow<Resource<List<GroupDto>>> = flow {
        emit(Resource.Loading())

        // 1. Fetch IDs assigned to this user from Render
        try {
            val idResponse = api.getUserGroupIds(mapOf("userId" to userId))
            val groupIds = idResponse.body()?.data?.groups ?: emptyList()

            if (groupIds.isNotEmpty()) {
                coroutineScope {
                    val groupDetailsList = groupIds.map { id ->
                        async {
                            try {
                                val detailResponse = api.getGroupById(mapOf("groupId" to id))
                                detailResponse.body()?.data?.group
                            } catch (e: Exception) { null }
                        }
                    }.awaitAll().filterNotNull()

                    // 2. Sync to Room
                    groupDao.upsertGroups(groupDetailsList.map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            // Network fail: Fallback to Room is automatic in Step 3
        }

        // 3. 🔥 SSOT: Only return groups from Room that belong to THIS user
        try {
            val localGroups = groupDao.getGroupsForUserOnce(userId).map { entity ->
                GroupDto(
                    groupId = entity.groupId,
                    groupName = entity.groupName,
                    groupMembers = entity.groupMembers,
                    memberNames = entity.memberNames,
                    groupItems = emptyList(), // Items are handled in ItemRepository
                    groupGraph = entity.groupGraph
                )
            }
            emit(Resource.Success(localGroups))
        } catch (e: Exception) {
            emit(Resource.Error("Local Database Error"))
        }
    }
    override suspend fun createGroup(name: String, members: List<String>): Flow<Resource<GroupDto>> = flow {
        emit(Resource.Loading())
        try {
            val request = CreateGroupRequest(groupName = name, groupMembers = members)
            val response = api.createGroup(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                response.body()?.data?.group?.let {
                    groupDao.upsertGroups(listOf(it.toEntity())) // 🔥 Sync to Room
                    emit(Resource.Success(it))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error"))
        }
    }

    override suspend fun addMember(groupId: String, email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.addMemberToGroup(AddMemberRequest(groupId, email))

            if (response.isSuccessful) {
                // Success Case (200 OK)
                emit(Resource.Success("Member added successfully"))
            } else {
                // Error Case (404, 400, 500, etc.)
                val errorMsg = when (response.code()) {
                    404 -> "User with this email not found"
                    400 -> "User is already in this group"
                    else -> "Server error: ${response.code()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: java.io.IOException) {
            // Network failures (e.g., Airplane mode, Render server timed out)
            emit(Resource.Error("Check your internet connection"))
        } catch (e: Exception) {
            // Any other logic/parsing crash
            emit(Resource.Error("An unexpected error occurred"))
        }
    }

    override suspend fun getSettlementSummary(groupId: String, userId: String): Flow<Resource<Pair<List<String>, List<String>>>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                val globalDef = async { api.getGlobalExpenseDetail(groupId) }
                val personalDef = async {
                    api.getCurrentUserExpenseDetail(mapOf("groupId" to groupId, "currentUserId" to userId))
                }
                val globalLines = globalDef.await().body()?.expenseDetail ?: emptyList()
                val personalLines = personalDef.await().body()?.expenseDetail ?: emptyList()
                emit(Resource.Success(Pair(globalLines, personalLines)))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Calculation failed"))
        }
    }

    override suspend fun deleteGroup(groupId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteGroup(mapOf("groupId" to groupId))
            if (response.isSuccessful) {
                groupDao.deleteGroupById(groupId) // 🔥 Remove from Room
                emit(Resource.Success(Unit))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Delete failed"))
        }
    }
}