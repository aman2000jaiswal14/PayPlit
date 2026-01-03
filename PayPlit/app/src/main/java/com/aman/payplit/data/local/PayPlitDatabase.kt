package com.aman.payplit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aman.payplit.data.local.dao.ExpenseDao
import com.aman.payplit.data.local.dao.GroupDao
import com.aman.payplit.data.local.entity.ExpenseEntity
import com.aman.payplit.data.local.entity.GroupEntity

@Database(
    entities = [
        GroupEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PayPlitConverters::class) // 🔥 Ensure PayPlitConverters is imported!
abstract class PayPlitDatabase : RoomDatabase() {
    abstract val groupDao: GroupDao
    abstract val expenseDao: ExpenseDao
}