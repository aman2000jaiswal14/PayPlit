package com.aman.payplit.data.repository

import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.data.remote.PayPlitApiService
import com.aman.payplit.data.remote.dto.CreateUserRequest
import com.aman.payplit.data.remote.dto.LoginRequest
import com.aman.payplit.data.remote.dto.UserDto
import com.aman.payplit.domain.repository.UserRepository
import com.aman.payplit.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: PayPlitApiService,
    private val sessionManager: SessionManager // 🔥 Added
) : UserRepository {

    override suspend fun login(email: String, password: String): Flow<Resource<UserDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.loginUser(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.status == "success") {
                val userDto = response.body()?.data
                if (userDto != null) {
                    sessionManager.saveUserId(userDto.userId) // 🔥 Save for later!
                    emit(Resource.Success(userDto))
                } else {
                    emit(Resource.Error("Data missing"))
                }
            } else {
                emit(Resource.Error("Invalid credentials"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Check internet connection"))
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Flow<Resource<UserDto>> = flow {
        emit(Resource.Loading())
        try {
            val request = CreateUserRequest(name, email, password)
            val response = api.createUser(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                response.body()?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("User created but data missing"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Signup failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Server error: ${e.localizedMessage}"))
        }
    }
}