package com.aman.payplit.data.local.dao

import androidx.room.*
import com.aman.payplit.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Upsert
    suspend fun upsertGroups(groups: List<GroupEntity>)

    // 🔥 INDUSTRY FIX: Only fetch groups where the logged-in user is a member
    @Query("SELECT * FROM group_table WHERE groupMembers LIKE '%' || :userId || '%'")
    fun getGroupsForUser(userId: String): Flow<List<GroupEntity>>

    // 🔥 Used for the "Sync" check
    @Query("SELECT * FROM group_table WHERE groupMembers LIKE '%' || :userId || '%'")
    suspend fun getGroupsForUserOnce(userId: String): List<GroupEntity>

    @Query("DELETE FROM group_table WHERE groupId = :groupId")
    suspend fun deleteGroupById(groupId: String)

    // 🔥 CRITICAL: Call this on Logout to ensure User B doesn't see User A's data
    @Query("DELETE FROM group_table")
    suspend fun clearAll()
}