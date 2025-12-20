package com.aman.payplit.data.repository

import com.aman.payplit.data.remote.PayPlitApiService
import com.aman.payplit.data.remote.dto.CreateGroupRequest
import com.aman.payplit.data.remote.dto.AddMemberRequest
import com.aman.payplit.data.remote.dto.GroupDto
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: PayPlitApiService
) : GroupRepository {

    override suspend fun getGroups(userId: String): Flow<Resource<List<GroupDto>>> = flow {
        emit(Resource.Loading())
        try {
            val idResponse = api.getUserGroupIds(mapOf("userId" to userId))
            val groupIds = idResponse.body()?.data?.groups ?: emptyList()

            if (groupIds.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@flow
            }

            coroutineScope {
                val groupDetailsList = groupIds.map { id ->
                    async {
                        try {
                            val detailResponse = api.getGroupById(mapOf("groupId" to id))
                            detailResponse.body()?.data?.group
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                emit(Resource.Success(groupDetailsList))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Check internet or server status"))
        }
    }

    override suspend fun createGroup(name: String, members: List<String>): Flow<Resource<GroupDto>> = flow {
        emit(Resource.Loading())
        try {
            val request = CreateGroupRequest(groupName = name, groupMembers = members)
            val response = api.createGroup(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                response.body()?.data?.group?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Group created but no data returned"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to create group"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }

    override suspend fun addMember(groupId: String, email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.addMemberToGroup(AddMemberRequest(groupId, email))
            if (response.isSuccessful) {
                val message = response.body()?.get("message") ?: "Member added"
                emit(Resource.Success(message))
            } else {
                val errorMsg = if (response.code() == 404) "User with this email not found" else "Failed to add member"
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getSettlementSummary(groupId: String, userId: String): Flow<Resource<Pair<List<String>, List<String>>>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                // 🔥 FIXED: renamed to getGlobalExpenseDetail
                val globalDef = async { api.getGlobalExpenseDetail(groupId) }

                // 🔥 FIXED: renamed to getCurrentUserExpenseDetail
                val personalDef = async {
                    api.getCurrentUserExpenseDetail(mapOf("groupId" to groupId, "currentUserId" to userId))
                }

                val globalResponse = globalDef.await()
                val personalResponse = personalDef.await()

                val globalLines = globalResponse.body()?.expenseDetail ?: emptyList()
                val personalLines = personalResponse.body()?.expenseDetail ?: emptyList()

                emit(Resource.Success(Pair(globalLines, personalLines)))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to calculate settlements"))
        }
    }

    override suspend fun deleteGroup(groupId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteGroup(mapOf("groupId" to groupId))
            if (response.isSuccessful && response.body()?.status == "success") {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Failed to delete group"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }
}