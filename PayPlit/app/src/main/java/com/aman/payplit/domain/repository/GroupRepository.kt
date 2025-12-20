package com.aman.payplit.domain.repository

import com.aman.payplit.data.remote.dto.GroupDto
import com.aman.payplit.util.Resource
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroups(userId: String): Flow<Resource<List<GroupDto>>>
    // domain/repository/GroupRepository.kt
    suspend fun createGroup(name: String, members: List<String>): Flow<Resource<GroupDto>>

    suspend fun addMember(groupId: String, email: String): Flow<Resource<String>>

    suspend fun getSettlementSummary(groupId: String, userId: String): Flow<Resource<Pair<List<String>, List<String>>>>
    suspend fun deleteGroup(groupId: String): Flow<Resource<Unit>>

}

