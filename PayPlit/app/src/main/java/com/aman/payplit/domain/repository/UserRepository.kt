package com.aman.payplit.domain.repository

import com.aman.payplit.data.remote.dto.UserDto
import com.aman.payplit.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Flow<Resource<UserDto>>
    suspend fun signUp(name: String, email: String, password: String): Flow<Resource<UserDto>>
}