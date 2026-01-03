package com.aman.payplit.di

import android.content.Context
import androidx.room.Room
import com.aman.payplit.data.local.PayPlitDatabase
import com.aman.payplit.data.local.dao.ExpenseDao
import com.aman.payplit.data.local.dao.GroupDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PayPlitDatabase {
        return Room.databaseBuilder(
            context,
            PayPlitDatabase::class.java,
            "payplit_db"
        ).build()
    }

    @Provides
    fun provideGroupDao(db: PayPlitDatabase): GroupDao = db.groupDao

    @Provides
    fun provideExpenseDao(db: PayPlitDatabase): ExpenseDao = db.expenseDao
}