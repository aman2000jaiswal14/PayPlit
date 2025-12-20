package com.aman.payplit.di

import com.aman.payplit.data.repository.GroupRepositoryImpl
import com.aman.payplit.data.repository.ItemRepositoryImpl
import com.aman.payplit.data.repository.UserRepositoryImpl
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindItemRepository(
        itemRepositoryImpl: ItemRepositoryImpl
    ): ItemRepository
}