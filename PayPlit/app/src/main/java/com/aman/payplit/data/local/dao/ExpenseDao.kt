package com.aman.payplit.data.local.dao

import androidx.room.*
import com.aman.payplit.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Upsert
    suspend fun upsertExpenses(expenses: List<ExpenseEntity>)

    // This is the reactive stream for the UI
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC, time DESC")
    fun getExpensesForGroup(groupId: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses WHERE itemId = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("DELETE FROM expenses WHERE groupId = :groupId")
    suspend fun deleteExpensesByGroupId(groupId: String)

    @Query("DELETE FROM expenses")
    suspend fun clearAll()

    @Query("DELETE FROM expenses WHERE itemId NOT IN (SELECT itemId FROM expenses WHERE groupId = :groupId ORDER BY date DESC LIMIT 50) AND groupId = :groupId")
    suspend fun trimOldExpenses(groupId: String)
}